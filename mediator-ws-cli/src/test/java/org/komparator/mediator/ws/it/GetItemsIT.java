package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;

/**
 * Test suite
 */
public class GetItemsIT extends BaseIT {

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
		// (since getItems is read-only the initialization below
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
	}

	@After
	public void tearDown() {
	}

	// tests
	// assertEquals(expected, actual);

	// public List<ItemView> getItems(String productId) throws
	// InvalidItemId_Exception {

	// bad input tests

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsNullTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(null);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsEmptyTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsWhitespaceTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(" ");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsTabTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsNewlineTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}

	// main tests

	@Test
	public void getItemsExistsTest() throws InvalidItemId_Exception {
		List<ItemView> items = mediatorClient.getItems("X1");
		int i=0;
			assertEquals(LISTSUPPLIERS.size(),items.size());
		for(ItemView item: items){
			assertEquals("X1", item.getItemId().getProductId());
			assertEquals(10+i, item.getPrice());
			assertEquals("Basketball", item.getDesc());
			assertEquals("T59_Supplier"+(++i), item.getItemId().getSupplierId());
		}
	}

	@Test
	public void getItemsAnotherExistsTest() throws InvalidItemId_Exception {
		List<ItemView> items = mediatorClient.getItems("Y2");
		int i=Math.min(LISTSUPPLIERS.size()-1,19);
			assertEquals(LISTSUPPLIERS.size(),items.size());
		for(ItemView item: items){
			assertEquals("Y2", item.getItemId().getProductId());
			assertEquals(20-i, item.getPrice());
			assertEquals("Baseball", item.getDesc());
			assertEquals("T59_Supplier"+((i--)+1), item.getItemId().getSupplierId());
		}
	}

	@Test
	public void getItemsYetAnotherExistsTest() throws InvalidItemId_Exception {
		List<ItemView> items = mediatorClient.getItems("Z3");
		int i=0;
			assertEquals(LISTSUPPLIERS.size(),items.size());
		for(ItemView item: items){
			assertEquals("Z3", item.getItemId().getProductId());
			assertEquals(30+i, item.getPrice());
			assertEquals("Soccer ball", item.getDesc());
			assertEquals("T59_Supplier"+(++i), item.getItemId().getSupplierId());
		}
	
	}

	@Test
	public void getItemsNotExistsTest() throws InvalidItemId_Exception {
		// when product does not exist, null should be returned
		List<ItemView> items = mediatorClient.getItems("A0");

		assertEquals(items.size(),0);
	}

	@Test
	public void getItemsLowercaseNotExistsTest() throws InvalidItemId_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		List<ItemView> items = mediatorClient.getItems("x1");
		assertEquals(items.size(),0);
	}

}
