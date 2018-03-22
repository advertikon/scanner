package ua.com.advertikon.scanner;

import javafx.beans.property.SimpleStringProperty;
import java.util.*;

public class DataRow {
	private final SimpleStringProperty monthProfits;
	private final SimpleStringProperty name;
	private final SimpleStringProperty sales;
	private final SimpleStringProperty price;
	private final SimpleStringProperty dateAdded;
	private final SimpleStringProperty dateModified;
	private final SimpleStringProperty profits;
	private final SimpleStringProperty id;
	private final SimpleStringProperty totalSales;
	private final SimpleStringProperty chart;

	final private String CHART_SYMBOL = "*";

	DataRow( Map<String, String> data ) {
		data = (HashMap<String, String>)data;

		this.chart        = new SimpleStringProperty( data.getOrDefault( "chart", "" ).equals( "" ) ? "" : CHART_SYMBOL );
		this.name         = new SimpleStringProperty( data.getOrDefault( "name", "" ) );
		this.sales        = new SimpleStringProperty( data.getOrDefault( "sales", "0" ) );
		this.price        = new SimpleStringProperty( data.getOrDefault( "price", "0" ) );
		this.dateAdded    = new SimpleStringProperty( data.getOrDefault( "date_added" , "" ) );
		this.dateModified = new SimpleStringProperty( data.getOrDefault( "date_modified" ,"" ) );
		this.profits      = new SimpleStringProperty( data.getOrDefault( "profits", "0" ) );
		this.monthProfits = new SimpleStringProperty( data.getOrDefault( "month_profits", "0" ) );
		this.id           = new SimpleStringProperty( data.getOrDefault( "id", "0" ) );
		this.totalSales   = new SimpleStringProperty( data.getOrDefault( "total_sales" , "0" ) );
	}

	public String getChart() {
		return chart.get();
	}

	public void setChart( boolean chart ) {
		this.chart.set( chart ? CHART_SYMBOL :  "" );
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

	public Double getMonthProfits() {
		return Double.parseDouble( monthProfits.get() );
	}

	public void setMonthProfits( String monthProfits ) {
		this.monthProfits.set( monthProfits );
	}

	public int getId() { 
		return Integer.parseInt( id.get() );
	}

	public void setId( String id ) {
		this.id.set( id );
	}

	@Override
	public String toString() {
		return "DataRow";
	}
}