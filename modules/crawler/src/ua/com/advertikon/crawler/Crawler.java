/*
 * Main class
 */
package ua.com.advertikon.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author max
 */
public class Crawler extends Application {
	protected Button mButtonSave;
	protected Button mButtonReload;
	protected Button mButtonDelete;
	protected Button mButtonCollect;
	protected Button mButtonProcess;
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
	protected Label mStatusBar;
	protected Alert mAlert = new Alert( Alert.AlertType.WARNING );
	
	protected final Double SPACING             = 5.0;
	protected final Double LABEL_WIDTH         = 100.0;
	protected final Double BUTTON_WIDTH        = 100.0;
	protected final Double STATUS_BAR_HEIGHT   = 20.0;
	protected final Double WINDOW_WIDTH        = 1000.0;
	protected final Double WINDOW_HEIGHT       = 700.0;
	protected final String CODE_TEXT           = "code";
	protected final String VERSION_TEXT        = "version";
	protected final String INCL_FILE_TEXT      = "include_file";
	protected final String EXCL_FILE_TEXT      = "exclude_file";
	protected final String INCL_FOLDER_TEXT    = "inclide_folder";
	protected final String EXCL_FOLDER_TEXT    = "exclude_folder";
	protected final String INCL_REGEX_TEXT     = "include_regex";
	protected final String EXCL_REGEX_TEXT     = "exclude_regex";
    protected final String PACKAGE_NAME_PREFIX = ".";
    protected final String PACKAGE_NAME_SUFFIX = ".package";
	
	protected ArrayList<Path> mFiles;
    
