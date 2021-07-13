package org.komparator.mediator.domain;

import org.komparator.supplier.ws.ProductView;


public class Item{

	private String productId;
	private String supplierId;
	private int price;
	private String desc;

	public Item(ProductView pd,String id){
		productId=pd.getId();
		price=pd.getPrice();
		supplierId=id;
		desc=pd.getDesc();
	}

	public Item(String pdid, String sid , String desc , int pc){
		productId=pdid;
		supplierId=sid;
		this.desc=desc;
		price=pc;

	}

	public int getPrice(){
		return price;
	}

	public String getProductId(){
		return productId;
	}

	public String getSupplierId(){
		return supplierId;
	}

	public String getDesc(){
		return desc;
	}


}