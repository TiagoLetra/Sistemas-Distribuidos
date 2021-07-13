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
public class ListCartsIT extends BaseIT {

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
		
		SupplierClient client = LISTSUPPLIERS.get(0);
		client.clear();
		// fill-in test products
		// (since addToCart is read-only the initialization below
		// can be done once for all tests in this suite)

		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
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

	// public List<CartView> listCarts()  {

	// bad input tests

	


	// main tests

	@Test
	public void listCartsTest() throws InvalidCartId_Exception,
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


	

}
