package com.ua.advertikon.scanner;

import javafx.beans.property.SimpleStringProperty;

public class DataRow {
	private final SimpleStringProperty monthProfit;
	private final SimpleStringProperty name;
	private final SimpleStringProperty sales;
	private final SimpleStringProperty price;
	private final SimpleStringProperty dateAdded;
	private final SimpleStringProperty dateModified;
	private final SimpleStringProperty profits;
	private final SimpleStringProperty id;

	DataRow( String monthProfit, String name, String price, String sales, String dateAdded, String dateModified, String profits, String id ) {
		this.monthProfit  = new SimpleStringProperty( monthProfit );
		this.name         = new SimpleStringProperty( name );
		this.sales        = new SimpleStringProperty( sales );
		this.price        = new SimpleStringProperty( price );
		this.dateAdded    = new SimpleStringProperty( dateAdded );
		this.dateModified = new SimpleStringProperty( dateModified );
		this.profits      = new SimpleStringProperty( profits );
		this.id           = new SimpleStringProperty( id );
	}

	public String getMonthProfit() {
		return monthProfit.get();
	}

	public void setMonthProfit( String monthProfit ) {
		this.monthProfit.set( monthProfit );
	}

	public String getName() {
		return name.get();
	}

	public void setName( String name ) {
		this.name.set( name );
	}

	public String getSales() {
		return sales.get();
	}

	public void setSales( String sales ) {
		this.sales.set( sales );
	}

	public String getPrice() {
		return price.get();
	}

	public void setPrice( String price ) {
		this.price.set( price );
	}

	public String getDateAdded() {
		return dateAdded.get();
	}

	public void setDateAdded( String dateAdded ) {
		this.dateAdded.set( dateAdded );
	}

	public String getDateModified() {
		return dateModified.get();
	}

	public void setDateModified( String dateModified ) {
		this.dateModified.set( dateModified );
	}

	public String getProfits() {
		return profits.get();
	}

	public void setProfits( String profits ) {
		this.profits.set( profits );
	}

	public String getId() {
		return id.get();
	}

	public void setId( String id ) {
		this.id.set( id );
	}
}