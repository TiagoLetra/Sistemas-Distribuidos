package org.komparator.mediator.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cart{
	private String cartid;
	private List<CartItem> item = new CopyOnWriteArrayList<CartItem>();

	public Cart(String cid){
		cartid=cid;
	}

	public void addToCart(CartItem cartitem){
		item.add(cartitem);
	}

	public String getId(){
		return cartid;
	}

	public List<CartItem> getItems(){
		return item;
	}


}