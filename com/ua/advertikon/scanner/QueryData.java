// Query object class
package com.ua.advertikon.scanner;

public class QueryData {
	public String period   = "";
	public String dateFrom = "";
	public String dateTo   = "";
	public String profits  = "";
	public String limit    = "200";

	QueryData( String period, String profits ) {
		this.period = period;
		this.profits = profits;
	}
}