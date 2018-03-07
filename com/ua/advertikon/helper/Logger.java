package com.ua.advertikon.helper;

paublic class Logger {
	private ColorRed = "\e[0;31m";
	private ColorEnd = "\e[0m";

	public void error( String message ) {
		System.err.printf( "%s%s%s", ColorRed, message, ColorEnd );
	}
}