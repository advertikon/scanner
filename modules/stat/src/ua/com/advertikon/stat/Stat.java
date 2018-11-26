package ua.com.advertikon.stat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import ua.com.advertikon.helper.*;

public class Stat extends Application {
	private final TableView<DataRow> table       = new TableView<>();
	private final TableView<DataRow> freeTable   = new TableView<>();
	private final TableView<VisitRow> visitsTable = new TableView<>();

	LineChart<String, Number> commercialChart = null;
	LineChart<String, Number> freeChart = null;
	private LineChart<String, Number> visitsChart = null;

	public StatDB db = new StatDB();

	private final ObservableList<DataRow> data        = FXCollections.observableArrayList();
	private final ObservableList<DataRow> freeData    = FXCollections.observableArrayList(); // To store unfiltered dataset
	private final ObservableList<DataRow> _data       = FXCollections.observableArrayList();
	private final ObservableList<DataRow> _freeData   = FXCollections.observableArrayList(); // To store unfiltered dataset
	private final ObservableList<VisitRow> _visitsData = FXCollections.observableArrayList();
	private final ObservableList<VisitRow> visitsData  = FXCollections.observableArrayList();

	private VBox leftPane = null;

	private final String DEFAULT_PERIOD = "Month";
	private final String DEFAULT_PROFIT = "300";
	private final String VISIT_URL = "https://oc.advertikon.com.ua/pixel.php?get_statistics=";

	private final QueryData queryData = new QueryData( DEFAULT_PERIOD, DEFAULT_PROFIT );

	private int controlsIsDisabled = 0;
	
	HashMap<String, String> mModulesList = new HashMap();

	public static void main( String[] args ) {
            launch( args );
	}

	@Override
	public void init() {
		mModulesList.put("Import", "34983" );
		mModulesList.put("Email", "27789" );
		mModulesList.put("FreeImport", "35222" );
		mModulesList.put("Mail", "34131" );
		mModulesList.put("Stripe", "26891" );
		mModulesList.put( "GDPR", "35243" );
            // Log.debug( "Initializing" );
	}

	/**
	 *
	 * @param primaryStage
	 */
	@Override
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

	@Override
	public void stop() {
		Log.debug( "Stop" );
	}

	/**
	 * Initializes commercial statistics table
	 */
	@SuppressWarnings("unchecked")
	protected void iniTable() {
            TableColumn<DataRow, String> chartColumn        = new TableColumn<>( "Chart" );
            TableColumn<DataRow, Integer> id                = new TableColumn<>( "ID" );
            TableColumn<DataRow, String> nameColumn         = new TableColumn<>( "Name" );
            TableColumn<DataRow, Integer> salesColumn       = new TableColumn<>( "Total sales" );
            TableColumn<DataRow, Integer> totalSalesColumn  = new TableColumn<>( "Sales" );
            TableColumn<DataRow, Double> priceColumn        = new TableColumn<>( "Price" );
            TableColumn<DataRow, String> dateAddedColumn    = new TableColumn<>( "Date Added" );
            TableColumn<DataRow, String> dateModifiedColumn = new TableColumn<>( "Date Modified" );
            TableColumn<DataRow, Double> profits            = new TableColumn<>( "Profits" );
            TableColumn<DataRow, Double> monthProfits       = new TableColumn<>( "Month profits" );

            table.getColumns().addAll(
                    chartColumn,
                    id,
                    nameColumn,
                    salesColumn,
                    totalSalesColumn,
                    priceColumn,
                    profits,
                    monthProfits,
                    dateAddedColumn,
                    dateModifiedColumn
            );

            chartColumn.setCellValueFactory(        new PropertyValueFactory<>( "chart" ) );
            nameColumn.setCellValueFactory(         new PropertyValueFactory<>( "name" ) );
            salesColumn.setCellValueFactory(        new PropertyValueFactory<>( "sales" ) );
            totalSalesColumn.setCellValueFactory(   new PropertyValueFactory<>( "totalSales" ) );
            priceColumn.setCellValueFactory(        new PropertyValueFactory<>( "price" ) );
            dateAddedColumn.setCellValueFactory(    new PropertyValueFactory<>( "dateAdded" ) );
            dateModifiedColumn.setCellValueFactory( new PropertyValueFactory<>( "dateModified" ) );
            profits.setCellValueFactory(            new PropertyValueFactory<>( "profits" ) );
            monthProfits.setCellValueFactory(       new PropertyValueFactory<>( "monthProfits" ) );
            id.setCellValueFactory(                 new PropertyValueFactory<>( "id" ) );

            table.setRowFactory( ( TableView<DataRow> t ) -> {
                    CustomTableRow row = new CustomTableRow( "commercial", Stat.this );
                    return row;
            } );
	}

