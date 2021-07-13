package org.komparator.mediator.domain;

import java.util.ArrayList;
import java.util.List;

import org.komparator.supplier.ws.ProductView;

public class ShoppingResult{
	private String id;
	private ArrayList<CartItem> itemperdidos = null;
	private ArrayList<CartItem> itemcomprados = null; 
	private int totalPrice = 0;

	public ShoppingResult(String uniqueid,int totalprice, ArrayList<CartItem> itemcmprds, ArrayList<CartItem> itemprdds ){
		id=uniqueid;
		totalPrice=totalprice;
		itemperdidos=itemprdds;
		itemcomprados=itemcmprds;
	}

	public ArrayList<CartItem> getItemsComprados(){
		return itemcomprados;
	}

	public ArrayList<CartItem> getItemsPerdidos(){
		return itemperdidos;
	}

	public String getId(){
		return id;
	}

	public int getTotalPrice(){
		return totalPrice;
	}	




}


