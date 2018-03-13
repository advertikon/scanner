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

import com.ua.advertikon.helper.*;

public class Console extends Application {
	@Override
	public void start( Stage stage ) {

		// Scene
		stage.setTitle( "Statistics" );
		Group root = new Group();
		Scene scene = new Scene( root, 1000, 700 );
		// scene.getStylesheets().add( "css/style.css" );
		stage.setScene( scene );


	}
}