	/**
	 * Initializes free statistics table
	 */
	@SuppressWarnings("unchecked")
	protected void iniFreeTable() {
            TableColumn<DataRow, String> chartColumn        = new TableColumn<>( "Chart" );
            TableColumn<DataRow, Integer> id                = new TableColumn<>( "ID" );
            TableColumn<DataRow, String> nameColumn         = new TableColumn<>( "Name" );
            TableColumn<DataRow, Integer> salesColumn       = new TableColumn<>( "Total downloads" );
            TableColumn<DataRow, Integer> totalSalesColumn  = new TableColumn<>( "Downloads" );
            TableColumn<DataRow, String> dateAddedColumn    = new TableColumn<>( "Date Added" );
            TableColumn<DataRow, String> dateModifiedColumn = new TableColumn<>( "Date Modified" );

            freeTable.getColumns().addAll(
                    chartColumn,
                    id,
                    nameColumn,
                    salesColumn,
                    totalSalesColumn,
                    dateAddedColumn,
                    dateModifiedColumn
            );

            chartColumn.setCellValueFactory(        new PropertyValueFactory<>( "chart" ) );
            id.setCellValueFactory(                 new PropertyValueFactory<>( "id" ) );
            nameColumn.setCellValueFactory(         new PropertyValueFactory<>( "name" ) );
            salesColumn.setCellValueFactory(        new PropertyValueFactory<>( "sales" ) );
            totalSalesColumn.setCellValueFactory(   new PropertyValueFactory<>( "totalSales" ) );
            dateAddedColumn.setCellValueFactory(    new PropertyValueFactory<>( "dateAdded" ) );
            dateModifiedColumn.setCellValueFactory( new PropertyValueFactory<>( "dateModified" ) );

            freeTable.setRowFactory( ( TableView<DataRow> t ) -> {
                    CustomTableRow row = new CustomTableRow( "free", this );

                    return row;
            } );
	}

	/**
	 * Initializes visits table
	 */
	@SuppressWarnings("unchecked")
	protected void iniVisitsTable() {
            TableColumn<VisitRow, String> date       = new TableColumn<>( "Date" );
            TableColumn<VisitRow, Integer> id        = new TableColumn<>( "ID" );
            TableColumn<VisitRow, String> country    = new TableColumn<>( "Country" );
            TableColumn<VisitRow, String> ip         = new TableColumn<>( "IP" );
            TableColumn<VisitRow, String> search     = new TableColumn<>( "Search" );
            TableColumn<VisitRow, String> referrer   = new TableColumn<>( "Referrer" );
            TableColumn<VisitRow, Integer> page      = new TableColumn<>( "Page" );
            TableColumn<VisitRow, String> category   = new TableColumn<>( "Category" );
            TableColumn<VisitRow, String> version    = new TableColumn<>( "Version" );
            TableColumn<VisitRow, String> license    = new TableColumn<>( "License" );
            TableColumn<VisitRow, String> rating     = new TableColumn<>( "Rating" );
            TableColumn<VisitRow, String> vendor     = new TableColumn<>( "Vendor" );
            TableColumn<VisitRow, String> sort       = new TableColumn<>( "Sort" );

            visitsTable.getColumns().addAll(
                    date,
                    id,
                    country,
                    ip,
                    search,
                    referrer,
                    page,
                    category,
                    version,
                    license,
                    rating,
                    vendor,
                    sort
            );

            date.setCellValueFactory(     new PropertyValueFactory<>( "date" ) );
            id.setCellValueFactory(       new PropertyValueFactory<>( "id" ) );
            country.setCellValueFactory(  new PropertyValueFactory<>( "country" ) );
            ip.setCellValueFactory(       new PropertyValueFactory<>( "ip" ) );
            search.setCellValueFactory(   new PropertyValueFactory<>( "search" ) );
            referrer.setCellValueFactory( new PropertyValueFactory<>( "referrer" ) );
            page.setCellValueFactory(     new PropertyValueFactory<>( "page" ) );
            category.setCellValueFactory( new PropertyValueFactory<>( "category" ) );
            version.setCellValueFactory(  new PropertyValueFactory<>( "version" ) );
            license.setCellValueFactory(  new PropertyValueFactory<>( "license" ) );
            rating.setCellValueFactory(   new PropertyValueFactory<>( "rating" ) );
            vendor.setCellValueFactory(   new PropertyValueFactory<>( "vendor" ) );
            sort.setCellValueFactory(     new PropertyValueFactory<>( "sort" ) );
	}

	/**
	 * Populates the commercial statistics table's dataset
	 */
	protected void setTableData() {
            disableControls( true );
            data.clear();
            _data.clear();

            db.getStatisticData( queryData ).forEach( ( row ) -> {
                    data.add( new DataRow( row ) );
            } );
            
            Log.error( "Set data for commmercial table" );

            _data.addAll( data );
            table.setItems( data );
            disableControls( false );
	}

	/**
	 * Populates the free statistics table dataset
	 */
	protected void setFreeTableData() {
            disableControls( true );
            freeData.clear();
            _freeData.clear();

            db.getFreeStatisticData( queryData ).forEach( ( row ) -> {
                    freeData.add( new DataRow( row ) );
            } );

            _freeData.addAll( freeData );
            freeTable.setItems( freeData );
            disableControls( false );
	}

