package org.komparator.mediator.domain;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.ProductView;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.Collection;

import pt.ulisboa.tecnico.sdis.ws.cli.*;
import org.komparator.mediator.ws.LifeProof;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import java.util.Date;

public class Mediator{


	private volatile int numberSupllier = 1;

	private Map<String,SupplierClient> listSuppliers = new ConcurrentHashMap<>();

	private List<Cart> cartlist = new CopyOnWriteArrayList<Cart>();

	private List<String> operationscart = new CopyOnWriteArrayList<String>();

	private Map<String,ShoppingResult> shoppingHistory = new ConcurrentHashMap<>();

	private AtomicInteger shoppingId = new AtomicInteger(0);

	private Date lastTimeStamp;


	private Mediator() {
	}


	private static class MediatorSingletonHolder {
	private static final Mediator INSTANCE = new Mediator();
	}

	public static synchronized Mediator getInstance() {
		return MediatorSingletonHolder.INSTANCE;
	}

	public void reset(){
		listSuppliers.clear();
		cartlist.clear();
		operationscart.clear();
		shoppingHistory.clear();
		numberSupllier=1;
		shoppingId.set(0);
	}

	public List<Item> searchItems(String descText) throws BadText_Exception{
		lookForSuppliers();
		int i=1;
		String id;
		ArrayList<Item> itens = new ArrayList<>();
		int counter;
			for (;i<=listSuppliers.size();i++){
				id= "T59_Supplier"+ i;
				List<ProductView> product = listSuppliers.get(id).searchProducts(descText);
				if (product!=null){
				List<Item> item2 = new ArrayList();
				for(ProductView prd: product)
					item2.add(new Item(prd,id));
				for(Item item : item2){
					if(itens.isEmpty()){
						itens.add(item);
					}else {
						int strcmp;
						counter=0;
					while(counter<itens.size()){
						strcmp = item.getProductId().compareTo(itens.get(counter).getProductId());
						if(strcmp<0){
							break;
						}
						if(strcmp==0)
							if((itens.get(counter).getPrice() - item.getPrice())>0){
							break;
						}
						counter++;
					}
					itens.add(counter,item);
					}
				}
				}
			}
		return itens;
	}


	public List<Item> getItems(String productId) throws BadProductId_Exception {
		lookForSuppliers();
		int i=1;
		
		String id;
		ArrayList<Item> itens = new ArrayList<>();
		
			int counter;
			for (;i<=listSuppliers.size();i++){
				id= "T59_Supplier"+ i;
				ProductView product = listSuppliers.get(id).getProduct(productId);
				if (product==null){
					i++;
					continue;
				}
				Item item = new Item(product,id);
				if(itens.isEmpty()){
					itens.add(item);
				}else {
					counter=0;
				while(counter<itens.size()){
					if((itens.get(counter).getPrice() - item.getPrice())>0){
						break;
					}
					counter++;
				}
				itens.add(counter,item);
				}
				
			}
		return itens;
	}



	public Cart addToCart(String cartId, String idSup, String idProd, int itemQty,String idoperation) throws InvalidQuantityException, NotItemException,NotEnoughProductsException {
		lookForSuppliers();
		if(operationscart.contains(idoperation))
			return null;
		try{
			SupplierClient client= listSuppliers.get(idSup);
			if(client==null){
				throw new NotItemException("Supplier Id cannot be null!");
			}
			CartItem cartitem = null;
			Cart cartview = null;
			for (Cart cart:cartlist){
				if(cartId.compareTo(cart.getId())==0){
					cartview=cart;
					for(CartItem cartitemview : cartview.getItems()){
					if(cartitemview.getItem().getSupplierId().compareTo(idSup)==0)
						if(idProd.compareTo(cartitemview.getItem().getProductId())==0)
							cartitem=cartitemview;
					}
					break;
				}
			}
			ProductView prod = client.getProduct(idProd);
			if(prod==null)
				throw new NotItemException("Product Id cannot be null!");
			if(itemQty<= 0)
				throw new InvalidQuantityException("Quantity must be positive!");
			if(cartitem==null) {
				if(prod.getQuantity() < itemQty)
					throw new NotEnoughProductsException("Supplier doesn't have enough products!");
			}else if((itemQty + cartitem.getQuantity() ) > prod.getQuantity())
				throw new NotEnoughProductsException("Supplier doesn't have enough products!");
				synchronized(this){
			if (cartview==null){
				cartitem =  new CartItem(new Item(client.getProduct(idProd),idSup),itemQty);
				cartview = new Cart(cartId);
				cartview.addToCart(cartitem);
				cartlist.add(cartview);
			}else if(cartitem==null){
				cartitem =  new CartItem(new Item(client.getProduct(idProd),idSup),itemQty);
				cartview.addToCart(cartitem);
				}else{
				cartitem.setQuantity(itemQty);
			}
			}
			operationscart.add(idoperation);
			return cartview;		
		}catch(BadProductId_Exception x){
			throw new NotItemException("Item must exist!");
		}
	}

