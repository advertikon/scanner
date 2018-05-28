// Query object class
package ua.com.advertikon.stat;

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