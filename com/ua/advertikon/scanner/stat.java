package com.ua.advertikon.scanner;

import java.time.*;
import java.util.*;
// import java.lang.reflect.*;

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

import org.json.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.chart.*;

import java.sql.ResultSet;

import com.ua.advertikon.helper.*;

import java.sql.SQLException;

public class stat extends Application {
	private TableView<DataRow> table       = new TableView<>();
	private TableView<DataRow> freeTable   = new TableView<>();
	private TableView<VisitRow> visitsTable = new TableView<>();

	public LineChart<String, Number> commercialChart = null;
	public LineChart<String, Number> freeChart = null;
	public LineChart<String, Number> visitsChart = null;

	public stat_db db = new stat_db();

	private ObservableList<DataRow> data        = FXCollections.observableArrayList();
	private ObservableList<DataRow> freeData    = FXCollections.observableArrayList(); // To store unfiltered dataset
	private ObservableList<DataRow> _data       = FXCollections.observableArrayList();
	private ObservableList<DataRow> _freeData   = FXCollections.observableArrayList(); // To store unfiltered dataset
	private ObservableList<VisitRow> _visitsData = FXCollections.observableArrayList();
	private ObservableList<VisitRow> visitsData  = FXCollections.observableArrayList();

	private VBox leftPane = null;

	private final String DEFAULT_PERIOD = "Month";
	private final String DEFAULT_PROFIT = "300";
	private final String VISIT_URL = "https://oc.advertikon.com.ua/pixel.php?get_statistics=";

	private QueryData queryData = new QueryData( DEFAULT_PERIOD, DEFAULT_PROFIT );

	private int controlsIsDisabled = 0;

	public static void main( String[] args ) {
		launch( args );
	}

	public void init() {
		// Log.debug( "Initializing" );
	}

	public void start( Stage primaryStage ) {

		// Scene
		primaryStage.setTitle( "Statistics" );
		Group root = new Group();
		Scene scene = new Scene( root, 1000, 700 );
		scene.getStylesheets().add( "css/style.css" );
		primaryStage.setScene( scene );

		// Main layout
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft( getLeftPane() ); // Controls

		// Main content
		TabPane tabPane = new TabPane();
		
		// Commercial statistics table
		Tab statTab = new Tab( "Statistic" );
		statTab.setClosable( false );
		statTab.setContent( table );
		tabPane.getTabs().add( statTab );
		iniTable();

		// Free statistics table
		Tab freeStatTab = new Tab( "Free statistic" );
		freeStatTab.setClosable( false );
		freeStatTab.setContent( freeTable );
		tabPane.getTabs().add( freeStatTab );
		iniFreeTable();

		// Visits table
		Tab visitsTab = new Tab( "Visits" );
		visitsTab.setClosable( false );
		visitsTab.setContent( visitsTable );
		tabPane.getTabs().add( visitsTab );
		iniVisitsTable();

		setTableData(); // update both tables in sequence

		// Charts tab
		Tab graphTab = new Tab( "Graphics" );
		graphTab.setClosable( false );
		graphTab.setContent( new VBox( initCommercialChart(), initFreeChart(), initVisitsChart() ) );
		tabPane.getTabs().add( graphTab );

		// Size the main layout
		borderPane.prefHeightProperty().bind( scene.heightProperty() );
		borderPane.prefWidthProperty().bind( scene.widthProperty() );
		borderPane.setCenter( tabPane );

		root.getChildren().add( borderPane );
		primaryStage.show();

		initialRenderer();

		sysncVisits();
	}

	public void stop() {
		Log.debug( "Stop" );
	}

