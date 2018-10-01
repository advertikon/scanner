package ua.com.advertikon.stat;

import com.sun.javafx.css.CssError;
import ua.com.advertikon.helper.*;

import java.time.*;
import java.util.regex.*;
import java.time.format.*;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatDB extends DBhelper {
	final String TABLE          = "modules";
	final String TABLE_CHART_ID = "chart_id";
	final String TABLE_VISIT    = "visits";

	StatDB( ) {
		super();
		
		try( Statement stm = connection.createStatement() ) {
			stm.execute( "create table if not exists " + TABLE_CHART_ID + " (id integer, chart text)" );
			stm.execute( "create table if not exists " + TABLE_VISIT +
				" (date DATETIME not null, "
					+ "ip VARCHAR(40) not null, "
					+ "country VARCHAR(4) not null, "
					+ "referrer varchar(200) not null, "
					+ "id int not null, "
					+ "filter_category VARCHAR (50) not null, "
					+ "filter_license VARCHAR(6) not null, "
					+ "filter_rating VARCHAR(10) not null, "
					+ "filter_download_id VARCHAR(20) not null, "
					+ "filter_member_type VARCHAR(20) not null, "
					+ "sort VARCHAR(20)not null, "
					+ "page int not null, "
					+ "search VARCHAR(255) not null )" );
			
		} catch (SQLException ex) {
			Logger.getLogger(StatDB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns statistics for commercial modules
	 * @param {Object} data Query data wrapper
	 * @return {List} Statistics data
	 */
	List<Map<String, String>> getStatisticData( QueryData data ) {
            List<Map<String, String>> ret = new ArrayList<>();

            try {
                String q = "SELECT m.*, IFNULL( c.chart, '' ) as chart," +
                                "( ( MAX( sales ) - MIN( sales ) ) * AVG( price ) ) as profits," +
                                "( MAX( sales ) - MIN( sales ) ) as total_sales ";

                q += ", ( ( MAX( sales ) - MIN( sales ) ) * AVG( price ) ) * 30 / DATEDIFF( MAX( date ), MIN( date ) ) as month_profits";
                q += " FROM " + TABLE + " as m LEFT JOIN " + TABLE_CHART_ID + " as c ON ( m.id = c.id AND c.chart = 'commercial' )" +
                                "WHERE price > 0 AND ";
                q += data.getDateRestriction();
                q += " GROUP by m.id HAVING profits > " + data.profits + " ORDER BY profits DESC LIMIT " + data.limit;

                Log.debug( q );

                try {
					Instant start = Instant.now();
					ret = query( q );
					Log.debug( "DQ query: " + Duration.between( start, Instant.now() ).toMillis() + " msec" );

                } catch ( SQLException e ) {
                        Log.error( "stat_db::getStatisticData: " + e.getMessage() );
                }

            } catch ( Exception e ) {
                    Log.error( "stat_db::getStatisticData: " + e.getMessage() );
            }

            Log.debug( "Items count: " + ret.size() );

            return ret;
	}

	List<Map<String, String>> getFreeStatisticData( QueryData data ) {
            List<Map<String, String>> ret = new ArrayList<>();

            try {
                String q = "SELECT m.*, IFNULL( c.chart, '' ) as chart, ( MAX( sales ) - MIN( sales ) ) as total_sales  ";

                q += " FROM " + TABLE + " as m LEFT JOIN " + TABLE_CHART_ID + " as c ON ( m.id = c.id AND c.chart = 'free' ) WHERE price < 1 AND ";
                q += getDateRestriction( data );
                q += " GROUP BY id ORDER BY total_sales DESC LIMIT " + data.limit;

                Log.debug( q );

                try {
                        Instant start = Instant.now();
                        ret = query( q );
                        Log.debug( "DQ query: " + Duration.between( start, Instant.now() ).toMillis() + " msec" );

                } catch ( SQLException e ) {
                        Log.error( "stat_db::getFreeStatisticData: " + e.getMessage() );
                }


            } catch ( Exception e ) {
                    Log.error( "stat_db::getFreeStatisticData: " + e.getMessage() );
            }

            return ret;
	}

	List<Map<String, String>> getVisitsStatisticData( QueryData data ) {
            List<Map<String, String>> ret = new ArrayList<>();
            String from = "";
            String to = "";

            try {
                String q = "SELECT * FROM " + TABLE_VISIT + " WHERE ";
                q += getDateRestriction( data );
                q += " ORDER BY date DESC LIMIT " + data.limit;

                Log.debug( q );

                try {
                    ret = query( q );

                } catch ( SQLException e ) {
                        Log.error( "stat_db::getVisitsStatisticData: " + e.getMessage() );
                }

            } catch ( Exception e ) {
                    Log.error( "stat_db::getVisitsStatisticData: " + e.getMessage() );
            }

            return ret;
	}

	/**
	 * Checks if module is in chart table
	 * @param id
	 * @param chart
	 * @return {boolean} Result
	 */
	public boolean isModuleInChart( String id, String chart ) {
            boolean ret = false;
            ResultSet rs = null;
            String q = "SELECT id FROM " + TABLE_CHART_ID + " WHERE id = ? AND chart = ?";

            try ( PreparedStatement s = connection.prepareStatement( q ) ) {
                s.setString( 1, id );
                s.setString( 2, chart );

                rs = s.executeQuery();

                while( rs.next() ) {
                        if ( rs.getInt( "id" ) > 0  ) {
                                ret = true;
                        }
                }

            } catch ( Exception e ) {
                    Log.error( "stat_db::isModuleInChart: " + e.getMessage() );
            }

            return ret;
	}

	public boolean addModuleChart( String id, String chart ) {
            boolean ret = false;
            String q = "INSERT INTO " + TABLE_CHART_ID + " SET id = ?, chart = ?";

            try ( PreparedStatement s = connection.prepareStatement( q ) ) {
                s.setString( 1, id );
                s.setString( 2, chart );

                ret = s.executeUpdate() > 0;

            } catch ( Exception e ) {
                Log.error( "stat_db::addModuleChart: " + e.getMessage() );
            }

            return ret;
	}

	public boolean deleteModuleChart( String id, String chart ) {
            boolean ret = false;
            String q = "DELETE FROM " + TABLE_CHART_ID + " WHERE id = ? AND chart = ?";

            try ( PreparedStatement s = connection.prepareStatement( q ) ) {
                s.setString( 1, id );
                s.setString( 2, chart );

                ret = s.executeUpdate() > 0;

            } catch ( Exception e ) {
                Log.error( "stat_db::deleteModuleChart: " + e.getMessage() );
            }

            return ret;
	}

	public List<Map<String, String>> getChartModules( String chartName ) {
            String q = "SELECT id FROM " + TABLE_CHART_ID + " WHERE chart = ?";
            List<Map<String, String>> rs = new ArrayList<>();

            try ( PreparedStatement s = connection.prepareStatement( q ) ) {
                s.setString( 1, chartName );
                rs = getData( s.executeQuery() );

            } catch ( Exception e ) {
                Log.error( "stat_db::deleteModuleChart: " + e.getMessage() );
            }

            return rs;
	}

	/**
	* Returns module statistics
	 * @param id
	 * @param data
	* @return {List} Result 
	*/
	public List<Map<String, String>> getModule( String id, QueryData data ) {
		List<Map<String, String>> rs = new ArrayList<>();
		String minSales = getMinSales( id, data );
		
		String q = String.format(
			"select id, left(date,19) as %s, max(sales) - %s as %s from %s where id = %s and %s group by %s order by %s",
			TimeLine.DATE_FIELD,
			minSales,
			TimeLine.TARGET_FIELD,
			TABLE,
			id,
			data.getDateRestriction(),
			TimeLine.DATE_FIELD,
			TimeLine.DATE_FIELD
		);

		try {
			rs = query( q );

		} catch (SQLException ex) {
			Logger.getLogger(StatDB.class.getName()).log(Level.SEVERE, null, ex);
		}

		TimeLine tl = data.getTimeLine();
		tl.fill( rs );

		return tl.toList();
	}

	/**
	 * Returns minimum sales value for specific module for specific time period
	 * @param id
	 * @param data
	 * @return String Sales value
	 */
	protected String getMinSales( String id, QueryData data ) {
		String ret = "0";
		String q = "SELECT MIN( sales ) as min_sales FROM " + TABLE + " WHERE id = ? AND ";
		ResultSet rs = null;

		q += getDateRestriction( data );

		try ( PreparedStatement s = connection.prepareStatement( q ) ) {
			s.setString( 1, id );
			rs = s.executeQuery();

			if ( rs.next() ) {
				ret = rs.getString( "min_sales" );
			}

		} catch ( Exception e ) {
			Log.error( "stat_db::getMinSales: " + e.getMessage() );
		}

		return ret;
	}

	protected String getDateRestriction( QueryData data ) {
		String q = "";
		String from = "";

		if ( "sqlite".equals( mode ) ) {
			if ( !data.period.equals( "" ) ) {
				if ( 0 == data.period.compareToIgnoreCase( "week" ) ) {
					from = "-7 days";

				} else {
					from = "-1 " + data.period;
				}

				q += " date >= date( 'now', '" + from + "')";

			} else {
				q += " date >= date( '" + data.dateFrom + "' ) AND date <= date( '" + data.dateTo + "' )";
			}
			
		} else {
			if ( !data.period.equals( "" ) ) {
				q += " date >= date_sub( now(), interval 1 " + data.period + ")";

			} else {
				q += " date >= '" + data.dateFrom + "' AND date <= '" + data.dateTo + "'";
			}
		}

		return q;
	}

	/**
	 * Returns minimum sales value for specific module for specific time period
	 * @param data
	 * @return String Sales value
	 */
	protected List<Map<String, String>> getDates( QueryData data ) {
		List<Map<String, String>> ret = new ArrayList<>();
		String q = "SELECT DISTINCT DATE( date ) as date FROM " + TABLE + " WHERE ";

		q += getDateRestriction( data );
		q += " ORDER BY date";

		try {
			ret = query( q );

		} catch ( SQLException e ) {
			Log.error( "stat_db::getDates: " + e );
		}

		return ret;
	}

	public boolean saveVisit( String[] data ) {
		Boolean ret = false;

		String date     = data[ 0 ];
		String ip       = data[ 1 ];
		String country  = data.length >= 3 ? data[ 2 ] : "";
		String query    = data.length >= 4 ? data[ 3 ] : "";
		String referrer = data.length >= 5 ? data[ 4 ] : "";

		String module_id   = "";
		String category    = "";
		String license     = "";
		String sort        = "";
		String page        = "0";
		String rating      = "";
		String download    = "";
		String member_type = "";
		String search      = "";

		Matcher extension_id_matcher = Pattern.compile( "extension_id=(\\d+)" ).matcher( referrer );
		Matcher id_matcher           = Pattern.compile( "id=(\\d+)" ).matcher( query );
		Matcher extension_matcher    = Pattern.compile( "extension=(\\d+)" ).matcher( query );

		Matcher category_matcher    = Pattern.compile( "filter_category_id=([^&]+)" ).matcher( referrer );
		Matcher license_matcher     = Pattern.compile( "filter_license=([^&]+)" ).matcher( referrer );
		Matcher sort_matcher        = Pattern.compile( "sort=([^&]+)" ).matcher( referrer );
		Matcher page_matcher        = Pattern.compile( "page=([^&]+)" ).matcher( referrer );
		Matcher rating_matcher      = Pattern.compile( "filter_rating=([^&]+)" ).matcher( referrer );
		Matcher download_matcher    = Pattern.compile( "filter_download=([^&]+)" ).matcher( referrer );
		Matcher member_type_matcher = Pattern.compile( "filter_member_type=([^&]+)" ).matcher( referrer );
		Matcher search_matcher      = Pattern.compile( "filter_search_type=([^&]+)" ).matcher( referrer );

		if ( id_matcher.find() ) {
			module_id = id_matcher.group( 1 );

		} else if ( extension_id_matcher.find() ) {
			module_id = extension_id_matcher.group( 1 );

		} else if ( extension_matcher.find() ) {
			module_id = extension_matcher.group( 1 );
		}

		if ( "".equals( module_id ) ) {
			Log.error( "Failed to detect module ID for query: " + query + " and referrer: " + referrer );
			return false;
		}

		if ( category_matcher.find() ) {
			switch( category_matcher.group( 1 ) ) {
				case "20" :
					category = "Marketplace";
				break;
				case "1":
					category = "Themes";
				break;
				case "2":
					category = "Languages";
				break;
				case "3":
					category = "Payment Gateways";
				break;
				case "4":
					category = "Shipping Methods";
				break;
				case "5":
					category = "Modules";
				break;
				case "6":
					category = "Order Totals";
				break;
				case "19":
					category = "Product Feeds";
				break;
				case "7":
					category = "Reports";
				break;
				case "8":
					category = "Other";
				break;
				case "21":
					category = "VQMOD";
				break;
				default:
					category = "Unknown";
			}
		}

		if ( license_matcher.find() ) {
			license = license_matcher.group( 1 );
		}

		if ( sort_matcher.find() ) {
			sort = sort_matcher.group( 1 );
		}

		if ( page_matcher.find() ) {
			page = page_matcher.group( 1 );
		}

		if ( rating_matcher.find() ) {
			rating = rating_matcher.group( 1 );
		}

		if ( download_matcher.find() ) {
			switch ( download_matcher.group( 1 ) ) {
				case "56":
					download = "3.0.3.0b";
				break;
				case "55":
					download = "3.0.2.0";
				break;
				case "53":
					download = "3.0.1.2";
				break;
				case "52":
					download = "3.0.1.1";
				break;
				case "49":
					download = "3.0.0.0";
				break;
				case "47":
					download = "2.3.0.2";
				break;
				case "46":
					download = "2.3.0.1";
				break;
				case "45":
					download = "2.3.0.0";
				break;
				case "44":
					download = "2.2.0.0";
				break;
				case "43":
					download = "2.1.0.2";
				break;
				case "42":
					download = "2.1.0.1";
				break;
				case "41":
					download = "2.0.3.1";
				break;
				case "40":
					download = "2.0.2.0";
				break;
				case "39":
					download = "2.0.1.1";
				break;
				case "38":
					download = "2.0.1.0";
				break;
				case "37":
					download = "2.0.0.0";
				break;
				case "36":
					download = "1.5.6.4";
				break;
				case "35":
					download = "1.5.6.3";
				break;
				case "34":
					download = "1.5.6.2";
				break;
				case "33":
					download = "1.5.6.1";
				break;
				case "32":
					download = "1.5.6";
				break;
				case "31":
					download = "1.5.5.1";
				break;
				case "30":
					download = "1.5.5";
				break;
				case "29":
					download = "1.5.4.1";
				break;
				case "28":
					download = "1.5.4";
				break;
				case "27":
					download = "1.5.3.1";
				break;
				case "26":
					download = "1.5.3";
				break;
				case "25":
					download = "1.5.2.1";
				break;
				case "24":
					download = "1.5.2";
				break;
				case "23":
					download = "1.5.1.3";
				break;
				case "22":
					download = "1.5.1.2";
				break;
				case "21":
					download = "1.4.9.6";
				break;
				case "20":
					download = "1.5.1.1";
				break;
				case "19":
					download = "1.5.1";
				break;
				case "18":
					download = "1.5.0.5";
				break;
				case "17":
					download = "1.5.0.4";
				break;
				case "16":
					download = "1.5.0.3";
				break;
				case "15":
					download = "1.5.0.2";
				break;
				case "14":
					download = "1.5.0.1";
				break;
				case "13":
					download = "1.5.0";
				break;
				case "12":
					download = "1.4.9.5";
				break;
				case "11":
					download = "1.4.9.4";
				break;
				case "10":
					download = "1.4.9.3";
				break;
				case "9":
					download = "1.4.9.2";
				break;
				case "8":
					download = "1.4.9.1";
				break;
				case "7":
					download = "1.4.9";
				break;
				case "5":
					download = "1.4.8b";
				break;
				case "4":
					download = "1.4.8";
				break;
				case "2":
					download = "1.4.7";
				break;
			}
		}

		if ( member_type_matcher.find() ) {
			member_type = member_type_matcher.group( 1 );
		}

		if ( search_matcher.find() ) {
			search = search_matcher.group( 1 );
		}

		String q = "INSERT INTO " + TABLE_VISIT 
			+ " ( date, ip, country, referrer, id, filter_category, filter_license, "
			+ "filter_rating, filter_download_id, filter_member_type, sort, page, search ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try ( PreparedStatement s = connection.prepareStatement( q ) ) {
			s.setString( 1, date );
			s.setString( 2, ip );
			s.setString( 3, country );
			s.setString( 4, referrer );
			s.setString( 5, module_id );
			s.setString( 6, category );
			s.setString( 7, license );
			s.setString( 8, rating );
			s.setString( 9, download );
			s.setString( 10, member_type );
			s.setString( 11, sort );
			s.setString( 12, page );
			s.setString( 13, search );

			ret = s.executeUpdate() > 0;

		} catch ( Exception e ) {
			Log.error( "stat_db::saveVisit: " + e.getMessage() );
		}

		return ret;
	}

	public String getLastVisit() {
		String ret = "null";

		try{
			ret = query( "SELECT MAX(date) as max_date FROM " + TABLE_VISIT ).get( 0 ).get( "max_date" );

		} catch ( SQLException e ) {
			Log.error( "stat_db::getLastVisit: " + e.getMessage() );
		}

		return ret == null ? "null" : ret;
	}

	List<Map<String, String>> getVisits( String id, QueryData data ) {
		List<Map<String, String>> ret = new ArrayList<>();
		String format = data.getFormat();

		try {
			String q = String.format( "select count(*) as %s, left(date, 19) as "
				+ "%s from %s where id = '%s' and %s group by %s order by %s",
					TimeLine.TARGET_FIELD,
					TimeLine.DATE_FIELD,
					TABLE_VISIT,
					id,
					data.getDateRestriction(),
					TimeLine.DATE_FIELD,
					TimeLine.DATE_FIELD
				);

			Log.debug( q );

			try {
				ret = query( q );
				
			} catch ( SQLException e ) {
				Log.error( "stat_db::getVisits: " + e.getMessage() );
			}

		} catch ( Exception e ) {
			Log.error( "stat_db::getVisits: " + e.getMessage() );
		}
		
		TimeLine tl = data.getTimeLine();
		tl.fill( ret );

		return tl.toList();
	}
	
	protected List<Map<String, String>> normilizeTimeLine( List<Map<String,String>> in, QueryData query ) {
		System.out.println( query.getTimeLine() );
		List<Map<String, String>> out = new ArrayList<>();
		
		return out;
	}
}