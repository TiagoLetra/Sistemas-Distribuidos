package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
public class SearchProductsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp()throws BadProductId_Exception, BadProduct_Exception  {
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

	@AfterClass
	public static void oneTimeTearDown() {
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

	// public List<ProductView> searchProducts(String descText) throws
	// BadText_Exception

	// bad input tests

	@Test(expected = BadText_Exception.class)
	public void SearchProductsNullTest() throws BadText_Exception {
		client.searchProducts(null);
	}

	@Test(expected = BadText_Exception.class)
	public void SearchProductsEmptyTest() throws BadText_Exception {
		client.searchProducts("");
	}

	@Test(expected = BadText_Exception.class)
	public void SearchProductsWhitespaceTest() throws BadText_Exception {
		client.searchProducts(" ");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsTabTest() throws BadText_Exception {
		client.searchProducts("\t");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsNewlineTest() throws BadText_Exception {
		client.searchProducts("\n");
	}


	// main tests



			@Test
	public void searchOneCharExistsTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("S");
		assertEquals(1,products.size());
		assertEquals("Z3", products.get(0).getId());
		assertEquals(30, products.get(0).getPrice());
		assertEquals(30, products.get(0).getQuantity());
		assertEquals("Soccer ball", products.get(0).getDesc());
	}

		@Test
	public void searchFullDescriptionExistsTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("Soccer ball");
		assertEquals(1,products.size());
		assertEquals("Z3", products.get(0).getId());
		assertEquals(30, products.get(0).getPrice());
		assertEquals(30, products.get(0).getQuantity());
		assertEquals("Soccer ball", products.get(0).getDesc());
	}

			@Test
	public void searchHalfWordExistsTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("Soc");
		assertEquals(1,products.size());
		assertEquals("Z3", products.get(0).getId());
		assertEquals(30, products.get(0).getPrice());
		assertEquals(30, products.get(0).getQuantity());
		assertEquals("Soccer ball", products.get(0).getDesc());
	}

		@Test
	public void searchProductsExistsTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("Soccer");
		assertEquals(1,products.size());
		assertEquals("Z3", products.get(0).getId());
		assertEquals(30, products.get(0).getPrice());
		assertEquals(30, products.get(0).getQuantity());
		assertEquals("Soccer ball", products.get(0).getDesc());
	}

	@Test
	public void getProductAnotherExistsTest() throws BadText_Exception {
		List<ProductView> products = client.searchProducts("ball");
		assertEquals(3,products.size());

		assertEquals("X1", products.get(0).getId());
		assertEquals(10, products.get(0).getPrice());
		assertEquals(10, products.get(0).getQuantity());
		assertEquals("Basketball", products.get(0).getDesc());

		assertEquals("Y2", products.get(1).getId());
		assertEquals(20, products.get(1).getPrice());
		assertEquals(20, products.get(1).getQuantity());
		assertEquals("Baseball", products.get(1).getDesc());

		assertEquals("Z3", products.get(2).getId());
		assertEquals(30, products.get(2).getPrice());
		assertEquals(30, products.get(2).getQuantity());
		assertEquals("Soccer ball", products.get(2).getDesc());
	}

	@Test
	public void getProductNotExistsTest() throws BadText_Exception {
	List<ProductView> products = client.searchProducts("test");
	assertNotNull(products);
	}

	@Test
	public void getProductLowercaseNotExistsTest() throws  BadText_Exception  {
	List<ProductView> products = client.searchProducts("Ball");
	assertNotNull(products);
	}

}
