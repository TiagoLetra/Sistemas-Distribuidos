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
public class FailureIT extends BaseIT {

	// static members
	private static ArrayList<SupplierClient> LISTSUPPLIERS = new ArrayList<>();


	// one-time initialization and clean-up
@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception  {
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

	

		@Test
	public void buyCartTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception,InvalidItemId_Exception
	,InvalidQuantity_Exception, NotEnoughItems_Exception {
		ItemIdView itemid = new ItemIdView();
		itemid.setProductId("X1");
		itemid.setSupplierId("T59_Supplier1");
		System.out.println("Teste Preparado...");
		mediatorClient.addToCart("cart",itemid,1);
		mediatorClient.ping("kill"); // kill the primary mediator
		ShoppingResultView result = mediatorClient.buyCart("cart","6769381066990468"); // verifica update cart
		mediatorClient.ping("kill"); // kill first backup
		List<ShoppingResultView> list = mediatorClient.shopHistory();
		assertEquals(result.getId(),list.get(0).getId()); //id é único, verifica update shopResult

	}
}
