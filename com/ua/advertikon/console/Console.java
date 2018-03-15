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

	static public void main( String[] args ) {
		launch( args );
	}

	public void start( Stage stage ) {
// Log.debug( LocalDateTime.now().format( DateTimeFormatter.ofPattern( "E',' d'-'LLL'-'y H':'m':'s 'GMT'" ) ) );System.exit(0);
		// Scene
		stage.setTitle( "Statistics" );
		Group root = new Group();
		Scene scene = new Scene( root, 1000, 700 );
		scene.getStylesheets().add( "css/style.css" );
		stage.setScene( scene );

		// Main layout
		BorderPane borderPane = new BorderPane();
		borderPane.setLeft( getLeftPane() ); // Controls

		// Main content
		TabPane tabPane = new TabPane();
		
		// Commercial statistics table
		tabPane.getTabs().add( initIntallationTab() );
		iniInstallationTable();
		setInstallationTableData();

		// Connection log tab
		tabPane.getTabs().add( initConnectionTab() );

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

	protected Tab initConnectionTab() {
		Tab tab = new Tab( "Connection" );
		connectionLogger = new Logger();

		tab.setClosable( false );
		tab.setContent( connectionLogger.instance() );
		
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
		TextField connect = new TextField( "" );

		connect.setOnAction( ( e ) -> {
			if ( !connect.getText().equals( "" ) ) {
				connect( connect.getText() );
			}
		} );

		pane.getChildren().addAll( new Label( "Connect: " ), connect );

		return pane;
	}

	protected void connect( String site ) {
		new Thread( () -> {
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
					connection.setRequestProperty( "p", "letmein" );

					String data = "p=letmein";

					connection.setDoOutput( true );
					DataOutputStream wr = new DataOutputStream( connection.getOutputStream() );
					wr.writeBytes( data );
					wr.flush();
					wr.close();

					reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
					connectionLogger.println( "Response message: " + connection.getResponseMessage() );

					while ( null != ( line = reader.readLine() ) ) {
						connectionLogger.println( line );
					}

					System.out.println( new AUrl().getCookie( connection ) );
					String cookie = connection.getHeaderField( "Set-Cookie" );

					Log.debug( cookie );
					// Log.dump( cookie.split( "," ) );

					reader.close();

					return;

				} catch ( MalformedURLException e ) {
					Log.error( e );

				} catch ( FileNotFoundException e ) {

				} catch ( IOException e ) {
					Log.error( e );
				}
			}

			connectionLogger.println( "Console API is not found" );
			
		} ).start();
	}

	protected List getConnectionUrl( String site ) {
		List<String> ret = new ArrayList<>();
		URL url = null;

		try {
			url = new URL( site );
			
		} catch ( MalformedURLException e ) {
			Log.error( e );

			return ret;
		}

		String host = url.getProtocol() + "://" + url.getHost();

		ret.add( host + "/log" );

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
}