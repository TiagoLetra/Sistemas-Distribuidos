package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



import org.komparator.mediator.ws.Result;
import org.komparator.mediator.ws.ShoppingResultView;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.CartItemView;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.PurchaseView;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;

/**
 * Test suite
 */
public class BuyCartIT extends BaseIT {

	// static members
	private static ArrayList<SupplierClient> LISTSUPPLIERS = new ArrayList<>();


	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		// clear remote service state before all tests
		mediatorClient.clear();
		int i=1;
		while(true){	
			try{	
				SupplierClient supplier = new SupplierClient("http://localhost:9090","T59_Supplier"+i);
				i=i+1;
				LISTSUPPLIERS.add(supplier);
			 	
			
			}catch(Exception x){
				break;
			}
		}
		
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		mediatorClient.clear();
		for(SupplierClient client:LISTSUPPLIERS)
			client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
		for(SupplierClient client:LISTSUPPLIERS)
			client.clear();
		mediatorClient.clear();
		int j=0;
		for(SupplierClient client:LISTSUPPLIERS){
		client.clear();
			if(j==20)
				break;
		// fill-in test products
		// (since addToCart is read-only the initialization below
		// can be done once for all tests in this suite)

		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10+j);
			product.setQuantity(10+j);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20-j);
			product.setQuantity(20-j);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30+j);
			product.setQuantity(30+j);
			client.createProduct(product);
		}
		j++;
	}	
	}

	@After
	public void tearDown() {
		mediatorClient.clear();
		for(SupplierClient client:LISTSUPPLIERS)
			client.clear();
	}

	// tests
	// assertEquals(expected, actual);
	// public ShoppingResultView buyCart(String cartId, String creditCardNr)
	//	throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{

	// bad input tests

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartNullTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception{
		mediatorClient.buyCart(null,"");

	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCarttEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("","");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartWhitespaceTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart(" ","");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartTabTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\t","");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartNewlineTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\n","");
	}


			@Test(expected = InvalidCreditCard_Exception .class)
	public void buyCCNullTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception ,InvalidItemId_Exception 
		,InvalidQuantity_Exception ,NotEnoughItems_Exception{
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("cart",null);
	}

		@Test(expected = InvalidCreditCard_Exception .class)
	public void buyCCEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception ,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("cart","");
	}
		@Test(expected = InvalidCreditCard_Exception .class)
	public void buyCCWhiteSpaceTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception 
	,InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("cart"," ");
	}
		@Test(expected = InvalidCreditCard_Exception .class)
	public void buyCCTabTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception ,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("cart","\t");
	}
		@Test(expected = InvalidCreditCard_Exception .class)
	public void buyCCNewlineTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception 
	,InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("cart","\n");
	}

	


	// main tests

	@Test
	public void buyCartTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		ShoppingResultView result = mediatorClient.buyCart("cart","6769381066990468");
		SupplierClient client=LISTSUPPLIERS.get(0);

		List<PurchaseView> listp = client.listPurchases();
		assertEquals(1,listp.size());
		List<CartItemView> list = result.getPurchasedItems();
		assertEquals(10,result.getTotalPrice());
		assertEquals(1,list.size());
		assertEquals("X1",list.get(0).getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",list.get(0).getItem().getItemId().getSupplierId());
		assertEquals(1,list.get(0).getQuantity());
		list = result.getDroppedItems();
		assertEquals(0,list.size());
		assertEquals(Result.COMPLETE,result.getResult());
	}

	@Test
	public void buyCartMultipleItemsTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		itemid.setProductId("Y2");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,5);
		itemid.setProductId("Z3");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,5);

		ShoppingResultView result = mediatorClient.buyCart("cart","6769381066990468");
		SupplierClient client=LISTSUPPLIERS.get(0);

		List<PurchaseView> listp = client.listPurchases();
		List<CartItemView> list = result.getPurchasedItems();
		assertEquals(3,listp.size());
		assertEquals(260,result.getTotalPrice());
		assertEquals(3,list.size());
		assertEquals("X1",list.get(0).getItem().getItemId().getProductId());
		assertEquals("Y2",list.get(1).getItem().getItemId().getProductId());
		assertEquals("Z3",list.get(2).getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",list.get(0).getItem().getItemId().getSupplierId());
		assertEquals("T59_Supplier1",list.get(1).getItem().getItemId().getSupplierId());
		assertEquals("T59_Supplier1",list.get(2).getItem().getItemId().getSupplierId());
		assertEquals(1,list.get(0).getQuantity());
		assertEquals(5,list.get(1).getQuantity());
		assertEquals(5,list.get(2).getQuantity());
		list = result.getDroppedItems();
		assertEquals(0,list.size());
		assertEquals(Result.COMPLETE,result.getResult());

		}
	

	@Test
	public void buyCartMultipleSuppliersTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception{
		int i=1;
		ItemIdView item= new ItemIdView();
		for(;i<=LISTSUPPLIERS.size();i++){
			item.setProductId("Z3");
			item.setSupplierId("T59_Supplier"+i);
			mediatorClient.addToCart("cart",item,i);
			}
		ShoppingResultView result = mediatorClient.buyCart("cart","6769381066990468");
		List<CartItemView> listd = result.getDroppedItems();
		List<CartItemView> list = result.getPurchasedItems();
		int totalprice=0;
		for(i=1;i<=LISTSUPPLIERS.size();i++){
			List<PurchaseView> listp =	LISTSUPPLIERS.get(i-1).listPurchases();

			assertEquals(1,listp.size());
			totalprice=totalprice + (i*(30+i-1));
			assertEquals(LISTSUPPLIERS.size(),list.size());
			assertEquals("Z3",list.get(i-1).getItem().getItemId().getProductId());
			assertEquals("T59_Supplier"+i,list.get(i-1).getItem().getItemId().getSupplierId());
			assertEquals(i,list.get(i-1).getQuantity());
			assertEquals(0,listd.size());
			assertEquals(Result.COMPLETE,result.getResult());
			}
		assertEquals(totalprice,result.getTotalPrice());
	}	


		@Test
	public void buyCartEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,10);
		mediatorClient.addToCart("cart1",itemid,10);
		itemid.setProductId("Y2");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,20);
		mediatorClient.addToCart("cart1",itemid,20);
		itemid.setProductId("Z3");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,30);
		mediatorClient.addToCart("cart1",itemid,30);
		mediatorClient.buyCart("cart","6769381066990468");
		ShoppingResultView result=mediatorClient.buyCart("cart1","6769381066990468");

		SupplierClient client=LISTSUPPLIERS.get(0);
		List<PurchaseView> listp = client.listPurchases();
		assertEquals(3,listp.size());
		List<CartItemView> list = result.getPurchasedItems();
		assertEquals(0,result.getTotalPrice());
		assertEquals(0,list.size());
		list = result.getDroppedItems();
		assertEquals("X1",list.get(0).getItem().getItemId().getProductId());
		assertEquals("Y2",list.get(1).getItem().getItemId().getProductId());
		assertEquals("Z3",list.get(2).getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",list.get(0).getItem().getItemId().getSupplierId());
		assertEquals("T59_Supplier1",list.get(1).getItem().getItemId().getSupplierId());
		assertEquals("T59_Supplier1",list.get(2).getItem().getItemId().getSupplierId());
		assertEquals(10,list.get(0).getQuantity());
		assertEquals(20,list.get(1).getQuantity());
		assertEquals(30,list.get(2).getQuantity());
		list = result.getDroppedItems();
		assertEquals(3,list.size());
		assertEquals(Result.EMPTY,result.getResult());



	}

		@Test
	public void buyCartPartialTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart1",itemid,10);
		itemid.setProductId("Y2");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,20);
		mediatorClient.addToCart("cart1",itemid,20);
		itemid.setProductId("Z3");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart1",itemid,30);
		mediatorClient.buyCart("cart","6769381066990468");
		ShoppingResultView result =mediatorClient.buyCart("cart1","6769381066990468");
		SupplierClient client=LISTSUPPLIERS.get(0);

		List<PurchaseView> listp = client.listPurchases();
		assertEquals(3,listp.size());
		List<CartItemView> list = result.getPurchasedItems();
		assertEquals(30*30+10*10,result.getTotalPrice());
		assertEquals(2,list.size());
		assertEquals("X1",list.get(0).getItem().getItemId().getProductId());
		assertEquals("Z3",list.get(1).getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",list.get(0).getItem().getItemId().getSupplierId());
		assertEquals("T59_Supplier1",list.get(1).getItem().getItemId().getSupplierId());
		assertEquals(10,list.get(0).getQuantity());
		assertEquals(30,list.get(1).getQuantity());
		list = result.getDroppedItems();
		assertEquals(1,list.size());
		assertEquals("Y2",list.get(0).getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",list.get(0).getItem().getItemId().getSupplierId());
		assertEquals(20,list.get(0).getQuantity());
		assertEquals(Result.PARTIAL,result.getResult());

	}


	@Test
	public void buyCartSameCartTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("cart","6769381066990468");
		itemid.setProductId("Z3");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		ShoppingResultView result = mediatorClient.buyCart("cart","6769381066990468");

		SupplierClient client=LISTSUPPLIERS.get(0);

		List<PurchaseView> listp = client.listPurchases();
		assertEquals(2,listp.size());
		List<CartItemView> list = result.getPurchasedItems();
		assertEquals(30,result.getTotalPrice());
		assertEquals(1,list.size());
		assertEquals("Z3",list.get(0).getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",list.get(0).getItem().getItemId().getSupplierId());
		assertEquals(1,list.get(0).getQuantity());
		list = result.getDroppedItems();
		assertEquals(0,list.size());
		assertEquals(Result.COMPLETE,result.getResult());

		}
	
	
	@Test(expected = EmptyCart_Exception.class)
	public void buyCartEmptyCartTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception ,InvalidItemId_Exception
	 ,InvalidQuantity_Exception , NotEnoughItems_Exception {
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("cart","6769381066990468");
		mediatorClient.buyCart("cart","6769381066990468");
	}

		@Test(expected = InvalidCartId_Exception.class)
	public void buyCartNotExistsCartTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception ,InvalidItemId_Exception
	 ,InvalidQuantity_Exception , NotEnoughItems_Exception {
		mediatorClient.buyCart("carti","6769381066990468");
	}

		@Test(expected = InvalidCartId_Exception.class)
	public void buyCartCaseSensitiveCartTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception ,InvalidItemId_Exception
	 ,InvalidQuantity_Exception , NotEnoughItems_Exception {
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.buyCart("Cart","6769381066990468");
	}
	
}
