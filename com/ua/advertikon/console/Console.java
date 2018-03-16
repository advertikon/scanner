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

	private TextField connectInput = null;

	private String connectedHost = null;

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
		tab.setContent( connectionLogger.instance() );
		
		return tab;
	}

	protected Tab initConfigurationTab() {
		Tab tab = new Tab( "Configuration" );
		configurationLogger = new Logger();

		tab.setClosable( false );
		tab.setContent( configurationLogger.instance() );
		
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

		pane.getStyleClass().add( "left-pane" );

		// Connect
		connectInput = new TextField( "" );

		connectInput.setOnAction( ( e ) -> {
			if ( !connectInput.getText().equals( "" ) ) {
				connect( connectInput.getText() );
			}
		} );

		pane.getChildren().addAll( new Label( "Connect: " ), connectInput );

		return pane;
	}

	protected void connect( String site ) {
		new Thread( () -> {
			connectedHost = null;
			connectInput.setDisable( true );

			List<String> list = getConnectionUrl( site );
			URL u = null;
			HttpURLConnection connection = null;
			String line = "";
			BufferedReader reader = null;

			for ( String url: list ) {
				connectionLogger.println( "Connecting to " + url );

				try {
					u = new URL( url );
					connection = (HttpURLConnection)u.openConnection();
					connection.setFollowRedirects( true );
					connection.setRequestMethod( "POST" );
	
					AUrl.setCookie( connection );
					// AUrl.dumpRequestHeaders( connection );
					AUrl.setPost( connection, "p=letmein&info=1" );


					reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
					connectionLogger.println( "Response message: " + connection.getResponseMessage() );

					while ( null != ( line = reader.readLine() ) ) {
						connectionLogger.println( line );
					}

					try {
						AUrl.saveCookie( connection );
						
					} catch ( AException e ) {
						Log.error( e );
					}

					// AUrl.dumpHeaders( connection );

					reader.close();
					connectedHost = u.getProtocol() + "://" + u.getHost() + "/?" + u.getQuery();

				} catch ( MalformedURLException e ) {
					connectionLogger.error( e );

				} catch ( FileNotFoundException e ) {

				} catch ( IOException e ) {
					connectionLogger.error( e );

				} finally {
					
				}

				if ( null != connectedHost ) {
					break;
				}
			}

			if ( null == connectedHost ) {
				connectionLogger.println( "Console API is not found" );

			} else {
				getSettings();
			}

			connectInput.setDisable( false );
			
		} ).start();
	}

	protected List getConnectionUrl( String site ) {
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
			URL url = null;
			HttpURLConnection connection = null;

			configurationLogger.clear();

			connectInput.setDisable( true );

			try {
				url = new URL( connectedHost );
				configurationLogger.println( "Connecting to " + url );
				connection = (HttpURLConnection)url.openConnection();
				AUrl.setCookie( connection );
				// AUrl.dumpRequestHeaders( connection );
				connection.setRequestMethod( "POST" );
				AUrl.setPost( connection, "config=1" );
				configurationLogger.println( AUrl.read( connection ) );

			} catch ( MalformedURLException e ) {
				configurationLogger.error( e );

			} catch ( IOException e ) {
				configurationLogger.error( e );

			} finally {
				connectInput.setDisable( false );
			}
			
		} ).start();
	}
}