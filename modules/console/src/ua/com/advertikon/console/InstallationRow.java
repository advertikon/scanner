package ua.com.advertikon.console;

import javafx.beans.property.SimpleStringProperty;
import java.util.*;

public class InstallationRow {
	private final SimpleStringProperty id;
	private final SimpleStringProperty name;
	private final SimpleStringProperty dateCreated;
	private final SimpleStringProperty dateModified;
	private final SimpleStringProperty code;
	private final SimpleStringProperty version;
	private final SimpleStringProperty localhost;
	private final SimpleStringProperty ocVersion;
	private final SimpleStringProperty country;

	InstallationRow( Map<String, String> data ) {
		data = (HashMap<String, String>)data;

		this.id           = new SimpleStringProperty( data.getOrDefault( "id", "" ) );
		this.name         = new SimpleStringProperty( data.getOrDefault( "name", "" ) );
		this.dateCreated  = new SimpleStringProperty( data.getOrDefault( "date_created", "" ) );
		this.dateModified = new SimpleStringProperty( data.getOrDefault( "date_modified", "" ) );
		this.code         = new SimpleStringProperty( data.getOrDefault( "code" , "" ) );
		this.version      = new SimpleStringProperty( data.getOrDefault( "version" ,"" ) );
		this.localhost    = new SimpleStringProperty( data.getOrDefault( "localhost", "" ) );
		this.ocVersion    = new SimpleStringProperty( data.getOrDefault( "oc_version", "" ) );
		this.country      = new SimpleStringProperty( data.getOrDefault( "country", "" ) );
	}

	public int getId() {
		return Integer.parseInt( id.get() );
	}

	public void setId( String data ) {
		id.set( data );
	}

	public String getName() {
		return name.get();
	}

	public void setName( String data ) {
		name.set( data );
	}

	public String getDateCreated() {
		return dateCreated.get();
	}

	public void setDateCreated( String data ) {
		dateCreated.set( data );
	}

	public String getDateModified() {
		return dateModified.get();
	}

	public void setDateModified( String data ) {
		dateModified.set( data );
	}

	public String getCode() {
		return code.get();
	}

	public void setCode( String data ) {
		code.set( data );
	}

	public String getVersion() {
		return version.get();
	}

	public void setVersion( String data ) {
		version.set( data );
	}

	public String getLocalhost() {
		return localhost.get();
	}

	public void setLocalhost( String data ) {
		localhost.set( data );
	}

	public String getOcVersion() {
		return ocVersion.get();
	}

	public void setOcVersion( String data ) {
		ocVersion.set( data );
	}

	public String getCountry() {
		return country.get();
	}

	public void setCountry( String data ) {
		country.set( data );
	}
}