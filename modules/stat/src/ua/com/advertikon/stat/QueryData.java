// Query object class
package ua.com.advertikon.stat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryData {
    public String period   = "";
    public String dateFrom = "";
    public String dateTo   = "";
    public String profits  = "";
    public String limit    = "200";
	
	final protected String FORMAT_DAY_SQL   = "%H:%i";
	final protected String FORMAT_WEEK_SQL  = "%a";
	final protected String FORMAT_MONTH_SQL = "%d %b";
	final protected String FORMAT_YEAR_SQL  = "%d %b";
	
	final protected String FORMAT_DAY   = "H:s";
	final protected String FORMAT_WEEK  = "E";
	final protected String FORMAT_MONTH = "d MMM";
	final protected String FORMAT_YEAR  = "d MMM";
	
	final protected String SQL_DATE_FORMAT = "yyyy'-'MM'-'dd";
	
	enum Interval {
		Day, Week, Month, Year
	}

    QueryData( String period, String profits ) {
            this.period = period;
            this.profits = profits;
    }
	
	String getFormat() {
		switch ( getInterval() ) {
			case Day:
				return FORMAT_DAY_SQL;
			case Week:
				return FORMAT_WEEK_SQL;
			default:
				return FORMAT_MONTH_SQL;
		}
	}
	
	String getDateRestriction() {
		if ( !period.isEmpty() ) {
			return " date >= date_sub( now(), interval 1 " + period + ")";

		} else {
			return " date >= '" + dateFrom + "' AND date <= '" + dateTo + "'";
		}
	}
	
	Interval getInterval() {
		if ( period.isEmpty() ) {
			LocalDate dtFrom = LocalDate.parse( dateFrom, DateTimeFormatter.ofPattern( SQL_DATE_FORMAT ) );
			LocalDate dtTo   = LocalDate.parse( dateTo, DateTimeFormatter.ofPattern( SQL_DATE_FORMAT ) );

			if ( dtTo.minusDays( 1 ).compareTo( dtFrom ) == 0 ) {
				return Interval.Day;

			} else if ( dtTo.minusDays( 6 ).compareTo( dtFrom ) <= 0 ) {
				return Interval.Week;

			} else {
				return Interval.Month;
			}

		} else {
			switch ( period.toLowerCase() ) {
				case "day":
					return Interval.Day;
				case "week":
					return Interval.Week;
				default:
					return Interval.Month;
			}
		}
	}
	
	TimeLine getTimeLine() {
		ChronoUnit step;
		int count;
		TimeLine timeLine = new TimeLine();
		LocalDateTime now = LocalDateTime.now();
		String format;

		switch ( getInterval() ) {
			case Day:
				step = ChronoUnit.HOURS;
				count = 24;
				format = FORMAT_DAY;
				break;
			case Week:
				step = ChronoUnit.DAYS;
				count = 7;
				format = FORMAT_WEEK;
				break;
			case Month:
				step = ChronoUnit.DAYS;
				count = 31;
				format = FORMAT_MONTH;
				break;
			default:
				step = ChronoUnit.MONTHS;
				count = 12;
				format = FORMAT_YEAR;
				break;
		}
		
		for( long i = count - 1; i >= 0; i-- ) {
			LocalDateTime d = now.minus( i, step );
			timeLine.add( d, d.format( DateTimeFormatter.ofPattern( format ) ) );
		}
		
		return timeLine;
	}
}

class TimeLine {
	final protected ArrayList<TimeLineItem> mItems = new ArrayList<>();
	final static public String DATE_FIELD   = "date_f";
	final static public String TARGET_FIELD = "count";
	
	void add( LocalDateTime date, String formattedDate ) {
		mItems.add( new TimeLineItem( date, formattedDate ) );
	}
	
	void fill( List<Map<String, String>> in ) {
		in.stream().forEach( i -> {
			incrementCount( i );
		} );
	}
	
	void incrementCount( Map<String, String> item ) {
		TimeLineItem prev = null;

		for( TimeLineItem innerItem: mItems ) {
			if ( innerItem.compareTo( item.get( DATE_FIELD ) ) > 0 ) {
				if ( null != prev ) {
					prev.increment( item.get( TARGET_FIELD ) );

				} else {
					innerItem.increment( item.get( TARGET_FIELD ) );
				}

				return;
			}
			
			prev = innerItem;
		}
		
		if ( null != prev ) {
			prev.increment( item.get( TARGET_FIELD ) );
		}
	}
	
	List toList() {
		List<HashMap<String, String>> out = mItems.stream().map( (TimeLineItem i) -> {
			HashMap<String, String> m = new HashMap<>();
			m.put( TimeLine.TARGET_FIELD, i.getCount() );
			m.put( TimeLine.DATE_FIELD, i.getFormattedDate() );
			return m;
		} ).collect( Collectors.toList() );
		
		return out;
	}
}

class TimeLineItem {
	final protected String MYSQL_DATE_FORMAT = "yyyy'-'MM'-'dd' 00:00:00'";

	protected LocalDateTime mDate;
	protected String mFormattedDate;
	protected String mSQLDate;
	protected int mCount = 0;

	public TimeLineItem( LocalDateTime date, String formattedDate) {
		mDate = date;
		mFormattedDate = formattedDate;
		mSQLDate = date.format(DateTimeFormatter.ofPattern( MYSQL_DATE_FORMAT ) );
	}
	
	public int compareTo( String dateString ) {
		return mSQLDate.compareTo( dateString );
	}
	
	public void increment( String count ) {
		mCount += Integer.parseInt(count, 10 );
	}
	
	public String getFormattedDate() {
		return mFormattedDate;
	}
	
	public String getCount() {
		return String.valueOf( mCount );
	}
}