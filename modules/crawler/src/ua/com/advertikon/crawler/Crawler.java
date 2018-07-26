/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.crawler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author max
 */
public class Crawler extends Application {
	protected Button mButtonSave;
	protected Button mButtonReload;
	protected Button mButtonDelete;
	protected ComboBox<String> mPackageList;
	protected TextField mCodeName;
	protected Spinner mVersionMajor;
	protected Spinner mVersionMinor;
	protected Spinner mVersionPatch;
	protected TextArea mIncludeFile   = new TextArea();
	protected TextArea mExcludeFile   = new TextArea();
	protected TextArea mIncludeFolder = new TextArea();
	protected TextArea mExcludeFolder = new TextArea();
	protected TextArea mIncludeRegex  = new TextArea();
	protected TextArea mExcludeRegex  = new TextArea();
	
	protected Alert mAlert = new Alert( Alert.AlertType.WARNING );
	
	protected final Double SPACING          = 5.0;
	protected final Double LABEL_WIDTH      = 100.0;
	protected final String CODE_TEXT        = "code";
	protected final String VERSION_TEXT     = "version";
	protected final String INCL_FILE_TEXT   = "include_file";
	protected final String EXCL_FILE_TEXT   = "exclude_file";
	protected final String INCL_FOLDER_TEXT = "inclide_folder";
	protected final String EXCL_FOLDER_TEXT = "exclude_folder";
	protected final String INCL_REGEX_TEXT  = "include_regex";
	protected final String EXCL_REGEX_TEXT  = "exclude_regex";
	
	
	@Override
	public void start( Stage stage ) {
		stage.setTitle( "Crawler" );
		Group root = new Group();
		Scene scene = new Scene( root, 1000, 700 );
		stage.setScene( scene );

		// Main layout
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding( new Insets( SPACING ) );
		borderPane.setLeft( getLeftPane() ); // Controls
		borderPane.setRight( getRightPane() );

		root.getChildren().add( borderPane );
		stage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	protected Pane getLeftPane() {
		final Double WIDTH = 340.0;
		
		final Double button_width = ( WIDTH - SPACING ) / 3;
		final Double SPINNER_WIDTH = ( WIDTH - LABEL_WIDTH ) / 3;
		
		// Main container
		VBox pane = new VBox();
		pane.setSpacing( SPACING );
		pane.setPrefWidth( WIDTH );
	
		// Buttons set
		HBox buttonSet = new HBox();
		mButtonSave    = new Button( "Save" );
		mButtonReload  = new Button( "Reload" );
		mButtonDelete  = new Button( "Delete" );
		
		mButtonReload.setOnAction( ( ActionEvent value ) -> { readPackage( value ); } );
		mButtonSave.setOnAction( ( ActionEvent value ) -> { savePackage( value ); } );
		
		buttonSet.setSpacing( SPACING );
		
		mButtonSave.setPrefWidth( button_width );
		mButtonReload.setPrefWidth( button_width );
		mButtonDelete.setPrefWidth( button_width );

		buttonSet.getChildren().addAll( mButtonSave, mButtonReload, mButtonDelete );
			
		mPackageList = new ComboBox<>();
		mPackageList.setPrefWidth( WIDTH );
		
		//Package code
		HBox codeSet = new HBox();
		mCodeName = new TextField( "" );
		Label codeLabel = new Label( "Code name" );
		
		codeSet.setSpacing( SPACING );
		codeLabel.setPrefWidth( LABEL_WIDTH );
		mCodeName.setPrefWidth( WIDTH - LABEL_WIDTH );
		codeSet.getChildren().addAll( codeLabel, mCodeName );
		
		// Version
		HBox versionSet = new HBox();
		mVersionMajor   = new Spinner( 0, 1000, 0 );
		mVersionMinor   = new Spinner( 0, 100, 0 );
		mVersionPatch   = new Spinner( 0, 100, 0 );
		Label versionLabel = new Label( "Version" );
		
		versionSet.setSpacing( SPACING );
		versionLabel.setPrefWidth( LABEL_WIDTH );
		mVersionMajor.setMaxWidth( SPINNER_WIDTH );
		mVersionMinor.setMaxWidth( SPINNER_WIDTH );
		mVersionPatch.setMaxWidth( SPINNER_WIDTH );
		versionSet.getChildren().addAll( versionLabel, mVersionMajor, mVersionMinor, mVersionPatch );
		
		pane.getChildren().addAll( buttonSet, mPackageList, codeSet, versionSet );

		return pane;
	}
	
	protected Pane getRightPane() {
		// Main container
		VBox pane = new VBox();
		pane.setSpacing( SPACING );
		pane.setPadding( new Insets( 0, 0, 0, 15 ) );
		
		pane.getChildren().addAll(
			makeConstrainLine( "Include File",   mIncludeFile ),
			makeConstrainLine( "Exclude File",   mExcludeFile ),
			makeConstrainLine( "Include Folder", mIncludeFolder ),
			makeConstrainLine( "Exclude Folder", mExcludeFolder ),
			makeConstrainLine( "Include RegEx",  mIncludeRegex ),
			makeConstrainLine( "Exclude RegEx",  mExcludeRegex )
		);

		return pane;
	}
	
	protected Node makeConstrainLine( String text, TextArea item ) {
		HBox set = new HBox();
		Label l = new Label( text );
		l.setPrefWidth( LABEL_WIDTH );
		item.setPrefWidth( 500 );
		item.setPrefHeight( 100 );
		set.getChildren().addAll( l, item );
		
		return set;
	}
	
	protected void readPackage( ActionEvent event ) {
		File current = new File( "." );
		File[] files = current.listFiles();
		
		for( File f: files ) {
			System.out.println( f.toString() );
		}
	}
	
	protected void savePackage( ActionEvent event ) {
		StringBuilder out = new StringBuilder();
		
		String codeName   = mCodeName.getText();
		String version;
		String inclFile   = mIncludeFile.getText();
		String exclFile   = mExcludeFile.getText();
		String inclFolder = mIncludeFolder.getText();
		String exclFolder = mExcludeFolder.getText();
		String inclRegex  = mIncludeRegex.getText();
		String exclRegex  = mExcludeRegex.getText();
		
		try {
			version = getConfigVersion();

		} catch (CrawlerException ex) {
			alert( ex.toString() );
			return;
		}
		
		if ( codeName.isEmpty() ) {
			alert( "Code name may not be empty" );
			return;
		}
		
		out.append( CODE_TEXT + ":" ).append( codeName ).append( "\n" );
		out.append( VERSION_TEXT + ":" ).append( version ).append( "\n" );
		out.append( INCL_FILE_TEXT + ":" ).append( textAreaToList( inclFile ) ).append( "\n" );
		out.append( EXCL_FILE_TEXT + ":" ).append( textAreaToList( exclFile ) ).append( "\n" );
		out.append( INCL_FOLDER_TEXT + ":" ).append( textAreaToList( inclFolder) ).append( "\n" );
		out.append( EXCL_FOLDER_TEXT + ":" ).append( textAreaToList( exclFolder ) ).append( "\n" );
		out.append( INCL_REGEX_TEXT + ":" ).append( textAreaToList( inclRegex ) ).append( "\n" );
		out.append( EXCL_REGEX_TEXT + ":" ).append( textAreaToList( exclRegex ) ).append( "\n" );
		
		System.out.println( out.toString() );
		
		
	}
	
	protected String getConfigVersion() throws CrawlerException {
		StringBuilder out = new StringBuilder();

		String major = mVersionMajor.getValue().toString();
		String minor = mVersionMinor.getValue().toString();
		String patch = mVersionPatch.getValue().toString();
		
		if ( major.equals( "" ) ) {
			throw new CrawlerException( "Empty mjor version number" );
		}

		if ( minor.equals( "" ) ) {
			throw new CrawlerException( "Empty minor version number" );
		}

		if ( patch.equals( "" ) ) {
			throw new CrawlerException( "Empty patch version number" );
		}
		
		out.append( major).append( "." ).append( minor ).append( "." ).append( patch );
		
		return out.toString();
	}
	
	protected void alert( String text ) {
		mAlert.setHeaderText( text );
		mAlert.show();
	}
	
	protected String textAreaToList( String in ) {
		if ( in.isEmpty() ) {
			return "";
		}

		String out = in.replace( '\n', ',' );
		int pos = out.length() - 1;
		
		// Trim to the right
		while ( out.charAt( pos ) == ',' ) {
			pos--;
		}
		
		return out.substring( 0, pos + 1 );
	}
	
	
	
}

class CrawlerException extends Exception{
	public CrawlerException( String message ) {
		super( message );
	}
}
