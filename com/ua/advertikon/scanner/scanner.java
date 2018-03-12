package com.ua.advertikon.scanner;

import com.ua.advertikon.helper.*;

import java.net.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.nio.file.*;

public class scanner {
	final String URL_STRING = "https://www.opencart.com/index.php?route=marketplace/extension/info&extension_id=";
	final int THREAD_COUNT = 20;
	final int MODULES_THRESHOLD = 30000;
	final int MODULES_FAILED_THRESHOLD = 50;

	int current_page = 0;
	int average = 0;
	long page_count = 0;
	int page_success = 0;
	int page_falure = 0;
	Instant program_start = null;
	int thread_run = 0;
	int failed_page_sequence = 0;
	static String mode = "continue";

	DateTimeFormatter formatterShort = null;
	DateTimeFormatter formatterLong = null;
	DateTimeFormatter outputFormat = null;

	Iterator<Map<String, String>> ids = null;
	scanner_db db = null;

	ThreadGroup group = null;

	public static void main( String[] args ) {
		if ( args.length > 0 ) {
			mode = args[ 0 ];
			Log.debug( "Mode is: " + mode );
		}

		scanner me = new scanner();
		me.crawl();
	}

	public scanner() {
		formatterShort = DateTimeFormatter.ofPattern( "d MMM yyyy" );
		formatterLong  = DateTimeFormatter.ofPattern( "dd MMM yyyy" );
		outputFormat   = DateTimeFormatter.ofPattern( "yyyy'-'MM'-'dd" );

		db = new scanner_db();

		switch ( mode ) {
			case "full":
				current_page = 0;
				Log.debug( "Start scanning from the start" );
			break;
			case "popular":
				ids = db.getWorthModules();
				Log.debug( "Start scanning bestsellers" );
			break;
			default:
				current_page = db.getLastId();
				Log.debug( "Start scanning from module #" + current_page );
			break;
		}
	}

	void crawl() {
		int i = 0;
		thread s = null;
		program_start = Instant.now();

		// try {
		// 	Files.copy(
		// 		Paths.get( "modules.db" ),
		// 		Paths.get( "~modules.db" ),
		// 		new CopyOption[] {
		// 			StandardCopyOption.REPLACE_EXISTING,
		// 			StandardCopyOption.COPY_ATTRIBUTES
		// 		}
		// 	);
			
		// } catch ( IOException e ) {
		// 	Log.error( e );
		// }

		group = new ThreadGroup( "Crawlers" );

		for ( i = 0; i < THREAD_COUNT; i++ ) {
			s = new thread( group, String.valueOf( i ) );
			Log.debug( "Thread started: " + s );
			s.go( this );
		}

		Log.debug( String.format( "%d threads have been launched", THREAD_COUNT ) );
	}

	/**
	 * Picks the next module's ID to process
	 * @return {int} Module ID
	 */
	synchronized int pick() {
		int page = -1; // -1 will stop a thread

		switch ( mode ) {
			case "full":
			case "continue":
				if ( current_page < MODULES_THRESHOLD || failed_page_sequence < MODULES_FAILED_THRESHOLD ) {
					page = ++current_page;
				}
			break;
			case "popular":
				if ( ids.hasNext() ) {
					page = Integer.parseInt( ids.next().get( "id" ) );
				}
			break;
			default:
				Log.exit( String.format( "Unsupported mode: %s", mode ) );
			break;
		}

		Log.debug( "Next ID#" + page );

		return page;
	}

	synchronized void saveData( HashMap<String, String> pageData ) {
		if ( pageData.get( "id" ) != null ) {
			db.saveData( pageData );
		}
	}

	public HashMap<String, String> connect( int page ) throws IOException {
		Instant start = null;
		Instant end = null;
		long duration = 0;
		String out = "";
		long average_time = 0;
		String text = "";
		String price = "";

		LocalDate date = null;
		HashMap<String, String> pageData = new HashMap<String, String>();
		
		start = Instant.now();

		try {
			Document doc = Jsoup.connect( URL_STRING + page ).get();

			page_success++;
			failed_page_sequence = 0;

			Element elName = doc.select( ".container h3" ).first();
			Element elPrice = doc.select( "#price .text-right" ).first();
			Element elDateAdded = doc.select( "#buy .row:last-child .col-xs-7" ).first();
			Element elDateModified = doc.select( "#buy .row:nth-last-child(3) .col-xs-7" ).first();
			Element elSales = doc.select( "#sales strong" ).first();

			if ( null != elName ) {
				pageData.put( "name", elName.text() );

				if ( null != elPrice ) {
					price = elPrice.text().replaceAll( "\\$", "" );

					if ( null == price ) {
						price = "0";
					}

					pageData.put( "price", price );
				}

				if ( null != elSales ) {
					pageData.put( "sales", elSales.text() );
				}

				if ( null != elDateAdded ) {
					try {
						try {
							date = LocalDate.parse( elDateAdded.text(), formatterLong );

						} catch ( Exception e ) {
							date = LocalDate.parse( elDateAdded.text(), formatterShort );
						}

						pageData.put( "dateAdded", date.format( outputFormat ) );
 
					} catch ( Exception e ) {
						System.out.println( e );

						return pageData;
					}
				}

				if ( null != elDateModified ) {
					try {
						try {
							date = LocalDate.parse( elDateModified.text(), formatterLong );
							
						} catch ( Exception e ) {
							date = LocalDate.parse( elDateModified.text(), formatterShort );
						}

						pageData.put( "dateModified", date.format( outputFormat ) );

					} catch ( Exception e ) {
						System.out.println( e );

						return pageData;
					}
				}

				pageData.put( "id", String.valueOf( page ) );
			}
			
		} catch ( IOException e ) {
			page_falure++;
			failed_page_sequence++;
			// System.out.println( e );

		} finally {
			end = Instant.now();
			duration = Duration.between( start, end ).toMillis();
			average += duration;
			page_count++;
			average_time = Duration.between( program_start, end ).toMillis() / page_count;

			out = "\r" + String.format( "ID: %8d, Time: %6d, Average time: %6d, Successful pages: %8d, Failed pages: %8d, Total pages: %8d", page, duration,  average_time, page_success, page_falure, page_count );
			Log.info( out );

			// System.out.println( "" );
		}

		return pageData;
	}

	public void destruct() {
		if ( group.activeCount() > 1 ) return;

		Duration d = Duration.between( program_start, Instant.now() );
		Log.info( String.format( "%nTime elapsed: %02d:%02d:%02d%n", d.toHours(), d.toMinutes() - ( d.toHours() * 60 ), d.toSeconds() - ( d.toMinutes() * 60 ) ) );
	}
}