	public ShoppingResult buyCart(String cartId, String creditCardNr,String idoperation) throws NoCartItemsException,NotCartException,CCInvalidException{
		lookForSuppliers();
		if(shoppingHistory.containsKey(idoperation))
			return shoppingHistory.get(idoperation);
		CreditCardClient ccClient=null;
		try{
			ccClient = new CreditCardClient("http://ws.sd.rnl.tecnico.ulisboa.pt:8080/cc");
		}catch(Exception x){
			System.out.println("Couldn't connect to server");
		}
			
			int lostBuys;
			int totalPrice=0;
			StringBuilder buyId = new StringBuilder();
	 	if (cartId == null)
			throw new NotCartException("Cart identifier cannot be null!");
		cartId = cartId.trim();
		if (cartId.length() == 0)
			throw new NotCartException("Cart identifier cannot be empty or whitespace!");
		Cart cartview = null;
		for (Cart cart:cartlist){
			if(cartId.compareTo(cart.getId())==0){
				cartview=cart;
				if (cartview.getItems().isEmpty()){
	 	 			throw new NoCartItemsException("Cart is Empty!");
	 			}
				break;
			}
		}
		if(cartview==null)
			throw new NotCartException("Cart Not Found!");

		if(creditCardNr==null)
			throw new CCInvalidException("Credit Card Number cannot be null!");
		creditCardNr = creditCardNr.trim();
		if (creditCardNr.length() == 0)
			throw new CCInvalidException("Credit Card Number cannot be empty or whitespace!");
		if((ccClient.validateNumber(creditCardNr))==false){
			throw new CCInvalidException("Credit Card Number Invalid!");
		}
		ArrayList<CartItem> itensPerdidos=new ArrayList<>();
		ArrayList<CartItem> itensComprados=new ArrayList<>();
		synchronized(this){
			for(CartItem cartitemview : cartview.getItems()){
				try{
					SupplierClient client =  listSuppliers.get(cartitemview.getItem().getSupplierId());
					buyId.append( client.buyProduct(cartitemview.getItem().getProductId(),cartitemview.getQuantity()));
					totalPrice = totalPrice + cartitemview.getItem().getPrice()*cartitemview.getQuantity();
					itensComprados.add(cartitemview);
				}catch(Exception x){
					itensPerdidos.add(cartitemview);
					continue;
				}
			}
		cartview.getItems().clear();
		}	
		ShoppingResult result= new ShoppingResult(buyId.toString() + Integer.toString(shoppingId.incrementAndGet()),totalPrice,itensComprados,itensPerdidos);
		shoppingHistory.put(idoperation,result);
		return result;
	}

	private synchronized void lookForSuppliers(){
		int i=1;
		while(true){	
			try{	
				String text= "T59_Supplier"+i;
				SupplierClient supplier = new SupplierClient("http://localhost:9090",text);
				i=i+1;
				listSuppliers.put(text,supplier);
			 	
			
			}catch(Exception x){
				break;
			}
		}
	}

	public List<Cart> listCarts() {
		return cartlist;
	}

	public List<ShoppingResult> shopHistory() {
		return  new ArrayList<ShoppingResult>(shoppingHistory.values());
	}


	public String ping(String arg0) {
				if (arg0 == null || arg0.trim().length() == 0)
			arg0 = "friend";
		lookForSuppliers();
		StringBuilder builder = new StringBuilder();

		int i = 1;
		for (;i<=listSuppliers.size();i++){
			builder.append(listSuppliers.get("T59_Supplier"+i).ping(arg0)).append("\n");
		}
		return builder.toString();
	 }

	 public boolean checkTime(){
	 	if(lastTimeStamp==null)
	 		return false;
	 	Date oldDate=new Date(System.currentTimeMillis()-(LifeProof.SECONDS+MediatorClient.MAX_TIME)*1000);
	 	return lastTimeStamp.compareTo(oldDate)<0;
	 }

	 public synchronized void newTimeStamp(){
	 	lastTimeStamp = new Date();
	 }

	public void updateShopHistory(ShoppingResult shopResult,String idoperation){
		for(ShoppingResult spr: shopHistory())
			if(shopResult.getId().compareTo(spr.getId())==0){
				System.out.println("Update Ignored");
				return;
			}
		shoppingHistory.put(idoperation,shopResult);
		System.out.println("Updated");

	}

    public void updateCart(Cart cart, String idoperation){
		for(Cart crt: cartlist)
			if(cart.getId().compareTo(crt.getId())==0){
				cartlist.remove(crt);
				cartlist.add(cart);
				return;
			}
		cartlist.add(cart);
		operationscart.add(idoperation);
		System.out.println("Updated");

    }

}
