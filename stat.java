import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.*;

import javafx.geometry.*;

import javax.swing.*;

public class stat extends Application {
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
		// rootNode.setAlignment( Pos.CENTER );

		Scene scene = new Scene( root, 300, 300 );
		primaryStage.setScene( scene );

		TabPane tabPane = new TabPane();
		BorderPane borderPane = new BorderPane();
		
		Tab statTab = new Tab( "Statistic" );
		// HBox statHBox = new HBox();
		JTable table = new JTable();
		ScrollPane scrollPane = new ScrollPane();
		statTab.setClosable( false );
		scrollPane.setContent( table );

		// statHBox.getChildren().add( new Label( "Statistics" ) );
		// statHBox.getChildren().add( scrollPane );
		// statHBox.setAlignment( Pos.CENTER );
		statTab.setContent( scrollPane );
		tabPane.getTabs().add( statTab );

		Tab graphTab = new Tab( "Graphics" );
		graphTab.setClosable( false );
		HBox graphHBox = new HBox();
		graphHBox.getChildren().add( new Label( "Graphics" ) );
		graphHBox.setAlignment( Pos.CENTER );
		graphTab.setContent( graphHBox );
		tabPane.getTabs().add( graphTab );

		// Label label = new Label( "Hello, World" );
		// rootNode.getChildren().add( label );

		// Button button = new Button( "Click me!!!" );
		// button.setOnAction( ( ae ) -> {
		// 	label.setText( "clicked" );
		// } );
		// rootNode.getChildren().add( button );

		borderPane.prefHeightProperty().bind( scene.heightProperty() );
        borderPane.prefWidthProperty().bind( scene.widthProperty() );
        
        borderPane.setCenter( tabPane );
        root.getChildren().add( borderPane );

		primaryStage.show();
	}

	public void stop() {
		System.out.println( "Stop" );
	}
}