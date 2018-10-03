/*
 * Class that handles translation stuff
 */
package ua.com.advertikon.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Advertikon
 */
public class Translator {
    protected ArrayList<Path> mFiles;
    protected ArrayList<String> mTranslateExtensions;
    protected ArrayList<String> mAdminTranslate;
    protected ArrayList<String> mCatalogTranslate;
    protected ArrayList<String> mCommonTranslate;
	protected String mCode;
	
	protected final int ADMIN   = 0;
	protected final int CATALOG = 1;
	
	protected final String CAPTION_KEY = "caption_";
    
    public Translator( ArrayList files, String code ) {
        mFiles = files;
		mCode = code;
    
        mTranslateExtensions = new ArrayList<>();
        mTranslateExtensions.add( "php" );
        mTranslateExtensions.add( "twig" );
        mTranslateExtensions.add( "tpl" );
        
        mAdminTranslate = new ArrayList<>();
        mCatalogTranslate = new ArrayList<>();
        mCommonTranslate = new ArrayList<>();
    }
    
	/**
	 * Main method
	 * @throws IOException
	 * @throws CrawlerException 
	 */
    public void run() throws IOException, CrawlerException {
        Profiler.record( "Translation" );
        
        for( Path path: mFiles ) {
            if ( doTranslate( path ) ) {
                addTranslates( path );
            }
        }
		
		write();
		
       Profiler.record( "Translation" );
    }
    
	/**
	 * Checks if file needs to be translated
	 * @param path Target file
	 * @return 
	 */
    protected boolean doTranslate( Path path ) {
        int index = path.toString().lastIndexOf( "." );
        
        if ( -1 == index ) {
            return false;
        }
        
        return mTranslateExtensions.contains( path.toString().substring( index + 1 ) );
    }
    
	/**
	 * Populate translation structures with translations from file
	 * @param path Target file
	 * @throws IOException 
	 */
    protected void addTranslates( Path path ) throws IOException {
        String content = new String( Files.readAllBytes( path ) );
        Pattern p = Pattern.compile( "__\\(\\s* (?<!\\\\)('|\")(.*?)(?<!\\\\)\\1", Pattern.COMMENTS );
        Matcher m = p.matcher( content );
        String name = path.toString();
 
        while( m.find() ) {
			if ( m.group( 1 ).isEmpty() ) {
				continue;
			}
			
			if ( m.group( 2 ).startsWith( CAPTION_KEY ) ) {
				continue; // do not include caption keys - it will add additional transaltion line
			}

            if ( name.contains( "/admin/" ) ) {
                mAdminTranslate.add( m.group( 2 ) );

            } else if ( name.contains( "/catalog/" ) ) {
                mCatalogTranslate.add( m.group( 2 ) );
        
            } else {
                mCommonTranslate.add( m.group( 2 ) );
            }
        }
    }
	
	/**
	 * Writes translation files to disk
	 * @throws CrawlerException
	 * @throws IOException 
	 */
	protected void write() throws CrawlerException, IOException {
		if ( !mCommonTranslate.isEmpty() ) {
			mAdminTranslate.addAll( mCommonTranslate );
			mCatalogTranslate.addAll( mCommonTranslate );
		}
		
		mAdminTranslate = unique( mAdminTranslate );
		mCatalogTranslate = unique( mCatalogTranslate );
		mAdminTranslate.sort( ( a , b ) -> a.compareTo( b ) );
		mCatalogTranslate.sort( ( a , b ) -> a.compareTo( b ) );
		
		StringBuilder content;
		String commontPart = guesPath();
		OpenOption[] options = new OpenOption[] { WRITE, CREATE };

		Path adminPath = Paths.get( "admin/language/en-gb/", commontPart, mCode + ".php" );
		Path adminRealPath = Paths.get( Packager.TMP_DIR ).resolve( adminPath );
		content = getContents( mAdminTranslate );
		content.append( getMandatoryContent( adminPath ) );
		Files.createDirectories( adminRealPath.getParent() );
		Files.write( adminRealPath, content.toString().getBytes() );
	 
		if ( !mCatalogTranslate.isEmpty() ) {
			Path catalogPath = Paths.get( "catalog/language/en-gb/", commontPart, mCode + ".php" );
			Path catalogRealPath = Paths.get( Packager.TMP_DIR ).resolve( catalogPath );
			content = getContents( mCatalogTranslate );
			content.append( getMandatoryContent( catalogPath ) );
			Files.createDirectories( catalogRealPath.getParent() );
			Files.write( catalogRealPath, content.toString().getBytes(), options );
		}
	}
	
	/**
	 * Tries to guess path part between <code>controller</code> and file name
	 * @return
	 * @throws CrawlerException 
	 */
	protected String guesPath() throws CrawlerException {
		String needle = "/admin/controller/";

		for( Path path: mFiles ) {
			int index = path.toString().indexOf( needle );

			if ( index < 0 ) {
				continue;
			}
			
			return path.getParent().toString().substring( index + needle.length() );
		}
		
		throw new CrawlerException( "Failed to gues language path" );
	}
	
	/**
	 * Returns list of translations that is mandatory for translation file
	 * @param path Target file
	 * @return
	 * @throws IOException 
	 */
	protected StringBuilder getMandatoryContent( Path path ) throws IOException {
		StringBuilder out = new StringBuilder();

		if ( !Files.exists( path ) ) {
			out.append( "$_['heading_title'] = 'My Extension';\n" );
			return out;
		}
		
		for( String line: Files.readAllLines( path ) ) {
			if ( line.contains( "$_['heading_title']" ) ) {
				out.append( line ).append( "\n" );
			}
			
			if ( line.contains( "$_['text_" + mCode + "']" ) ) {
				out.append( line ).append( "\n" );
			}
			
			if ( line.startsWith("$_['" + CAPTION_KEY ) ) {
				out.append( line ).append( "\n" );
			}
		}
		
		return out;
	}
	
	/**
	 * Returns translation file contents ready to be saved
	 * @param transaltions List of translations
	 * @return 
	 */
	protected StringBuilder getContents( ArrayList<String> transaltions ) {
		StringBuilder out = new StringBuilder();
		out.append( "<?php\n" );
		transaltions.stream().forEach( t -> out.append( String.format( "$_['%1$s'] = '%1$s';\n", t ) ) );
		
		return out;
	}
	
	/**
	 * Removed duplicate records from the list
	 * @param in Target list
	 * @return 
	 */
	protected ArrayList unique( ArrayList in ) {
		ArrayList out = new ArrayList();
		
		in.stream().filter( i -> ( !out.contains( i ) ) ).forEachOrdered( i -> {
			out.add( i );
		} );
		
		return out;
	}
}
