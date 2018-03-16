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
	protected final int MAX_RECORDS = 200;

	public Logger() {
		area.setBackground( new Background( new BackgroundFill( Paint.valueOf( "#000000" ) , null, null ) ) );
		area.setLineSpacing( 0.5d );

	}

	public Pane getArea() {
		return area;
	}

	public void println( String text ) {
		print( text + "\n" );

	}

	public void print( String text ) {
		Text t = new Text( text + "\n" );
		t.setFill( color );
		pane.setVvalue( 1.0d );

		Platform.runLater( new Runnable() {
			public void run() {
				area.getChildren().add( t );
				pane.setVvalue( 1.0d );
			}
		} );

		if ( area.getChildren().size() > MAX_RECORDS ) {
			area.getChildren().remove( 0, MAX_RECORDS );
		}
	}

	public void error( Exception e ) {
		Color old_color = color;
		color = Color.RED;
		println( e.getMessage() );
		color = old_color;
	}

	public ScrollPane instance() {
		pane = new ScrollPane( area );
		pane.setFitToWidth( true );
		pane.setFitToHeight( true );
		return pane;
	}

	public void clear() {
		Platform.runLater( new Runnable() {
			public void run() {
				area.getChildren().clear();
			}
		} );
	}
}