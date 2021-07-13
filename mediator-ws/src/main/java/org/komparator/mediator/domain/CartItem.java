package org.komparator.mediator.domain;


import org.komparator.supplier.ws.ProductView;

public class CartItem{
	private Item item;
	private volatile int quantity;

	public CartItem(Item ite, int qtty){
		item=ite;
		quantity=qtty;
	}

	public Item getItem(){
		return item;
	}

	public void setQuantity(int plus){
		quantity = quantity + plus;
	}

	public int getQuantity(){
		return quantity;
	}


}