	/**
	 * Initializes commercial statistics table
	 * @return {void}
	 */
	protected void iniTable() {
		TableColumn<DataRow, String> chartColumn        = new TableColumn<DataRow, String>( "Chart" );
		TableColumn<DataRow, Integer> id                = new TableColumn<DataRow, Integer>( "ID" );
		TableColumn<DataRow, String> nameColumn         = new TableColumn<DataRow, String>( "Name" );
		TableColumn<DataRow, Integer> salesColumn       = new TableColumn<DataRow, Integer>( "Total sales" );
		TableColumn<DataRow, Integer> totalSalesColumn  = new TableColumn<DataRow, Integer>( "Sales" );
		TableColumn<DataRow, Double> priceColumn        = new TableColumn<DataRow, Double>( "Price" );
		TableColumn<DataRow, String> dateAddedColumn    = new TableColumn<DataRow, String>( "Date Added" );
		TableColumn<DataRow, String> dateModifiedColumn = new TableColumn<DataRow, String>( "Date Modified" );
		TableColumn<DataRow, Double> profits            = new TableColumn<DataRow, Double>( "Profits" );
		TableColumn<DataRow, Double> monthProfits       = new TableColumn<DataRow, Double>( "Month profits" );

		table.getColumns().addAll( chartColumn, id, nameColumn, salesColumn, totalSalesColumn, priceColumn, profits, monthProfits, dateAddedColumn, dateModifiedColumn );

		chartColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, String>( "chart" ) );
		nameColumn.setCellValueFactory(         new PropertyValueFactory<DataRow, String>( "name" ) );
		salesColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, Integer>( "sales" ) );
		totalSalesColumn.setCellValueFactory(   new PropertyValueFactory<DataRow, Integer>( "totalSales" ) );
		priceColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, Double>( "price" ) );
		dateAddedColumn.setCellValueFactory(    new PropertyValueFactory<DataRow, String>( "dateAdded" ) );
		dateModifiedColumn.setCellValueFactory( new PropertyValueFactory<DataRow, String>( "dateModified" ) );
		profits.setCellValueFactory(            new PropertyValueFactory<DataRow, Double>( "profits" ) );
		monthProfits.setCellValueFactory(       new PropertyValueFactory<DataRow, Double>( "monthProfits" ) );
		id.setCellValueFactory(                 new PropertyValueFactory<DataRow, Integer>( "id" ) );

		table.setRowFactory( ( TableView<DataRow> table ) -> {
			CustomTableRow row = new CustomTableRow( "commercial", this );

			return row;
		} );
	}

	/**
	 * Initializes free statistics table
	 * @return {void}
	 */
	protected void iniFreeTable() {
		TableColumn<DataRow, String> chartColumn        = new TableColumn<DataRow, String>( "Chart" );
		TableColumn<DataRow, Integer> id                = new TableColumn<DataRow, Integer>( "ID" );
		TableColumn<DataRow, String> nameColumn         = new TableColumn<DataRow, String>( "Name" );
		TableColumn<DataRow, Integer> salesColumn       = new TableColumn<DataRow, Integer>( "Total downloads" );
		TableColumn<DataRow, Integer> totalSalesColumn  = new TableColumn<DataRow, Integer>( "Downloads" );
		TableColumn<DataRow, String> dateAddedColumn    = new TableColumn<DataRow, String>( "Date Added" );
		TableColumn<DataRow, String> dateModifiedColumn = new TableColumn<DataRow, String>( "Date Modified" );

		freeTable.getColumns().addAll( chartColumn, id, nameColumn, salesColumn, totalSalesColumn, dateAddedColumn, dateModifiedColumn );

		chartColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, String>( "chart" ) );
		id.setCellValueFactory(                 new PropertyValueFactory<DataRow, Integer>( "id" ) );
		nameColumn.setCellValueFactory(         new PropertyValueFactory<DataRow, String>( "name" ) );
		salesColumn.setCellValueFactory(        new PropertyValueFactory<DataRow, Integer>( "sales" ) );
		totalSalesColumn.setCellValueFactory(   new PropertyValueFactory<DataRow, Integer>( "totalSales" ) );
		dateAddedColumn.setCellValueFactory(    new PropertyValueFactory<DataRow, String>( "dateAdded" ) );
		dateModifiedColumn.setCellValueFactory( new PropertyValueFactory<DataRow, String>( "dateModified" ) );

		freeTable.setRowFactory( ( TableView<DataRow> table ) -> {
			CustomTableRow row = new CustomTableRow( "free", this );

			return row;
		} );
	}

	/**
	 * Initializes visits table
	 * @return {void}
	 */
	protected void iniVisitsTable() {
		TableColumn<VisitRow, String> date       = new TableColumn<VisitRow, String>( "Date" );
		TableColumn<VisitRow, Integer> id        = new TableColumn<VisitRow, Integer>( "ID" );
		TableColumn<VisitRow, String> country    = new TableColumn<VisitRow, String>( "Country" );
		TableColumn<VisitRow, String> ip         = new TableColumn<VisitRow, String>( "IP" );
		TableColumn<VisitRow, String> search     = new TableColumn<VisitRow, String>( "Search" );
		TableColumn<VisitRow, String> referrer   = new TableColumn<VisitRow, String>( "Referrer" );
		TableColumn<VisitRow, Integer> page      = new TableColumn<VisitRow, Integer>( "Page" );
		TableColumn<VisitRow, String> category   = new TableColumn<VisitRow, String>( "Category" );
		TableColumn<VisitRow, String> version    = new TableColumn<VisitRow, String>( "Version" );
		TableColumn<VisitRow, String> license    = new TableColumn<VisitRow, String>( "License" );
		TableColumn<VisitRow, String> rating     = new TableColumn<VisitRow, String>( "Rating" );
		TableColumn<VisitRow, String> vendor     = new TableColumn<VisitRow, String>( "Vendor" );
		TableColumn<VisitRow, String> sort       = new TableColumn<VisitRow, String>( "Sort" );
		
		visitsTable.getColumns().addAll( date, id, country, ip, search, referrer, page, category, version, license, rating, vendor, sort );

		date.setCellValueFactory(     new PropertyValueFactory<VisitRow, String>( "date" ) );
		id.setCellValueFactory(       new PropertyValueFactory<VisitRow, Integer>( "id" ) );
		country.setCellValueFactory(  new PropertyValueFactory<VisitRow, String>( "country" ) );
		ip.setCellValueFactory(       new PropertyValueFactory<VisitRow, String>( "ip" ) );
		search.setCellValueFactory(   new PropertyValueFactory<VisitRow, String>( "search" ) );
		referrer.setCellValueFactory( new PropertyValueFactory<VisitRow, String>( "referrer" ) );
		page.setCellValueFactory(     new PropertyValueFactory<VisitRow, Integer>( "page" ) );
		category.setCellValueFactory( new PropertyValueFactory<VisitRow, String>( "category" ) );
		version.setCellValueFactory(  new PropertyValueFactory<VisitRow, String>( "version" ) );
		license.setCellValueFactory(  new PropertyValueFactory<VisitRow, String>( "license" ) );
		rating.setCellValueFactory(   new PropertyValueFactory<VisitRow, String>( "rating" ) );
		vendor.setCellValueFactory(   new PropertyValueFactory<VisitRow, String>( "vendor" ) );
		sort.setCellValueFactory(     new PropertyValueFactory<VisitRow, String>( "sort" ) );

		// freeTable.setRowFactory( ( TableView<DataRow> table ) -> {
		// 	CustomTableRow row = new CustomTableRow( "free", this );

		// 	return row;
		// } );
	}

	/**
	 * Populates the commercial statistics table's dataset
	 * @return {void}
	 */
	protected void setTableData() {
		disableControls( true );
		data.clear();
		_data.clear();

		for ( Map<String, String> row: db.getStatisticData( queryData ) ) {
			data.add( new DataRow( row ) );
		}

		_data.addAll( data );
		table.setItems( data );
		disableControls( false );
	}

	/**
	 * Populates the free statistics table's dataset
	 * @return {void}
	 */
	protected void setFreeTableData() {
		disableControls( true );
		freeData.clear();
		_freeData.clear();

		for ( Map<String, String> row: db.getFreeStatisticData( queryData ) ) {
			freeData.add( new DataRow( row ) );
		}

		_freeData.addAll( freeData );
		freeTable.setItems( freeData );
		disableControls( false );
	}

	/**
	 * Populates the visits statistics table's dataset
	 * @return {void}
	 */
	protected void setVisitsTableData() {
		disableControls( true );
		visitsData.clear();
		_visitsData.clear();

		for ( Map<String, String> row: db.getVisitsStatisticData( queryData ) ) {
			visitsData.add( new VisitRow( row ) );
		}

		_visitsData.addAll( visitsData );
		visitsTable.setItems( visitsData );
		disableControls( false );

		Platform.runLater( () -> {
			// public void run() {
				addVisitsToChart();
			// }
		} );
	}

	/**
	 * Initializes left panel - filter controls
	 * @return {VBox} VBox layout object
	 */
	protected VBox getLeftPane() {
		VBox pane = new VBox();
		leftPane = pane;
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

			initialRenderer();
		} );

		combo.setValue( DEFAULT_PERIOD );
		pane.getChildren().add( combo );

		// Date FROM
		DatePicker dateFrom = new DatePicker();
		DatePicker dateTo = new DatePicker();

		dateFrom.setOnAction( ( e ) -> {
			LocalDate dFrom = dateFrom.getValue();
			LocalDate dTo   = dateTo.getValue();
			queryData.dateFrom = dFrom != null ? dFrom.toString() : "";
			queryData.dateTo   = dTo != null ? dTo.toString() : "";

			// If we have dateTo and dateFrom and dateTo > dateFrom - refresh the table data
			if ( !queryData.dateTo.equals( "" ) && dFrom.compareTo( dTo ) <= 0 ) {
				queryData.period = "";
				initialRenderer();
			}
		} );

		pane.getChildren().addAll( new Label( "Date from:" ), dateFrom );

		// Date TO
		dateTo.setOnAction( ( e ) -> {
			LocalDate dFrom    = dateFrom.getValue();
			LocalDate dTo      = dateTo.getValue();
			queryData.dateFrom = dFrom != null ? dFrom.toString() : "";
			queryData.dateTo   = dTo != null ? dTo.toString() : "";

			// If we have dateTo and dateFrom and dateTo > dateFrom - refresh the table data
			if ( !queryData.dateFrom.equals( "" ) && dTo.compareTo( dFrom ) >= 0 ) {
				queryData.period = "";
				initialRenderer();
			}
		} );

		pane.getChildren().addAll( new Label( "Date to:" ), dateTo );

		// Profits per month
		TextField profits = new TextField( "300" );

		profits.setOnAction( ( e ) -> {
			if ( !profits.getText().equals( "" ) ) {
				queryData.profits = profits.getText();
				initialRenderer();
			}
		} );

		pane.getChildren().addAll( new Label( "Profits: " ), profits );

		// Modules filter
		TextField filter = new TextField();

		filter.setOnAction( ( e ) -> {
			String search = filter.getText();

			data.clear();
			freeData.clear();
			
			if ( search.equals( "" ) ) {
				Log.debug( "Clear filter" );

				data.addAll( _data );
				freeData.addAll( _freeData );

			} else {
				Log.debug( String.format( "Filtering by '%s'", search ) );

				for( DataRow row: _data ) {
					if ( row.getName().contains( search ) ) {
						Log.debug( String.format( "Commercial filter: %s", row.getName() ) );

						data.add( row );
					}
				}

				for( DataRow row: _freeData ) {
					if ( row.getName().contains( search ) ) {
						freeData.add( row );
					}
				}
			}

			table.setItems( data );
			freeTable.setItems( freeData );
			updateCharts();
		} );

		pane.getChildren().addAll( new Label( "Filter: " ), filter );

		// Chart modules filter
		Button chartModules = new Button( "Chart modules" );

		chartModules.setOnAction( ( e ) -> {
			final String ON_TEXT  = "All modules";
			final String OFF_TEXT = "Chart modules";
			data.clear();
			freeData.clear();
			
			if ( chartModules.getText().equals( ON_TEXT ) ) {
				Log.debug( "Clear chart filter" );

				data.addAll( _data );
				freeData.addAll( _freeData );

				chartModules.setText( OFF_TEXT );

			} else {
				Log.debug( "Filtering chart modules" );

				for( DataRow row: _data ) {
					if ( !row.getChart().equals( "" ) ) {
						data.add( row );
					}
				}

				for( DataRow row: _freeData ) {
					if ( !row.getChart().equals( "" ) ) {
						freeData.add( row );
					}
				}

				chartModules.setText( ON_TEXT );
			}

			table.setItems( data );
			freeTable.setItems( freeData );
			updateCharts();
		} );

		pane.getChildren().add( chartModules );

		return pane;
	}

	/**
	 * Initializes commercial statistics chart
	 * @return {Chart} Chart object
	 */
	protected Chart initCommercialChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis   = new NumberAxis();
		commercialChart = new LineChart<String, Number>( xAxis, yAxis );

		return commercialChart;
	}

	/**
	 * Initializes free statistics chart
	 * @return {Chart} Chart object
	 */
	protected Chart initFreeChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis   = new NumberAxis();
		freeChart = new LineChart<String, Number>( xAxis, yAxis );

		return freeChart;
	}

	/**
	 * Initializes visits statistics chart
	 * @return {Chart} Chart object
	 */
	protected Chart initVisitsChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis   = new NumberAxis();
		visitsChart = new LineChart<String, Number>( xAxis, yAxis );
		visitsChart.setCreateSymbols( false );

		return visitsChart;
	}

	/**
	 * Adds visits to chart
	 * @param {String} id Module ID
	 * @param {Chart} chart Chart to add data to
	 * @return {void}
	 */
	public void addVisitsToChart() {
		String[] modules = { "26894", "27789" };
		XYChart.Series<String, Number> series = null;

		visitsChart.getData().clear();

		for( int i = 0; i < modules.length; i++ ) {
			series = new XYChart.Series<>();
			series.setName( modules[ i ] );

			for ( Map<String, String> row: db.getVisits( modules[ i ], queryData ) ) {
				series.getData().add( new XYChart.Data<String, Number>( row.get( "f_date" ), Integer.parseInt( row.getOrDefault( "count", "0" ) ) ) );
			}

			visitsChart.getData().add( series );
		}
	}


	/**
	 * Adds module to chart
	 * @param {String} id Module ID
	 * @param {Chart} chart Chart to add data to
	 * @return {void}
	 */
	public void addSeriesToChart( String id, LineChart<String, Number> chart ) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName( id );

		for ( Map<String, String> row: db.getModule( id, queryData ) ) {
			series.getData().add( new XYChart.Data<String, Number>( row.get( "date" ), Double.parseDouble( row.getOrDefault( "sales", "0" ) ) ) );
		}

		chart.getData().add( series );
	}

	/**
	 * Removes module from chart
	 * @param {String} id MOdule ID
	 * @param {Chart} chart Chart to remove module from
	 * @return {void}
	 */
	public void removeSeriesFromChart( String id, LineChart<String, Number> chart ) {
		for ( XYChart.Series<String, Number> series: chart.getData() ) {
			if ( series.getName().equals( id ) ) {
				Platform.runLater( new Runnable() {
					public void run() {
						chart.getData().remove( series );
					}
				} );
			}
		}
	}

	/**
	 * Adds all from statistic table which is already in chart
	 * @param {Chart} chart Target chart
	 * @param {String} chartName Commercial|Free
	 * @return {void}
	 */
	protected void addAllSeriesToChart( LineChart<String, Number> chart, String chartName ) {
		TableView<DataRow> t = chartName == "free" ? freeTable : table;
		chart.getData().clear();

		for ( DataRow row: t.getItems() ) {
			if ( !row.getChart().equals( "" ) ) {
				addSeriesToChart( String.valueOf( row.getId() ), chart );
			}
		}
	}

	/**
	 * Enables/disables left control panel regarding multiple calls
	 * @param {boolean} status Flag defined if panel should be enabled or disabled
	 * @return {void}
	 */
	synchronized public void disableControls( boolean status ) {
		if ( status ) {
			if ( controlsIsDisabled == 0 ) {
				leftPane.setDisable( true );
				controlsIsDisabled++;
			}

		} else {
			controlsIsDisabled--;

			if ( controlsIsDisabled == 0 ) {
				leftPane.setDisable( false );
			}
		}
	}

	/**
	 * Central place to manage data
	 * @return {void}
	 */
	protected void initialRenderer() {
		new Thread( () -> {
			setTableData();
			setFreeTableData();
			setVisitsTableData();
			updateCharts();

		} ).start();
	}

	protected void updateCharts() {
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				addAllSeriesToChart( commercialChart, "commercial" );
				addAllSeriesToChart( freeChart, "free" );
			}
		} );
	}

	protected void sysncVisits() {
		new Thread( () -> {
			URL url = null;
			HttpURLConnection connection = null;
			InputStream stream  = null;
			String line = "";
			StringBuffer data_string = new StringBuffer();
			// String out = "";
			BufferedReader reader = null;
			String[] lineData = null;
			int count = 0;
			String since = "null";

			Log.debug( "Updating visits..." );

			since = db.getLastVisit();

			Log.debug( "Since: " + since );

			try {
				url = new URL( VISIT_URL + URLEncoder.encode( since, "UTF-8" ) );
				Log.debug( "URL: " + url );
				
			} catch ( MalformedURLException e ) {
				Log.error( e );

				return;
			} catch ( UnsupportedEncodingException e ) {
				Log.error( e );

				return;
			}

			try {
				connection = (HttpURLConnection)url.openConnection();
				// stream = connection.getInputStream();
				reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );

				Log.debug( "View statistics source. Response message: " + connection.getResponseMessage() );

				while ( null != ( line = reader.readLine() ) ) {
					lineData = line.split( "\t" );

					if( db.saveVisit( lineData ) )
						count++;
				}

				Log.debug( String.format( "%d records have been imported", count ) );

				reader.close();

				if ( count > 0 ) {
					setVisitsTableData();
				}
					
			} catch ( IOException e ) {
				Log.error( e );

			} 
			
		} ).start();
	}
}

