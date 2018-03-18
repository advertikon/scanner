package com.ua.advertikon.console;

import com.ua.advertikon.helper.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.application.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.collections.*;
import javafx.beans.value.*;

public class Logger extends ScrollPane {
	protected TextFlow area = new TextFlow();
	protected Color color = Color.WHITE;
	protected ScrollPane pane = null;
	protected final int MAX_RECORDS = 200000;
	protected Region view = null;

	public Logger() {
		area.setBackground( new Background( new BackgroundFill( Paint.valueOf( "#000000" ) , null, null ) ) );
		area.setLineSpacing( 0.1d );
		area.setPadding( new Insets( 5.0 ) );

		setContent( area );
		setFitToWidth( true );
		setFitToHeight( true );

		area.setMaxHeight( Double.MAX_VALUE );

		area.getChildren().addListener( ( ListChangeListener.Change<? extends Node> c ) -> {
			setVvalue( 1.0d );
		} );

		// heightProperty().addListener( ( ObservableValue<? extends Object> observable, Object oldValue, Object newValue ) -> {
		// 	Double height = 0.0;
		// 	Parent parent = getParent();
		// 	Region p = (Region)parent;

		// 	for( Node c: parent.getChildrenUnmodifiable() ) {
		// 		Region child = (Region)c;
		// 		height += child.getHeight();
		// 	}

		// 	System.out.println( p.getHeight() + "|" + height + "|" + newValue );
		// 	setWidth( p.getHeight() - height );
		// } );
	}

	public void println( String text ) {
		print( text + "\n" );
	}

	public void println( String[] text ) {
		for ( int i = 0; i < text.length; i++ ) {
			if ( text[ i ].equals( "" ) ) continue;

			println( text[ i ] );
		}
	}

	public void print( String text ) {
		String[] lines = text.split( "\\u001b\\[" );

		Platform.runLater( new Runnable() {
			public void run() {
				int index = 0;
				String line = "";

				for( int i = 0; i < lines.length; i++ ) {
					if ( lines[ i ].length() == 0 ) continue;

					index = lines[ i ].substring( 0, Integer.min( 10, lines[ i ].length() ) ).indexOf( "m" ) + 1;
					String part = lines[ i ].substring( 0, index );

					if ( index < 6 && index >= 0 && lines.length > 1 ) {
						switch( part ) {
							case "90m":
								color( Color.GRAY );
							break;
							case "0;90m":
								color( Color.BLACK );
							break;
							case "0;91m":
								color( Color.RED );
							break;
							case "0;92m":
								color( Color.GREEN );
							break;
							case "0;93m":
								color( Color.YELLOW );
							break;
							case "0;94m":
								color( Color.BLUE );
							break;
							case "0;95m":
								color( Color.PURPLE );
							break;
							case "0;96m":
								color( Color.CYAN );
							break;
							case "0;97m":
								color( Color.WHITE );
							break;
							case "0m":
								color( Color.WHITE );
							break;
						}

						line = lines[ i ].substring( index );

					} else {
						line = lines[ i ];
					}

					Text t = new Text( line );
					t.setFill( color );
					area.getChildren().add( t );
				}

				if ( area.getChildren().size() > MAX_RECORDS ) {
					area.getChildren().remove( 0, MAX_RECORDS );
				}
			}
		} );

	}

	protected void color( Color c ) {
		color = c;
	}

	public void error( Exception e ) {
		Color old_color = color;
		color = Color.RED;
		println( e.getMessage() );
		color = old_color;
	}

	public void clear() {
		Platform.runLater( new Runnable() {
			public void run() {
				area.getChildren().clear();
			}
		} );
	}



	// @Override protected void layoutChildren() { Log.debug( "cildte" );
	// 	double w = getWidth();
	// 	double h = getHeight();
	// 	layoutInArea( view, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER );

	// 	pane.setVvalue( 1.0d );
	// }

	// @Override protected double computePrefWidth( double height ) {
	// 	return 750;
	// }

	// @Override protected double computePrefHeight( double width ) {
	// 	return 500;
	// }
}