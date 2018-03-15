package com.ua.advertikon.console;

import com.ua.advertikon.helper.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.application.*;
import javafx.scene.control.*;

public class Logger {
	protected TextFlow area = new TextFlow();
	protected Color color = Color.WHITE;
	protected ScrollPane pane = null;

	public Logger() {
		area.setBackground( new Background( new BackgroundFill( Paint.valueOf( "#000000" ) , null, null ) ) );

	}

	public Pane getArea() {
		return area;
	}

	public void println( String text ) {
		Text t = new Text( text + "\n" );
		t.setFill( color );
		pane.setVvalue( 1.0d );

		Platform.runLater( new Runnable() {
			public void run() {
				area.getChildren().add( t );
				pane.setVvalue( 1.0d );
			}
		} );

	}

	public void print( String text ) {
		Text t = new Text( text );
		t.setFill( color );

		area.getChildren().add( t );
	}

	public ScrollPane instance() {
		pane = new ScrollPane( area );
		pane.setFitToWidth( true );
		pane.setFitToHeight( true );
		return pane;
	}
}