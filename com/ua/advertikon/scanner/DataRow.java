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
	private final SimpleStringProperty totalSales;

	DataRow( String monthProfit, String name, String price, String sales, String dateAdded, String dateModified, String profits, String id, String totalSales ) {
		this.monthProfit  = new SimpleStringProperty( monthProfit );
		this.name         = new SimpleStringProperty( name );
		this.sales        = new SimpleStringProperty( sales );
		this.price        = new SimpleStringProperty( price );
		this.dateAdded    = new SimpleStringProperty( dateAdded );
		this.dateModified = new SimpleStringProperty( dateModified );
		this.profits      = new SimpleStringProperty( profits );
		this.id           = new SimpleStringProperty( id );
		this.totalSales   = new SimpleStringProperty( totalSales );
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

	public Integer getSales() {
		return Integer.parseInt( sales.get() );
	}

	public void setSales( String sales ) {
		this.sales.set( sales );
	}

	public Integer getTotalSales() {
		return Integer.parseInt( totalSales.get() );
	}

	public void setTotalSales( String totalSales ) {
		this.totalSales.set( totalSales );
	}

	public Double getPrice() {
		return Double.parseDouble( price.get() );
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

	public Double getProfits() {
		return Double.parseDouble( profits.get() );
	}

	public void setProfits( String profits ) {
		this.profits.set( profits );
	}

	public Integer getId() {
		return Integer.parseInt( id.get() );
	}

	public void setId( String id ) {
		this.id.set( id );
	}
}