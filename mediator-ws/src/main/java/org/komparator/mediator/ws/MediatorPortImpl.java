package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import org.komparator.mediator.ws.cli.MediatorClient;
import javax.xml.ws.BindingProvider;

import javax.jws.WebService;
import javax.jws.HandlerChain;

import java.util.concurrent.atomic.AtomicInteger;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import javax.annotation.Resource;
import org.komparator.mediator.domain.*;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.security.handler.IdOPerationReceiver;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;


	@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
		wsdlLocation = "mediator-ws.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
		)

@HandlerChain(file = "/mediator-ws_handler-chain.xml")
public class MediatorPortImpl implements MediatorPortType{

	// end point manager
	private MediatorEndpointManager endpointManager;


		@Resource
	private WebServiceContext webServiceContext;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}


	// Main operations -------------------------------------------------------


     @Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		Mediator mediator=Mediator.getInstance();
		try{		
			return newItemList(mediator.searchItems(descText));
		}catch(BadText_Exception x){
			throwInvalidText(x.getMessage());
		}
		return null;
	 }



	 @Override
	 public ShoppingResultView buyCart(String cartId, String creditCardNr)
			 throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		Mediator mediator=Mediator.getInstance();
		MessageContext messageContext =  webServiceContext.getMessageContext();
		try{
			ShoppingResult sresult = mediator.buyCart(cartId,creditCardNr, (String) messageContext.get(IdOPerationReceiver.REQUEST_PROPERTY));
			ShoppingResultView result =	newShoppingResultView(sresult);
			int i =0;
			boolean secundary=false;
			boolean stop = false;
			while(true){
			try{
				UDDINaming uddi = new UDDINaming("http://localhost:9090");
				if(endpointManager.getWsName()==null)
					break;
				String wsurl = uddi.lookup(endpointManager.getWsName());
				if(secundary){
					System.out.println("Backup"+i+" Reset");
					MediatorClient mc = new MediatorClient("http://localhost:807"+(i)+"/mediator-ws/endpoint");
					mc.updateShopHistory(result,(String) messageContext.get(IdOPerationReceiver.REQUEST_PROPERTY));
				}else if(wsurl.compareTo("http://localhost:807"+(i)+"/mediator-ws/endpoint")==0){
					secundary=true;
				}
				i++;
			}catch(Exception x){
				break;
			}
		}
			return result;
		}catch(CCInvalidException x){
			throwInvalidCreditCard(x.getMessage());
		}catch(NoCartItemsException x){
			throwEmptyCart(x.getMessage());
		}catch(NotCartException x){
			throwInvalidCartId(x.getMessage());
		}
		return null;


	 }



	 @Override
	 public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	    Mediator mediator =Mediator.getInstance();

	    if (cartId == null)
		throwInvalidCartId("Cart identifier cannot be null!");
		cartId = cartId.trim();
		if (cartId.length() == 0)
			throwInvalidCartId("Cart identifier cannot be empty or whitespace!");

		if(itemId==null)
			throwInvalidItemId("Insert Supplier Id!");
		String sup = itemId.getSupplierId();
		if(sup==null)
			throwInvalidItemId("Insert Supplier Id!");
		sup = sup.trim();
		if (sup.length() == 0)
			throwInvalidItemId("The supplier cannot be empty or whitespace!");
		CartView cv; 
		MessageContext messageContext =  webServiceContext.getMessageContext();
		try{
			Cart cart = mediator.addToCart(cartId,itemId.getSupplierId(),itemId.getProductId(),itemQty,(String) messageContext.get(IdOPerationReceiver.REQUEST_PROPERTY));
			if (cart==null)
				return;
			cv=newCartview(cart);
			if(cv!=null){
			int i =0;
			boolean secundary=false;
			boolean stop = false;
			while(true){
			try{

				UDDINaming uddi = new UDDINaming("http://localhost:9090");
				if(endpointManager.getWsName()==null)
					break;
				String wsurl = uddi.lookup(endpointManager.getWsName());
				if(secundary){
					System.out.println("Backup"+i+" Reset");
					MediatorClient mc = new MediatorClient("http://localhost:807"+(i)+"/mediator-ws/endpoint");
					mc.updateCart(cv,(String) messageContext.get(IdOPerationReceiver.REQUEST_PROPERTY));
				}else if(wsurl.compareTo("http://localhost:807"+(i)+"/mediator-ws/endpoint")==0){
					secundary=true;
				}
				i++;

			}catch(Exception x){
				break;
			}
			}
		}

		}catch(NotItemException x){
			throwInvalidItemId(x.getMessage());
		}catch(InvalidQuantityException x){
			throwInvalidQuantity(x.getMessage());
		}catch(NotEnoughProductsException x){
			throwNotEnoughItems(x.getMessage());
		}


	}
		
	



	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		Mediator mediator=Mediator.getInstance();
		try{		
			return newItemList(mediator.getItems(productId));
		}catch(BadProductId_Exception x){
			throwInvalidItemId(x.getMessage());
		}
		return null;
	}


	
    
	// Auxiliary operations --------------------------------------------------	
	
		@Override
    public void updateShopHistory(ShoppingResultView shopResult,String idOperation) {
    	System.out.println("Receiving Update ShopHistory");
		Mediator mediator =Mediator.getInstance();
		ArrayList<CartItem> recebidos = new ArrayList<CartItem>();
		for(CartItemView cart :shopResult.getPurchasedItems()){
			ItemView item = cart.getItem();
			recebidos.add(new CartItem(new Item(item.getItemId().getProductId(),item.getItemId().getSupplierId(),item.getDesc(),item.getPrice()),cart.getQuantity()));
		}
		ArrayList<CartItem> perdidos = new ArrayList<CartItem>();
		for(CartItemView cart :shopResult.getDroppedItems()){
			ItemView item = cart.getItem();
			perdidos.add(new CartItem(new Item(item.getItemId().getProductId(),item.getItemId().getSupplierId(),item.getDesc(),item.getPrice()),cart.getQuantity()));
		}
		mediator.updateShopHistory(new ShoppingResult(shopResult.getId(),shopResult.getTotalPrice(),recebidos,perdidos),idOperation);

	}

		@Override
    public void updateCart(CartView cart, String idOperation) {
    System.out.println("Receiving Update Cart");
		Mediator mediator =Mediator.getInstance();
		Cart realCart = new Cart(cart.getCartId());
		for(CartItemView cartitem:cart.getItems()){
			ItemView item = cartitem.getItem();
			realCart.addToCart(new CartItem(new Item(item.getItemId().getProductId(),item.getItemId().getSupplierId(),item.getDesc(),item.getPrice()),cartitem.getQuantity()));
		}
		mediator.updateCart(realCart,idOperation);

	}

		@Override
	public void imAlive() {
		boolean secundario=true;
		if(secundario){
			Mediator mediator =Mediator.getInstance();
			mediator.newTimeStamp();
			System.out.println("Alive!!");
		}

	}


     @Override
	public String ping(String arg0) {
		
		if(arg0.equals("kill"))
			System.exit(0);
		return Mediator.getInstance().ping(arg0);
	 }

    @Override
	public void clear() {
		Mediator.getInstance().reset();
		int i=1;

		System.out.println("System Reset");
		boolean secundary=false;
		boolean stop = false;
		while(true){
			try{

				UDDINaming uddi = new UDDINaming("http://localhost:9090");
				if(endpointManager.getWsName()==null)
					break;
				String wsurl = uddi.lookup(endpointManager.getWsName());
				if(secundary){
					MediatorClient mc = new MediatorClient("http://localhost:807"+(i)+"/mediator-ws/endpoint");
					mc.clear();
					System.out.println("Backup"+i+" Reset");
				}else if(wsurl.compareTo("http://localhost:807"+(i)+"/mediator-ws/endpoint")==0){
					secundary=true;
				}
				i++;
			}catch(Exception x){
				break;
			}

		}
	}

		    @Override
	public List<CartView> listCarts() {
		 Mediator mediator =Mediator.getInstance();
		 ArrayList<CartView> listcart= new ArrayList();
		 for(Cart cart : mediator.listCarts())
		 	listcart.add(newCartview(cart));
		 return listcart;
	}

			 @Override
	 public List<ShoppingResultView> shopHistory() {
	 	Mediator mediator =Mediator.getInstance();
	 	ArrayList<ShoppingResultView> shoppingHistory = new ArrayList();
		for(ShoppingResult shop: mediator.shopHistory())
			shoppingHistory.add(newShoppingResultView(shop));
		return shoppingHistory;
	 }

	// View helpers -----------------------------------------------------
	
	private List<ItemView> newItemList(List<Item> item){
		ArrayList<ItemView> list = new ArrayList();
		for(Item it:item)
			list.add(newItemView(it));
		return list;
	}

	private ItemView newItemView(Item item){
		ItemView itev = new ItemView();
		ItemIdView itid = new ItemIdView();
		itev.setPrice(item.getPrice());
		itid.setProductId(item.getProductId());
		itid.setSupplierId(item.getSupplierId());
		itev.setItemId(itid);
		itev.setDesc(item.getDesc());
		return itev;
	}


    private CartView newCartview(Cart cart){
    	CartView cartv = new CartView();
    		cartv.setCartId(cart.getId());
    		cartv.getItems().addAll(newCartItemViewList(cart.getItems()));
    		return cartv;
    }

	private List<CartItemView> newCartItemViewList(List<CartItem> listcart){
		ArrayList<CartItemView> listcartv = new ArrayList<>();
		for(CartItem cart: listcart)
			listcartv.add(newCartItemView(cart));
		return listcartv;
	} 

    private CartItemView newCartItemView(CartItem cartitem){
    	CartItemView cartitemv = new CartItemView();
    	cartitemv.setQuantity(cartitem.getQuantity());
    	cartitemv.setItem(newItemView(cartitem.getItem()));
    	return cartitemv;
    }

    private ShoppingResultView newShoppingResultView(ShoppingResult sresult){
    		ShoppingResultView result = new ShoppingResultView();
    		result.setTotalPrice(sresult.getTotalPrice());
    		result.setId(sresult.getId());
    		result.getPurchasedItems().addAll(newCartItemViewList(sresult.getItemsComprados()));
    		result.getDroppedItems().addAll(newCartItemViewList(sresult.getItemsPerdidos()));

    		if(result.getDroppedItems().size()==0)
				result.setResult(Result.COMPLETE);
			else if(0<result.getPurchasedItems().size())
				result.setResult(Result.PARTIAL);
			else 
				result.setResult(Result.EMPTY);
    	return result;
    }

    
	// Exception helpers -----------------------------------------------------

	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}

	private void throwInvalidText(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}

	private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.message = message;
		throw new InvalidCartId_Exception(message, faultInfo);
	}


	private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.message = message;
		throw new InvalidQuantity_Exception(message, faultInfo);
	}

	private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.message = message;
		throw new NotEnoughItems_Exception(message, faultInfo);
	}

	private void throwEmptyCart(final String message) throws EmptyCart_Exception {
    	EmptyCart faultInfo = new EmptyCart();
		faultInfo.message = message;
		throw new EmptyCart_Exception(message, faultInfo);
	}

	private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
    	InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.message = message;
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}
}