	@Override
	public void start( Stage stage ) {
		stage.setTitle( "Crawler" );
		Group root = new Group();
		Scene scene = new Scene( root, WINDOW_WIDTH, WINDOW_HEIGHT );
		stage.setScene( scene );

		// Main layout
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding( new Insets( SPACING ) );
		borderPane.setLeft( getLeftPane() ); // Controls
		borderPane.setRight( getRightPane() );
		borderPane.setBottom( getStatusBar() );
		
		root.getChildren().add( borderPane );
		stage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Creates and returns contents of the left panel
	 * @return 
	 */
	protected Pane getLeftPane() {
		final Double WIDTH = 340.0;
		
		final Double button_width  = ( WIDTH - SPACING ) / 3;
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
		
		mButtonReload.setOnAction( ( ActionEvent value ) -> {
			try {
				getPackagesList();
				readPackage();

			} catch (IOException | CrawlerException ex) {
				Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
				alert( "Failed to read packages list" );
			}
		} );
		mButtonSave.setOnAction( ( e ) -> { savePackage(); } );
		
		buttonSet.setSpacing( SPACING );
		
		mButtonSave.setPrefWidth( button_width );
		mButtonReload.setPrefWidth( button_width );
		mButtonDelete.setPrefWidth( button_width );

		buttonSet.getChildren().addAll( mButtonSave, mButtonReload, mButtonDelete );
			
		mPackageList = new ComboBox<>();
		mPackageList.setPrefWidth( WIDTH );
		mPackageList.setOnAction( ( value ) -> {
			try {
					readPackage();

				} catch (IOException | CrawlerException ex) {
					Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
					alert( "Failed to read package information" );
				}
			} );

		try {
			getPackagesList();

		} catch (IOException ex) {
			Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
			alert( "Failed to read packages list" );
		}
		
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
		
		// Process buttons
		HBox proceessButtonSet = new HBox();
		proceessButtonSet.setSpacing( SPACING );
		mButtonCollect = new Button( "Collect" );
		mButtonProcess = new Button( "Process" );
		
		mButtonCollect.setPrefWidth( BUTTON_WIDTH );
		mButtonProcess.setPrefWidth( BUTTON_WIDTH );
		mButtonProcess.setDisable( true );
		proceessButtonSet.setPadding( new Insets( 10, 0, 0, 0 ) );
		
		mButtonCollect.setOnAction( value -> collectFiles() );
		mButtonProcess.setOnAction( value -> makePackage() );
		
		proceessButtonSet.getChildren().addAll( mButtonCollect, mButtonProcess );
	
		pane.getChildren().addAll( buttonSet, mPackageList, codeSet, versionSet, proceessButtonSet );

		return pane;
	}
	
	/**
	 * Creates and returns contents of the right pane
	 * @return 
	 */
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
	
	/**
	 * Creates and returns status bar
	 * @return 
	 */
	protected Node getStatusBar() {
		mStatusBar = new Label( "Inititlized" );
		mStatusBar.setBackground( new Background( new BackgroundFill( Color.GAINSBORO, null, null ) ) );
		mStatusBar.setPrefWidth( WINDOW_WIDTH );
		mStatusBar.setPadding( new Insets( 5.0 )  );
		
		return mStatusBar;
	}
	
	/**
	 * Creates and returns constrain line ( Label + TextArea )
	 * @param text Label text
	 * @param item Target TextArea
	 * @return 
	 */
	protected Node makeConstrainLine( String text, TextArea item ) {
		HBox set = new HBox();
		Label l = new Label( text );
		l.setPrefWidth( LABEL_WIDTH );
		item.setPrefWidth( 500 );
		item.setPrefHeight( 100 );
		set.getChildren().addAll( l, item );
		
		return set;
	}
	
	/**
	 * Populates {@link #mPackageList} with list of package configuration files
	 * from current working directory
	 * @throws IOException 
	 */
	protected void getPackagesList() throws IOException {
		Files.list( Paths.get( "." ) )
			.map( path -> path.getFileName().toString() )
			.forEach( ( name ) -> {
				if ( name.startsWith( PACKAGE_NAME_PREFIX ) && name.endsWith( PACKAGE_NAME_SUFFIX ) ) {
					mPackageList.getItems().add( name );
				}
			} );
	}
	
    /**
     * Saves package configuration to a disk
	 * Callback to click on {@link #mButtonSave}
     * @param event 
     */
	protected void savePackage() {
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
        
        Path packagePath = Paths.get( PACKAGE_NAME_PREFIX + codeName + PACKAGE_NAME_SUFFIX );
        
        if ( Files.isRegularFile( packagePath ) ) {
            try {
                Files.delete( packagePath );

            } catch (IOException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
                alert( "Failed to delete old config file" );
                return;
            }
        }
        
        try {
            Files.write( packagePath, out.toString().getBytes() );
 
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            alert( "Failed to save configuration to file" );
        }
	}
	
	/**
	 * Returns package version as stated in configuration file
	 * @return
	 * @throws CrawlerException if configuration version fields is empty
	 */
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
	
	/**
	 * Shows alert popup
	 * @param text Alert message
	 */
	protected void alert( String text ) {
		Platform.runLater( () -> {
			mAlert.setHeaderText( text );
			mAlert.show();
		} );
	}
	
	/**
	 * Reads contents of TextArea and puts it into List
	 * replacing newlines with commas
	 * @param in Input string
	 * @return 
	 */
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
	
	/**
	 * Reads package configuration and populates corresponding inputs
	 * @throws IOException
	 * @throws CrawlerException 
	 */
	protected void readPackage() throws IOException, CrawlerException {
		String packageName = mPackageList.getValue();
		
		if ( null == packageName || packageName.isEmpty() ) {
			alert( "Select a package" );
			return;
		}
		
		for( String line: Files.readAllLines( Paths.get( packageName ) ) ) {
			String[] parts = line.split( ":" );
			
			if ( parts.length < 2 ) {
				throw new CrawlerException( "Invalid format of packahe file: " + line );
			}

			switch( parts[ 0 ] ) {
				case CODE_TEXT:
					mCodeName.setText( parts[ 1 ] );
					break;
				case VERSION_TEXT:
					String[] versions = parts[ 1 ].split( "\\." );
					
					if ( versions.length < 3 ) {
						throw new CrawlerException( "Invalid package's version number: " + parts[ 1 ] );
					}
					
					mVersionMajor.getValueFactory().setValue( Integer.valueOf(versions[ 0 ] ) );
					mVersionMinor.getValueFactory().setValue( Integer.valueOf(versions[ 1 ] ) );
					mVersionPatch.getValueFactory().setValue( Integer.valueOf(versions[ 2 ] ) );
					break;
				case INCL_FILE_TEXT:
					mIncludeFile.setText( parts[ 1 ].replace( ',', '\n' ) );
					break;
				case EXCL_FILE_TEXT:
					mExcludeFile.setText( parts[ 1 ].replace( ',', '\n' ) );
					break;
				case INCL_FOLDER_TEXT:
					mIncludeFolder.setText( parts[ 1 ].replace( ',', '\n' ) );
					break;
				case EXCL_FOLDER_TEXT:
					mExcludeFolder.setText( parts[ 1 ].replace( ',', '\n' ) );
					break;
				case INCL_REGEX_TEXT:
					mIncludeRegex.setText( parts[ 1 ].replace( ',', '\n' ) );
					break;
				case EXCL_REGEX_TEXT:
					mExcludeRegex.setText( parts[ 1 ].replace( ',', '\n' ) );
					break;
			}
		}
	}
	
    /**
     * Collects package's files into {@link #mFiles}
     */
	protected void collectFiles() {
		mButtonProcess.setDisable( true );
		mButtonCollect.setDisable( true );
		
		if ( null != mFiles ) {
			mFiles.clear();
		}

		Collector collector = new Collector( mIncludeFile, mExcludeFile, mIncludeFolder,
				mExcludeFolder, mIncludeRegex, mExcludeRegex );
		
		new Thread( () -> {
			try {
				setFiles( collector.get() );
				status( String.format( "Collected %d files", mFiles.size() ) );
				mButtonProcess.setDisable( false );

			} catch (IOException ex) {
				Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
				alert( "Failed to collect files" );
			}
			
			mButtonCollect.setDisable( false );

		} ).start();
	}
	
	/**
	 * {@link #mFiles} setter
	 * @param files 
	 */
	protected void setFiles( ArrayList<Path> files ) {
		mFiles = files;
	}
	
    /**
     * Prints text to status bar
     * @param text Message
     */
	public void status( String text ) {
		Platform.runLater( () -> {
			mStatusBar.setText( text );
		} );
	}
    
	/**
	 * Creates package
	 */
    protected void makePackage() {
		mButtonCollect.setDisable( true );
		mButtonProcess.setDisable( true );
        
        String code = mCodeName.getText();
        
        try {
			if ( null == code || code.isEmpty() ) {
				throw new CrawlerException( "Package name field is empty" );
			}

			if ( null == mFiles || mFiles.isEmpty() ) {
				throw new CrawlerException( "Files set is empty" );
			}

			Packager packager = new Packager( mFiles, code, mVersionMajor, mVersionMinor, mVersionPatch );
            packager.runIt();
			savePackage();
 
        } catch ( Exception ex ) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            alert( ex.toString() );
        }
		
		mButtonCollect.setDisable( false);
		mButtonProcess.setDisable( false);
	}
}

class CrawlerException extends Exception{
	public CrawlerException( String message ) {
		super( message );
	}
}

class Profiler {
    private static ArrayDeque<Record> mPull = new ArrayDeque<>();
    
    static public void record( String name ) {
        if ( !Profiler.mPull.isEmpty() && Profiler.mPull.peekLast().is( name ) ) {
            System.out.println( Profiler.mPull.pop() );

        } else {
            Profiler.mPull.add( new Profiler.Record( name ) ); 
        }
    }
    
    static class Record {
        private String mRecord;
        private long mTime;
        
        private Record( String name ) {
            mRecord = name;
            mTime = System.currentTimeMillis();
        }
        
        public boolean is( String name ) {
            return mRecord.equals( name );
        }
        
        @Override
        public String toString() {
            return String.format( "%s: Time: %2.4f", mRecord, ( System.currentTimeMillis() - mTime ) / 1000.0 );
        }
        
    }
}