	/**
	 * Populates the visits statistics table dataset
	 */
	protected void setVisitsTableData() {
		disableControls( true );
		visitsData.clear();
		_visitsData.clear();

		db.getVisitsStatisticData( queryData ).forEach( ( row ) -> {
			visitsData.add( new VisitRow( row ) );
		} );

		_visitsData.addAll( visitsData );
		visitsTable.setItems( visitsData );
		disableControls( false );

		Platform.runLater( () -> {
			addVisitsToChart();
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

		dateFrom.setOnAction( ( ActionEvent e ) -> {
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
		dateTo.setOnAction( ( ActionEvent e ) -> {
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

		filter.setOnAction( ( ActionEvent e ) -> {
			String search = filter.getText();

			data.clear();
			freeData.clear();
			
			if ( search.equals( "" ) ) {
				Log.debug( "Clear filter" );

				data.addAll( _data );
				freeData.addAll( _freeData );

			} else {
				Log.debug( String.format( "Filtering by '%s'", search ) );

				_data.stream().filter( ( row ) -> ( row.getName().contains( search ) ) ).map( ( row ) -> {
					Log.debug( String.format( "Commercial filter: %s", row.getName() ) );
					return row;
				} ).forEachOrdered( ( row ) -> {
					data.add( row );
				} );

				_freeData.stream().filter( ( row ) -> ( row.getName().contains( search ) ) ).forEachOrdered( ( row ) -> {
					freeData.add( row );
				} );
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

				_data.stream().filter( ( row ) -> ( !row.getChart().equals( "" ) ) ).forEachOrdered( ( row ) -> {
					data.add( row );
				} );

				_freeData.stream().filter( ( row ) -> ( !row.getChart().equals( "" ) ) ).forEachOrdered( ( row ) -> {
					freeData.add( row );
				} );

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
		commercialChart = new LineChart<>( xAxis, yAxis );

		return commercialChart;
	}

	/**
	 * Initializes free statistics chart
	 * @return {Chart} Chart object
	 */
	protected Chart initFreeChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis   = new NumberAxis();
		freeChart = new LineChart<>( xAxis, yAxis );

		return freeChart;
	}

	/**
	 * Initializes visits statistics chart
	 * @return {Chart} Chart object
	 */
	protected Chart initVisitsChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis   = new NumberAxis();
		visitsChart = new LineChart<>( xAxis, yAxis );
		visitsChart.setCreateSymbols( false );

		return visitsChart;
	}

	/**
	 * Adds visits to chart
	 */
	public void addVisitsToChart() {
		visitsChart.getData().clear();

		mModulesList.forEach( ( name, id ) -> {
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName( name );

			db.getVisits( id, queryData ).forEach((row) -> {
				series.getData().add(
					new XYChart.Data<>(
						row.get( TimeLine.DATE_FIELD ),
						Integer.parseInt( row.getOrDefault( TimeLine.TARGET_FIELD, "0" ) )
					)
				);
			});

			visitsChart.getData().add( series );
		} );
	}


	/**
	 * Adds module to chart
	 * @param id
	 * @param name
	 * @param chart
	 */
	public void addSeriesToChart( String id, String name, LineChart<String, Number> chart ) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName( name + "(" + id + ")" );

		db.getModule( id, queryData ).forEach( ( row ) -> {
			series.getData().add(
				new XYChart.Data<>( row.get( TimeLine.DATE_FIELD ), Double.parseDouble( row.getOrDefault( TimeLine.TARGET_FIELD, "0" ) ) )
			);
		} );

		chart.getData().add( series );
	}

	/**
	 * Removes module from chart
	 * @param id
	 * @param chart
	 */
	public void removeSeriesFromChart( String id, LineChart<String, Number> chart ) {
		chart.getData().stream().filter( ( series ) -> ( series.getName().equals( id ) ) ).forEachOrdered( ( series ) -> {
			Platform.runLater(() -> {
				chart.getData().remove( series );
			});
		} );
	}

	/**
	 * Adds all from statistic table which is already in chart
	 * @param chart
	 * @param chartName
	 */
	protected void addAllSeriesToChart( LineChart<String, Number> chart, String chartName ) {
		TableView<DataRow> t = "free".equals( chartName ) ? freeTable : table;
		chart.getData().clear();

		t.getItems().stream().filter( ( row ) -> ( !row.getChart().equals( "" ) ) ).forEachOrdered( ( row ) -> {
			addSeriesToChart( String.valueOf( row.getId() ), row.getName(), chart );
		} );
	}

	/**
	 * Enables/disables left control panel regarding multiple calls
	 * @param status
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
		Platform.runLater(() -> {
			addAllSeriesToChart( commercialChart, "commercial" );
			addAllSeriesToChart( freeChart, "free" );
		});
	}

	protected void sysncVisits() {
		new Thread( () -> {
			URL url = null;
			HttpURLConnection connection = null;
			String line = "";
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
				
			} catch ( MalformedURLException | UnsupportedEncodingException e ) {
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

					if( db.saveVisit( lineData ) ) {
						count++;
					}
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

