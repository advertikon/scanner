package ua.com.advertikon.helper;

import javafx.scene.web.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.application.*;

public class Browser extends Region {
	final protected WebView view = new WebView();
	final protected WebEngine engine = view.getEngine();

	public Browser() {
		getChildren().add( view );
	}

	private Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	@Override protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea( view, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER );
	}

	@Override protected double computePrefWidth( double height ) { Log.debug( "width" );
		return 750;
	}

	@Override protected double computePrefHeight( double width ) { Log.debug( "height" );
		return 500;
	}

	public void load( String url ) {
		Platform.runLater( () -> {
			engine.load( url );
		} );
	}

	public void loadContent( String content ) {
		Platform.runLater( () -> {
			engine.loadContent( content );
		} );
	}

	public void clear() {
		Platform.runLater( () -> {
			engine.loadContent( "" );
		} );
	}
}