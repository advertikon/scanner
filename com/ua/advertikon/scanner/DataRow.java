package com.ua.advertikon.scanner;

import javafx.beans.property.SimpleStringProperty;

public class DataRow {
	private final SimpleStringProperty date;
	private final SimpleStringProperty name;
	private final SimpleStringProperty sales;
	private final SimpleStringProperty price;
	private final SimpleStringProperty dateAdded;
	private final SimpleStringProperty dateModified;

	DataRow( String date, String name, String price, String sales, String dateAdded, String dateModified ) {
		this.date         = new SimpleStringProperty( date );
		this.name         = new SimpleStringProperty( name );
		this.sales        = new SimpleStringProperty( sales );
		this.price        = new SimpleStringProperty( price );
		this.dateAdded    = new SimpleStringProperty( dateAdded );
		this.dateModified = new SimpleStringProperty( dateModified );
	}

	public String getDate() {
		return date.get();
	}

	public void setDate( String date ) {
		this.date.set( date );
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
}