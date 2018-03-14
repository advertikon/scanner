package com.ua.advertikon.console;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.util.*;
import java.net.*;
import java.io.*;

import java.time.*;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import com.ua.advertikon.helper.*;

public class Console extends Application {
	private TableView<InstallationRow> installationTable = new TableView<>();

	private ObservableList<InstallationRow> installationData = FXCollections.observableArrayList();

	private ConsoleDb db = new ConsoleDb();
	private ConsoleModel model = new ConsoleModel();

	static public void main( String[] args ) {
		launch( args );
	}

	public void start( Stage stage ) {

		// Scene
		stage.setTitle( "Statistics" );
		Group root = new Group();
		Scene scene = new Scene( root, 1000, 700 );
		// scene.getStylesheets().add( "css/style.css" );
		stage.setScene( scene );

		// Main layout
		BorderPane borderPane = new BorderPane();
		// borderPane.setLeft( getLeftPane() ); // Controls

		// Main content
		TabPane tabPane = new TabPane();
		
		// Commercial statistics table
		tabPane.getTabs().add( initIntallationTab() );
		iniInstallationTable();
		setInstallationTableData();

		// Free statistics table
		// Tab freeStatTab = new Tab( "Free statistic" );
		// freeStatTab.setClosable( false );
		// freeStatTab.setContent( freeTable );
		// tabPane.getTabs().add( freeStatTab );
		// iniFreeTable();

		// Size the main layout
		borderPane.prefHeightProperty().bind( scene.heightProperty() );
		borderPane.prefWidthProperty().bind( scene.widthProperty() );
		borderPane.setCenter( tabPane );

		root.getChildren().add( borderPane );
		stage.show();
	}

	public Tab initIntallationTab() {
		Tab tab = new Tab( "Installations" );
		tab.setClosable( false );
		tab.setContent( installationTable );
		
		return tab;
	}

	/**
	 * Initializes commercial statistics table
	 * @return {void}
	 */
	protected void iniInstallationTable() {
		TableColumn<InstallationRow, Integer> id          = new TableColumn<InstallationRow, Integer>( "ID" );
		TableColumn<InstallationRow, String> name         = new TableColumn<InstallationRow, String>( "Name" );
		TableColumn<InstallationRow, String> code         = new TableColumn<InstallationRow, String>( "Code" );
		TableColumn<InstallationRow, String> version      = new TableColumn<InstallationRow, String>( "Version" );
		TableColumn<InstallationRow, String> ocVersion    = new TableColumn<InstallationRow, String>( "OC Version" );
		TableColumn<InstallationRow, String> dateAdded    = new TableColumn<InstallationRow, String>( "Created" );
		TableColumn<InstallationRow, String> dateModified = new TableColumn<InstallationRow, String>( "Last access" );
		TableColumn<InstallationRow, String> localhost    = new TableColumn<InstallationRow, String>( "Localhost" );
		TableColumn<InstallationRow, String> country      = new TableColumn<InstallationRow, String>( "Country" );

		installationTable.getColumns().addAll( id, code, name, version, ocVersion, dateModified, dateAdded, country );

		id.setCellValueFactory(           new PropertyValueFactory<InstallationRow, Integer>( "id" ) );
		name.setCellValueFactory(         new PropertyValueFactory<InstallationRow, String>( "name" ) );
		code.setCellValueFactory(         new PropertyValueFactory<InstallationRow, String>( "code" ) );
		version.setCellValueFactory(      new PropertyValueFactory<InstallationRow, String>( "version" ) );
		ocVersion.setCellValueFactory(    new PropertyValueFactory<InstallationRow, String>( "ocVersion" ) );
		dateAdded.setCellValueFactory(    new PropertyValueFactory<InstallationRow, String>( "dateCreated" ) );
		dateModified.setCellValueFactory( new PropertyValueFactory<InstallationRow, String>( "dateModified" ) );
		localhost.setCellValueFactory(    new PropertyValueFactory<InstallationRow, String>( "localhost" ) );
		country.setCellValueFactory(      new PropertyValueFactory<InstallationRow, String>( "country" ) );

		installationTable.setRowFactory( ( TableView<InstallationRow> table ) -> {
			InstallationTableRow row = new InstallationTableRow( this );

			return row;
		} );

		dateModified.setCellFactory( ( TableColumn<InstallationRow, String> column ) -> {
			return new DateModifiedCell();
		} );
	}

	/**
	 * Populates the commercial statistics table's dataset
	 * @return {void}
	 */
	protected void setInstallationTableData() {
		new Thread( () -> {
			// disableControls( true );
			installationData.clear();
			// _data.clear();

			for ( Map<String, String> row: model.getInstallation() ) {
				installationData.add( new InstallationRow( row ) );
			}

			// _data.addAll( data );
			installationTable.setItems( installationData );
			// disableControls( false );
			
		} ).start();
	}
}