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

import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.CartItemView;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;

/**
 * Test suite
 */
public class AddToCartIT extends BaseIT {

	// static members
	private static ArrayList<SupplierClient> LISTSUPPLIERS = new ArrayList<>();


	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
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
	public void setUp() {
		mediatorClient.clear();
	}

	@After
	public void tearDown() {
		mediatorClient.clear();
	}

	// tests
	// assertEquals(expected, actual);

	// public List<ItemView> addToCart(String ProductId) throws
	// InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {

	// bad input tests

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartNullTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		ItemIdView item = null;
		mediatorClient.addToCart(null,item,-1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartEmptyTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = null;
		mediatorClient.addToCart("",item,-1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartWhitespaceTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = null;
		mediatorClient.addToCart(" ",item,-1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartTabTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = null;
		mediatorClient.addToCart("\t",item,-1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartNewlineTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = null;
		mediatorClient.addToCart("\n",item,-1);
	}

		@Test(expected = InvalidItemId_Exception.class)
	public void addToCartNullItemIdTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item = null;
		mediatorClient.addToCart("cart",item,-1);
	}
	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartNullSupplierTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartNoSupplierTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("NOTEXIST");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartEmptySupplierrTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartWhiteSpaceSupplierTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId(" ");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartNewLineSupplierTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("\n");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartTabSupplierTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("\t");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartEmptyProductTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartWhiteSpaceProductTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId(" ");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartTabProductTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("\t");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartNewLineProductTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("\n");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidQuantity_Exception.class)
	public void addToCartNegativeTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,-1);
	}

	@Test(expected = InvalidQuantity_Exception.class)
	public void addToCartZeroTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,0);
	}

	@Test(expected = NotEnoughItems_Exception.class)
	public void addToCartOverTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,11);
	}


	@Test(expected = NotEnoughItems_Exception.class)
	public void addToCartNotEnoughTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,6);
		mediatorClient.addToCart("cart",item,5);
	}
	


	// main tests

	@Test
	public void addToCartTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,5);
		List<CartView> cartlist = mediatorClient.listCarts();

		CartItemView cart = cartlist.get(0).getItems().get(0);
		assertEquals(5,cart.getQuantity());
		assertEquals("cart",cartlist.get(0).getCartId());
		assertEquals("X1",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(10,cart.getItem().getPrice());
	}

	@Test
	public void addToCartDiferentProductsSameCartTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,5);
		item.setProductId("Y2");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,7);
		List<CartView> cartlist = mediatorClient.listCarts();

		CartItemView cart = cartlist.get(0).getItems().get(0);
		assertEquals(5,cart.getQuantity());
		assertEquals("cart",cartlist.get(0).getCartId());
		assertEquals("X1",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(10,cart.getItem().getPrice());
	

		cart = cartlist.get(0).getItems().get(1);
		assertEquals(7,cart.getQuantity());
		assertEquals("cart",cartlist.get(0).getCartId());
		assertEquals("Y2",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(20,cart.getItem().getPrice());


		}
	

	@Test
	public void addToCartDiferentSuppliersSameCartTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		int i=1;
		ItemIdView item= new ItemIdView();
		for(;i<=LISTSUPPLIERS.size();i++){
			item.setProductId("Z3");
			item.setSupplierId("T59_Supplier"+i);
			mediatorClient.addToCart("cart",item,i);
			}
		CartItemView cart;
		List<CartView> cartlist = mediatorClient.listCarts();
		for(;i<=LISTSUPPLIERS.size();i++){
			cart = cartlist.get(0).getItems().get(i-1);
			assertEquals(i,cart.getQuantity());
			assertEquals("cart",cartlist.get(0).getCartId());
			assertEquals("Z3",cart.getItem().getItemId().getProductId());
			assertEquals("T59_Supplier"+i,cart.getItem().getItemId().getSupplierId());
			assertEquals(30+i,cart.getItem().getPrice());
			}




	}	
	@Test
	public void addToCartMultipleCartsSameProductTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,5);
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart1",item,7);
		List<CartView> cartlist = mediatorClient.listCarts();


		CartItemView cart = cartlist.get(0).getItems().get(0);
		assertEquals(5,cart.getQuantity());
		assertEquals("cart",cartlist.get(0).getCartId());
		assertEquals("X1",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(10,cart.getItem().getPrice());
	

		cart = cartlist.get(1).getItems().get(0);
		assertEquals(7,cart.getQuantity());
		assertEquals("cart1",cartlist.get(1).getCartId());
		assertEquals("X1",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(10,cart.getItem().getPrice());	
	}
	@Test
	public void AddSameProductSameCartTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,2);
		mediatorClient.addToCart("cart",item,3);
		List<CartView> cartlist = mediatorClient.listCarts();

		CartItemView cart = cartlist.get(0).getItems().get(0);
		assertEquals(1,cartlist.size());
		assertEquals(5,cart.getQuantity());
		assertEquals("cart",cartlist.get(0).getCartId());
		assertEquals("X1",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(10,cart.getItem().getPrice());
		}
		@Test
	public void AddCartCaseSensitiveTest() throws InvalidCartId_Exception,
		InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView item= new ItemIdView();
		item.setProductId("X1");
		item.setSupplierId("T59_Supplier1");
		mediatorClient.addToCart("cart",item,2);
		mediatorClient.addToCart("Cart",item,3);
		List<CartView> cartlist = mediatorClient.listCarts();

		CartItemView cart = cartlist.get(0).getItems().get(0);
		assertEquals(2,cartlist.size());
		assertEquals(2,cart.getQuantity());
		assertEquals("cart",cartlist.get(0).getCartId());
		assertEquals("X1",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(10,cart.getItem().getPrice());

		cart = cartlist.get(1).getItems().get(0);
		assertEquals(3,cart.getQuantity());
		assertEquals("Cart",cartlist.get(1).getCartId());
		assertEquals("X1",cart.getItem().getItemId().getProductId());
		assertEquals("T59_Supplier1",cart.getItem().getItemId().getSupplierId());
		assertEquals(10,cart.getItem().getPrice());
		}
}
