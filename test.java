import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.*;

import javafx.geometry.*;

import java.io.*;

public class test extends Application {
	public static void main( String[] args ) {
		launch( args );
	}

	public void init() {
		System.out.println( "Initializing" );
	}

	public void start( Stage mainStage ) {
		System.out.println( "Run module" );

		mainStage.setTitle( "Statistics" );
		FlowPane rootNode = new FlowPane( 10, 10 );
		rootNode.setAlignment( Pos.CENTER );

		Scene myScene = new Scene( rootNode, 300, 300 );
		mainStage.setScene( myScene );

		Label label = new Label( "Hello, World" );
		rootNode.getChildren().add( label );

		Button button = new Button( "Click me!!!" );
		button.setOnAction( ( ae ) -> {
			label.setText( "clicked" );
		} );
		rootNode.getChildren().add( button );

		mainStage.show();
	}

	public void stop() {
		System.out.println( "Stop" );
	}
}