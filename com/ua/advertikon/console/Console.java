package com.ua.advertikon.console;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import javafx.scene.web.*;
import javafx.scene.paint.*;
import javafx.beans.value.*;

import java.time.*;
import java.util.*;
import java.time.format.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import com.ua.advertikon.helper.*;

public class Console extends Application {
	private TableView<InstallationRow> installationTable = new TableView<>();

	private ObservableList<InstallationRow> installationData = FXCollections.observableArrayList();

	private ConsoleDb db = new ConsoleDb();
	private ConsoleModel model = new ConsoleModel();

	private Logger connectionLogger = null;
	private Logger configurationLogger = null;
	private Logger tailLogger = null;

	private TextField connectInput = null;

	private String connectedHost = null;

	private Browser configurationPage = null;

	private Button logButton = null;

	private Logger sqlLogger = null;
	private TextArea sqlInput = null;
	private Button sqlButton = null;

	private Logger evalLogger = null;
	private TextArea evalInput = null;
	private Button evalButton = null;

	static public void main( String[] args ) {
		launch( args );
	}

	public void start( Stage stage ) {

		// Scene
		stage.setTitle( "Console" );
		Group root = new Group();
		Scene scene = new Scene( root, 1000, 700 );
		scene.getStylesheets().add( "css/style.css" );
		stage.setScene( scene );

		// Main layout
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft( getLeftPane() ); // Controls

		// Main content
		TabPane tabPane = new TabPane();
		
		// Active installations
		tabPane.getTabs().add( initIntallationTab() );
		iniInstallationTable();
		setInstallationTableData();

		// Connection log tab
		tabPane.getTabs().add( initConnectionTab() );

		// Configuration tab
		tabPane.getTabs().add( initConfigurationTab() );

		// Tail tab
		tabPane.getTabs().add( initTailTab() );

		// SQL tab
		tabPane.getTabs().add( initSQLTab() );

		// Eval tab
		tabPane.getTabs().add( initEvalTab() );

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

	protected Tab initConnectionTab() {
		Tab tab = new Tab( "Connection" );
		connectionLogger = new Logger();
		tab.setClosable( false );
		tab.setContent( connectionLogger );
		
		return tab;
	}

	protected Tab initConfigurationTab() {
		Tab tab = new Tab( "Configuration" );
		configurationPage = new Browser();
		tab.setClosable( false );
		tab.setContent( configurationPage );
		
		return tab;
	}

	protected Tab initTailTab() {
		Tab tab = new Tab( "Tail" );
		tailLogger = new Logger();
		tab.setClosable( false );
		tab.setContent( tailLogger );
		
		return tab;
	}

	protected Tab initSQLTab() {
		Tab tab = new Tab( "SQL" );
		sqlLogger = new Logger();
		sqlInput = new TextArea();
		tab.setClosable( false );
		sqlInput.setMinHeight( 200 );
		VBox b = new VBox();
		b.setBackground( new Background( new BackgroundFill( Paint.valueOf( "#000000" ) , null, null ) ) );
		b.getChildren().addAll( sqlInput, sqlLogger );
		tab.setContent( b );
		
		return tab;
	}

	protected Tab initEvalTab() {
		Tab tab = new Tab( "Eval" );
		evalLogger = new Logger();
		evalInput = new TextArea();
		tab.setClosable( false );
		evalInput.setMinHeight( 200 );
		VBox b = new VBox();
		b.setBackground( new Background( new BackgroundFill( Paint.valueOf( "#000000" ) , null, null ) ) );
		b.getChildren().addAll( evalInput, evalLogger );
		tab.setContent( b );
		
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

		dateModified.setCellFactory( ( TableColumn<InstallationRow, String> column ) -> {
			return new DateModifiedCell();
		} );

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

	protected Pane getLeftPane() {
		VBox pane = new VBox();
		pane.setSpacing( 10 );

		pane.getStyleClass().add( "left-pane" );

		// Connect
		connectInput = new TextField( "" );

		connectInput.setOnAction( ( e ) -> {
			if ( !connectInput.getText().equals( "" ) ) {
				connect( connectInput.getText() );
			}
		} );

		pane.getChildren().addAll( new Label( "Connect: " ), connectInput );

		// Log
		logButton = new Button( "Get log" );

		logButton.setOnAction( ( e ) -> {
			getLog();
		} );

		logButton.setDisable( true );
		logButton.setMaxWidth( Double.MAX_VALUE );

		pane.getChildren().addAll( new Label( "Log: " ), logButton );

		// SQL
		sqlButton = new Button( "Run SQL" );

		sqlButton.setOnAction( ( e ) -> {
			runSQL();
		} );

		sqlButton.setDisable( true );
		sqlButton.setMaxWidth( Double.MAX_VALUE );

		pane.getChildren().addAll( new Label( "Run SQL query: " ), sqlButton );

		// SQL
		evalButton = new Button( "Eval" );

		evalButton.setOnAction( ( e ) -> {
			runEval();
		} );

		evalButton.setDisable( true );
		evalButton.setMaxWidth( Double.MAX_VALUE );

		pane.getChildren().addAll( new Label( "Run Expression: " ), evalButton );

		return pane;
	}

	protected void connect( String site ) {
		new Thread( () -> {
			connectedHost = null;
			connectInput.setDisable( true );
			logButton.setDisable( true );
			sqlButton.setDisable( true );
			evalButton.setDisable( true );

			List<String> list = getConnectionUrl( site );
			String line = "";

			connectionLogger.println( "Establishing connection to " + site );

			for ( String url: list ) {
				connectionLogger.println( "Trying " + url );

				Connection connection = new Connection( url, "POST", "p=letmein&info=1" );

				if ( null != connection.reader ) {
					while ( null != ( line = connection.readLine() ) ) {
						connectionLogger.println( line );
					}

					connectedHost = connection.url.getProtocol() + "://" + connection.url.getHost() + connection.url.getPath() + "?" + connection.url.getQuery();
					connectionLogger.println( "Connection established" );
					connection.disconnect();
					break;

				} else {
					connectionLogger.println( "Connection failed" );
				}

				connection.disconnect();
			}

			if ( null == connectedHost ) {
				connectionLogger.println( "Failed to connect to Log API" );

			} else {
				logButton.setDisable( false );
				sqlButton.setDisable( false );
				evalButton.setDisable( false );
				getSettings();
			}

			connectInput.setDisable( false );
			
		} ).start();
	}

	protected List<String> getConnectionUrl( String site ) {
		List<String> ret = new ArrayList<>();
		URL url = null;

		try {
			url = new URL( site );
			
		} catch ( MalformedURLException e ) {
			connectionLogger.error( e );

			return ret;
		}

		String host = url.getProtocol() + "://" + url.getHost();

		ret.add( host + "/adk_log" );

		if ( url.getQuery() == null ) {
			ret.add( host + "/index.php?route=extension/payment/advertikon_stripe/log" );
			ret.add( host + "/index.php?route=payment/advertikon_stripe/log" );
			ret.add( host + "/index.php?route=extension/module/adk_mail/log" );
			ret.add( host + "/index.php?route=module/adk_mail/log" );

		} else {
			ret.add( site );
		}

		return ret;
	}

	protected void getSettings() {
		new Thread( () -> {
			connectionLogger.println( "Fetching PHP configurations" );

			connectInput.setDisable( true );
			configurationPage.clear();
			Connection connection = new Connection( connectedHost, "POST", "config=1" );
			
			if ( connection.reader != null ) {
				connectionLogger.println( "PHP configurations have been fetched" );
				configurationPage.loadContent( connection.readAll() );

			} else {
				connectionLogger.println( "Failed to fetch PHP configurations" );
			}

			connection.disconnect();

			connectInput.setDisable( false );
			
		} ).start();
	}

	protected void getLog() {
		new Thread( () -> {
			Connection connection = new Connection( connectedHost, "POST", "full=info" );

			if ( connection.canRead() ) {
				splitLogToLines( connection.readAll() );
			}

			connection.disconnect();

		} ).start();
	}

	protected void splitLogToLines( String data ) { Log.debug( data );
		if ( null == data ) return;

		String[] lines = data.split( "<#>" );

		tailLogger.clear();

		for ( int i = 0; i < lines.length; i++ ) {
			if ( lines[ i ].length() == 0 ) continue;

			tailLogger.print( Pattern.compile( "(In file:.*?)$",  Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE | Pattern.UNIX_LINES ).matcher( lines[ i ] ).replaceAll( "\u001b[90m$1\u001b[0m" ) );
		}
	}

	protected void runSQL() {
		new Thread( () -> {
			sqlButton.setDisable( true );
			String sql = sqlInput.getText();

			if ( sql.length() != 0 ) {
				Connection c = new Connection( connectedHost, "POST", "q=" + sql );

				String out = c.readAll();

				Platform.runLater( ()-> {
					sqlLogger.println( out );
					sqlButton.setDisable( false );
				} );
			}
		} ).start();
	}

	protected void runEval() {
		new Thread( () -> {
			evalButton.setDisable( true );
			String eval = evalInput.getText();

			if ( eval.length() != 0 ) {
				Connection c = new Connection( connectedHost, "POST", "e=" + eval );

				String out = c.readAll();

				Platform.runLater( ()-> {
					evalLogger.println( out );
					evalButton.setDisable( false );
				} );
			}
		} ).start();
	}
}