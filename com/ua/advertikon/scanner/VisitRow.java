package com.ua.advertikon.scanner;

import javafx.beans.property.SimpleStringProperty;
import com.ua.advertikon.helper.*;
import java.util.*;

public class VisitRow {
	private final SimpleStringProperty date;
	private final SimpleStringProperty ip;
	private final SimpleStringProperty country;
	private final SimpleStringProperty referrer;
	private final SimpleStringProperty id;
	private final SimpleStringProperty category;
	private final SimpleStringProperty license;
	private final SimpleStringProperty rating;
	private final SimpleStringProperty version;
	private final SimpleStringProperty vendor;
	private final SimpleStringProperty sort;
	private final SimpleStringProperty page;
	private final SimpleStringProperty search;

	VisitRow( Map<String, String> data ) {
		data = (HashMap<String, String>)data;

		this.date     = new SimpleStringProperty( data.getOrDefault( "date", "" ) );
		this.ip       = new SimpleStringProperty( data.getOrDefault( "ip", "" ) );
		this.country  = new SimpleStringProperty( data.getOrDefault( "country", "" ) );
		this.referrer = new SimpleStringProperty( data.getOrDefault( "referrer", "" ) );
		this.id       = new SimpleStringProperty( data.getOrDefault( "id" , "0" ) );
		this.category = new SimpleStringProperty( data.getOrDefault( "filter_category" ,"" ) );
		this.license  = new SimpleStringProperty( data.getOrDefault( "filter_license", "" ) );
		this.rating   = new SimpleStringProperty( data.getOrDefault( "filter_rating", "0" ) );
		this.version  = new SimpleStringProperty( data.getOrDefault( "filter_download_id", "" ) );
		this.vendor   = new SimpleStringProperty( data.getOrDefault( "filter_member_trype" , "" ) );
		this.sort     = new SimpleStringProperty( data.getOrDefault( "sort" , "" ) );
		this.page     = new SimpleStringProperty( data.getOrDefault( "page" , "0" ) );
		this.search   = new SimpleStringProperty( data.getOrDefault( "search" , "" ) );
	}

	public String getDate() {
		return date.get();
	}

	public void setDate( String date ) {
		this.date.set( date );
	}

	public String getIp() {
		return ip.get();
	}

	public void setIp( String ip ) {
		this.ip.set( ip );
	}

	public String getCountry() {
		return country.get();
	}

	public void setCountry( String contry ) {
		this.country.set( contry );
	}

	public String getReferrer() {
		return referrer.get();
	}

	public void setReferrer( String referrer ) {
		this.referrer.set( referrer );
	}

	public Integer getId() {
		return Integer.parseInt( id.get() );
	}

	public void setId( String id ) {
		this.id.set( id );
	}

	public String getCategory() {
		return category.get();
	}

	public void setCategory( String category ) {
		this.category.set( category );
	}

	public String getLicense() {
		return license.get();
	}

	public void setLicense( String license ) {
		this.license.set( license );
	}

	public String getRating() {
		return rating.get();
	}

	public void setRatig( String rating ) {
		this.rating.set( rating );
	}

	public String getVersion() {
		return version.get();
	}

	public void seVersion( String version ) {
		this.version.set( version );
	}

	public String getVendor() { 
		return vendor.get();
	}

	public void setVendor( String vendor ) {
		this.vendor.set( vendor );
	}

	public String getSort() { 
		return sort.get();
	}

	public void setSort( String sort ) {
		this.sort.set( sort );
	}

	public int getPage() { 
		return Integer.parseInt( page.get() );
	}

	public void setPage( String page ) {
		this.page.set( page );
	}

	public String getSearch() { 
		return search.get();
	}

	public void setSearch( String search ) {
		this.search.set( search );
	}
}