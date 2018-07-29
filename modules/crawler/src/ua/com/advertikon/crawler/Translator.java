/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    public void run() throws IOException, CrawlerException {
        Profiler.record( "Translation" );
        
        for( Path path: mFiles ) {
            if ( dotranslate( path ) ) {
                addTranslates( path );
            }
        }
		
		write();
		
        Profiler.record( "Translation" );
    }
    
    protected boolean dotranslate( Path path ) {
        int index = path.toString().lastIndexOf( "." );
        
        if ( -1 == index ) {
            return false;
        }
        
        return mTranslateExtensions.contains( path.toString().substring( index + 1 ) );
    }
    
    protected void addTranslates( Path path ) throws IOException {
        String content = new String( Files.readAllBytes( path ) );
        Pattern p = Pattern.compile( "__\\(\\s* (?<!\\\\)('|\")(.*?)(?<!\\\\)\\1", Pattern.COMMENTS );
        Matcher m = p.matcher( content );
        String name = path.toString();
 
        while( m.find() ) {
            if ( name.contains( "/admin/" ) ) {
                mAdminTranslate.add( m.group( 2 ) );

            } else if ( name.contains( "/catalog/" ) ) {
                mCatalogTranslate.add( m.group( 2 ) );
        
            } else {
                mCommonTranslate.add( m.group( 2 ) );
            }
        }
    }
	
	protected void write() throws CrawlerException, IOException {
		if ( !mCommonTranslate.isEmpty() ) {
			mAdminTranslate.addAll( mCommonTranslate );
			mCatalogTranslate.addAll( mCommonTranslate );
		}
		
		mAdminTranslate = unique( mAdminTranslate );
		mCatalogTranslate = unique( mCatalogTranslate );
		mAdminTranslate.sort( ( a , b ) -> a.compareTo( b ) );
		mCatalogTranslate.sort( ( a , b ) -> a.compareTo( b ) );
		
		if ( mAdminTranslate.isEmpty() ) {
			mAdminTranslate.add( "heading_title" );
		}
		
		StringBuilder content;
		String commontPart = guesPath();
		OpenOption[] options = new OpenOption[] { WRITE, CREATE };

		Path adminPath = Paths.get( "admin/language/en-gb/", commontPart, mCode + ".php" );
		Path adminRealPath = Paths.get( Packager.TMP_DIR ).resolve( adminPath );
		content = getContents( mAdminTranslate );
		content.append( getMandatoryContent( adminPath ) );
		System.out.println( adminPath.getParent() );
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
	
	protected StringBuilder getMandatoryContent( Path path ) throws IOException {
		StringBuilder out = new StringBuilder();

		if ( !Files.exists( path ) ) {
			out.append( "$_['heading_title'] = 'My Extension'\n" );
			return out;
		}
		
		for( String line: Files.readAllLines( path ) ) {
			if ( line.contains( "$_['heading_title']" ) ) {
				out.append( line ).append( "\n" );
			}
		}
		
		return out;
	}
	
	protected StringBuilder getContents( ArrayList<String> transaltions ) {
		StringBuilder out = new StringBuilder();
		out.append( "<?php\n" );
		transaltions.stream().forEach( t -> out.append( String.format( "$_['%1$s'] = '%1$s'\n", t ) ) );
		
		return out;
	}
	
	protected ArrayList unique( ArrayList in ) {
		ArrayList out = new ArrayList();
		
		in.stream().filter( i -> ( !out.contains( i ) ) ).forEachOrdered( i -> {
			out.add( i );
		} );
		
		return out;
	}
    
}
