package com.ua.advertikon.scanner;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.*;

import javafx.geometry.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;

public class stat extends Application {
	private TableView table                = new TableView();
	private TableColumn dateColumn         = new TableColumn( "Date" );
	private TableColumn nameColumn         = new TableColumn( "Name" );
	private TableColumn salesColumn        = new TableColumn( "Sales" );
	private TableColumn priceColumn        = new TableColumn( "Price" );
	private TableColumn dateAddedColumn    = new TableColumn( "Date Added" );
	private TableColumn dateModifiedColumn = new TableColumn( "Date Modified" );

	private stat_db db = new stat_db();
	private ObservableList<DataRow> data = FXCollections.observableArrayList();

	private final String DEFAULT_PERIOD = "Month";

	private QueryData queryData = new QueryData( DEFAULT_PERIOD );

	public static void main( String[] args ) {
		launch( args );
	}

	public void init() {
		System.out.println( "Initializing" );
	}

	public void start( Stage primaryStage ) {
		System.out.println( "Run module" );

		primaryStage.setTitle( "Statistics" );
		Group root = new Group();
		Scene scene = new Scene( root, 700, 700 );
		scene.getStylesheets().add( "css/style.css" );
		primaryStage.setScene( scene );

		BorderPane borderPane = new BorderPane();

		TabPane tabPane = new TabPane();
		
		Tab statTab = new Tab( "Statistic" );
		statTab.setClosable( false );
		statTab.setContent( table );
		tabPane.getTabs().add( statTab );
		iniTable();
		setTableData();

		Tab graphTab = new Tab( "Graphics" );
		graphTab.setClosable( false );
		HBox graphHBox = new HBox();
		graphHBox.getChildren().add( new Label( "Graphics" ) );
		graphHBox.setAlignment( Pos.CENTER );
		graphTab.setContent( graphHBox );
		tabPane.getTabs().add( graphTab );

		borderPane.prefHeightProperty().bind( scene.heightProperty() );
        borderPane.prefWidthProperty().bind( scene.widthProperty() );
        
        borderPane.setCenter( tabPane );
        borderPane.setLeft( getLeftPane() );
        root.getChildren().add( borderPane );

		primaryStage.show();
	}

	public void stop() {
		System.out.println( "Stop" );
	}

	protected void iniTable() {
		table.getColumns().addAll( dateColumn, nameColumn, salesColumn, priceColumn, dateAddedColumn, dateModifiedColumn );

		dateColumn.setCellValueFactory(         new PropertyValueFactory<DataRow, String>( "date" ) );
		nameColumn.setCellValueFactory(         new PropertyValueFactory<DataRow, String>( "name" ) );
		salesColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, String>( "sales" ) );
		priceColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, String>( "price" ) );
		dateAddedColumn.setCellValueFactory(    new PropertyValueFactory<DataRow, String>( "dateAdded" ) );
		dateModifiedColumn.setCellValueFactory( new PropertyValueFactory<DataRow, String>( "dateModified" ) );
	}

	protected void setTableData() {
		try ( ResultSet rs = db.getStatisticData( queryData ) ) {
			if ( null == rs ) {
				throw new Exception( "Result set is empty" );
			}

			data.clear();

			do {
				data.add( new DataRow(
					rs.getString( "date" ),
					rs.getString( "name" ),
					rs.getString( "price" ),
					rs.getString( "sales" ),
					rs.getString( "date_added" ),
					rs.getString( "date_modified" )
				) );

			} while( rs.next() );

			table.setItems( data );

		} catch ( Exception e ) {
			System.out.println( "stat::setTableData: " + e.getMessage() );
		}
	}

	protected VBox getLeftPane() {
		VBox pane = new VBox();
		pane.getStyleClass().add( "left-pane" );

		// Combo box to select the period
		ComboBox<String> combo = new ComboBox<>();
		combo.getItems().addAll( "Day", "Week", "Month", "Year" );

		// Set callback
		combo.setOnAction( ( e ) -> {
			System.out.println( combo.getValue() );

			queryData.period = combo.getValue();
			queryData.dateFrom = "";
			queryData.dateTo = "";

			setTableData();
		} );

		combo.setValue( DEFAULT_PERIOD );
		pane.getChildren().add( combo );

		return pane;
	}
}

class QueryData {
	public String period   = "";
	public String dateFrom = "";
	public String dateTo   = "";

	QueryData( String period ) {
		this.period = period;
	}
}