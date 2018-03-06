package com.ua.advertikon.scanner;

import java.time.*;

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
	private TableView<DataRow> table     = new TableView<>();
	private TableView<DataRow> freeTable = new TableView<>();

	private stat_db db = new stat_db();
	private ObservableList<DataRow> data = FXCollections.observableArrayList();

	private final String DEFAULT_PERIOD = "Month";
	private final String DEFAULT_PROFIT = "300";

	private QueryData queryData = new QueryData( DEFAULT_PERIOD, DEFAULT_PROFIT );

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
		Scene scene = new Scene( root, 1000, 700 );
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

		Tab freeStatTab = new Tab( "Free statistic" );
		freeStatTab.setClosable( false );
		freeStatTab.setContent( freeTable );
		tabPane.getTabs().add( freeStatTab );
		iniFreeTable();
		setFreeTableData();

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
		TableColumn<DataRow, String> id                 = new TableColumn<DataRow, String>( "ID" );
		// TableColumn<DataRow, String> monthProfitColumn  = new TableColumn<DataRow, String>( "Month profits" );
		TableColumn<DataRow, String> nameColumn         = new TableColumn<DataRow, String>( "Name" );
		TableColumn<DataRow, String> salesColumn        = new TableColumn<DataRow, String>( "Sales" );
		TableColumn<DataRow, String> priceColumn        = new TableColumn<DataRow, String>( "Price" );
		TableColumn<DataRow, String> dateAddedColumn    = new TableColumn<DataRow, String>( "Date Added" );
		TableColumn<DataRow, String> dateModifiedColumn = new TableColumn<DataRow, String>( "Date Modified" );
		TableColumn<DataRow, String> profits            = new TableColumn<DataRow, String>( "Profits" );

		table.getColumns().addAll( id, nameColumn, salesColumn, priceColumn, profits, dateAddedColumn, dateModifiedColumn );

		// monthProfitColumn.setCellValueFactory(  new PropertyValueFactory<DataRow, String>( "monthProfit" ) );
		nameColumn.setCellValueFactory(         new PropertyValueFactory<DataRow, String>( "name" ) );
		salesColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, String>( "sales" ) );
		priceColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, String>( "price" ) );
		dateAddedColumn.setCellValueFactory(    new PropertyValueFactory<DataRow, String>( "dateAdded" ) );
		dateModifiedColumn.setCellValueFactory( new PropertyValueFactory<DataRow, String>( "dateModified" ) );
		profits.setCellValueFactory(            new PropertyValueFactory<DataRow, String>( "profits" ) );
		id.setCellValueFactory(                 new PropertyValueFactory<DataRow, String>( "id" ) );
	}

	protected void iniFreeTable() {
		TableColumn<DataRow, String> id                 = new TableColumn<DataRow, String>( "ID" );
		TableColumn<DataRow, String> nameColumn         = new TableColumn<DataRow, String>( "Name" );
		TableColumn<DataRow, String> salesColumn        = new TableColumn<DataRow, String>( "Sales" );
		TableColumn<DataRow, String> dateAddedColumn    = new TableColumn<DataRow, String>( "Date Added" );
		TableColumn<DataRow, String> dateModifiedColumn = new TableColumn<DataRow, String>( "Date Modified" );

		freeTable.getColumns().addAll( id, nameColumn, salesColumn, dateAddedColumn, dateModifiedColumn );

		id.setCellValueFactory(                 new PropertyValueFactory<DataRow, String>( "id" ) );
		nameColumn.setCellValueFactory(         new PropertyValueFactory<DataRow, String>( "name" ) );
		salesColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, String>( "sales" ) );
		dateAddedColumn.setCellValueFactory(    new PropertyValueFactory<DataRow, String>( "dateAdded" ) );
		dateModifiedColumn.setCellValueFactory( new PropertyValueFactory<DataRow, String>( "dateModified" ) );
	}

	protected void setTableData() {
		class Async extends Thread {
			Async( String name ) {
				super( name );
				start();
			}

			public void run() {
				try ( ResultSet rs = db.getStatisticData( queryData ) ) {
					if ( null == rs ) {
						throw new Exception( "Result set is empty" );
					}

					data.clear();

					while ( rs.next() ) {
						data.add( new DataRow(
							"",
							rs.getString( "name" ),
							rs.getString( "price" ),
							rs.getString( "sales" ),
							rs.getString( "date_added" ),
							rs.getString( "date_modified" ),
							rs.getString( "profits" ),
							rs.getString( "id" )
						) );
					}

					table.setItems( data );

				} catch ( Exception e ) {
					System.out.println( "stat::setTableData: " + e.getMessage() );
				}
			}
		}

		// Run in separate thread
		new Async( "setTableData" );
	}

	protected void setFreeTableData() {
		class Async extends Thread {
			Async( String name ) {
				super( name );
				start();
			}

			public void run() {
				try ( ResultSet rs = db.getFreeStatisticData( queryData ) ) {
					if ( null == rs ) {
						throw new Exception( "Result set is empty" );
					}

					data.clear();

					while ( rs.next() ) {
						data.add( new DataRow(
							"",
							rs.getString( "name" ),
							"",
							rs.getString( "total_sales" ),
							rs.getString( "date_added" ),
							rs.getString( "date_modified" ),
							"",
							rs.getString( "id" )
						) );
					}

					freeTable.setItems( data );

				} catch ( Exception e ) {
					System.out.println( "stat::setFreeTableData: " + e.getMessage() );
				}
			}
		}

		// Run in separate thread
		new Async( "setFreeTableData" );
	}

	/**
	 * Initializes left panel - filter controls
	 * @return {VBox} VBox layout object
	 */
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

		// Date FROM
		DatePicker dateFrom = new DatePicker();

		dateFrom.setOnAction( ( e ) -> {
			LocalDate date = null;
			date = dateFrom.getValue();
			queryData.dateFrom = date.toString();

			// If we have dateTo and dateFrom and dateTo > dateFrom - refresh the table data
			if ( !queryData.dateTo.equals( "" ) && date.compareTo( LocalDate.parse( queryData.dateTo ) ) < 0 ) {
				queryData.period = "";
				setTableData();
			}
		} );

		pane.getChildren().addAll( new Label( "Date from:" ), dateFrom );

		// Date TO
		DatePicker dateTo = new DatePicker();

		dateTo.setOnAction( ( e ) -> {
			LocalDate date = null;
			date = dateTo.getValue();
			queryData.dateTo = date.toString();

			// If we have dateTo and dateFrom and dateTo > dateFrom - refresh the table data
			if ( !queryData.dateFrom.equals( "" ) && date.compareTo( LocalDate.parse( queryData.dateFrom ) ) > 0 ) {
				queryData.period = "";
				setTableData();
			}
		} );

		pane.getChildren().addAll( new Label( "Date to:" ), dateTo );

		// Profits per month
		TextField profits = new TextField( "300" );

		profits.setOnAction( ( e ) -> {
			if ( !profits.getText().equals( "" ) ) {
				queryData.profits = profits.getText();
				setTableData();
			}
		} );

		pane.getChildren().addAll( new Label( "Profits per month: " ), profits );

		return pane;
	}
}

class QueryData {
	public String period   = "";
	public String dateFrom = "";
	public String dateTo   = "";
	public String profits  = "";
	public String limit    = "50";

	QueryData( String period, String profits ) {
		this.period = period;
		this.profits = profits;
	}
}