package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		client.clear();

	}

	@AfterClass
	public static void oneTimeTearDown() {
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
				client.clear();
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
	

	@After
	public void tearDown() {
		client.clear();
	}

	// tests
	// assertEquals(expected, actual);

	// public String buyProduct(String productId, int quantity)
	// throws BadProductId_Exception, BadQuantity_Exception,
	// InsufficientQuantity_Exception {

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(null,1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("",1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductWhitespaceTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(" ",1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductTabTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\t",1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNewlineTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\n",1);
	}
	
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegative() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1",-1);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZero() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", 0);
	}

	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductInsufficient() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1",11);
	}

	// main tests

		@Test
	public void buyProductNotEnoughTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1",10);
		List<PurchaseView> purchases = client.listPurchases();
		try{
		client.buyProduct("X1",1);

		}catch(InsufficientQuantity_Exception x){
			assertEquals(1,purchases.size());
		}
	}

		@Test
	public void BuyProductAllTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		int i;
		for(i=0;i<10;i++)
		client.buyProduct("X1",1);
		assertEquals(0,client.getProduct("X1").getQuantity());
		List<PurchaseView> purchases = client.listPurchases();
		assertEquals(10,purchases.size());
	}


	@Test
	public void buyProductExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId = client.buyProduct("X1",1);
		List<PurchaseView> purchases = client.listPurchases();
		assertEquals(purchases.size(),1);
		PurchaseView purchase = purchases.get(0);
		assertEquals(purchaseId, purchase.getId());
		assertEquals("X1", purchase.getProductId());
		assertEquals(10, purchase.getUnitPrice());
		assertEquals(1, purchase.getQuantity());
		assertEquals(9,client.getProduct("X1").getQuantity());
	}

	@Test
	public void buyProductAnotherExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId = client.buyProduct("Y2",20);
		List<PurchaseView> purchases = client.listPurchases();
		assertEquals(purchases.size(),1);
		PurchaseView purchase = purchases.get(0);
		assertEquals(purchaseId, purchase.getId());
		assertEquals("Y2", purchase.getProductId());
		assertEquals(20, purchase.getUnitPrice());
		assertEquals(20, purchase.getQuantity());
		assertEquals(0,client.getProduct("Y2").getQuantity());
	}

	@Test
	public void buyProductYetAnotherExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId = client.buyProduct("Z3",15);
		List<PurchaseView> purchases = client.listPurchases();
		assertEquals(purchases.size(),1);
		PurchaseView purchase = purchases.get(0);
		assertEquals(purchaseId, purchase.getId());
		assertEquals("Z3", purchase.getProductId());
		assertEquals(30, purchase.getUnitPrice());
		assertEquals(15, purchase.getQuantity());
		assertEquals(15,client.getProduct("Z3").getQuantity());
	}

	@Test
	public void BuyProductMultipleTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId = client.buyProduct("Z3",15);
		String purchaseId2 = client.buyProduct("Y2",20);
		List<PurchaseView> purchases = client.listPurchases();
		assertEquals(purchases.size(),2);
		PurchaseView purchase = purchases.get(0);
		PurchaseView purchase2 = purchases.get(1);

		assertEquals(purchaseId, purchase.getId());
		assertEquals("Z3", purchase.getProductId());
		assertEquals(30, purchase.getUnitPrice());
		assertEquals(15, purchase.getQuantity());
		assertEquals(purchaseId, purchase.getId());
		assertEquals("Y2", purchase2.getProductId());
		assertEquals(20, purchase2.getUnitPrice());
		assertEquals(20, purchase2.getQuantity());
		assertEquals(0,client.getProduct("Y2").getQuantity());
		assertEquals(15,client.getProduct("Z3").getQuantity());
	}

	@Test
	public void buyProductNotExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
	String purchaseId = client.buyProduct("A0",1);
	List<PurchaseView> purchases = client.listPurchases();
	assertNotNull(purchases);
	assertEquals(purchases.size(),0);
	}

	@Test 
	public void buyProductLowercaseNotExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
	client.buyProduct("x1",2);
	List<PurchaseView> purchases = client.listPurchases();
	assertNotNull(purchases);
	assertEquals(purchases.size(),0);
	}
	
}
