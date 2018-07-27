/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TextArea;

/**
 *
 * @author max
 */
public class Collector {
	protected ArrayList<String> inclFiles   = new ArrayList<>();
	protected ArrayList<String> exclFiles   = new ArrayList<>();
	protected ArrayList<String> inclFolders = new ArrayList<>();
	protected ArrayList<String> exclFolders = new ArrayList<>();
	protected ArrayList<String> inclRegex   = new ArrayList<>();
	protected ArrayList<String> exclRegex   = new ArrayList<>();
	
	protected TextArea mInclFiles;
	protected TextArea mExclFiles;
	protected TextArea mInclFolders;
	protected TextArea mExclFolders;
	protected TextArea mInclRegex;
	protected TextArea mExclRegex;
	
	protected ArrayList<Path> files;
	
	protected ArrayList<String> permittedFolders;

	public Collector( TextArea inclFiles, TextArea exclFiles, TextArea inclFolders,
			TextArea exclFolders, TextArea inclRegex, TextArea exclRegex ) {
	
		mInclFiles = inclFiles;
		mExclFiles = exclFiles;
		mInclFolders = inclFolders;
		mExclFolders = exclFolders;
		mInclRegex = inclRegex;
		mExclRegex = exclRegex;
		
		permittedFolders = new ArrayList<>( Arrays.asList( "./admin/", "./catalog/", "./image/", "./system/" ) );
	}
	
	protected void init() {
		inclFiles.clear();
		exclFiles.clear();
		inclFolders.clear();
		exclFolders.clear();
		inclRegex.clear();
		exclRegex.clear();

		for( String line: mInclFiles.getText().split( "\\n" ) ) {
			if ( !line.isEmpty() ) {
				inclFiles.add( line );
			}
		}

		for( String line: mExclFiles.getText().split( "\\n" ) ) {
			if ( !line.isEmpty() ) {
				exclFiles.add( line );
			}
		}

		for( String line: mInclFolders.getText().split( "\\n" ) ) {
			if ( !line.isEmpty() ) {
				inclFolders.add( line );
			}
		}
		
		for( String line: mExclFolders.getText().split( "\\n" ) ) {
			if ( !line.isEmpty() ) {
				exclFolders.add( line );
			}
		}
		
		exclFolders.add( "./system/storage" );

		for( String line: mInclRegex.getText().split( "\\n" ) ) {
			if ( !line.isEmpty() ) {
				inclRegex.add( line );
			}
		}

		for( String line: mExclRegex.getText().split( "\\n" ) ) {
			if ( !line.isEmpty() ) {
				exclRegex.add( line );
			}
		}
	}
	
	public ArrayList<Path> get() throws IOException {
		init();
		ArrayList<Path> out = new ArrayList<>();
		Files.walk( Paths.get( "." ) ).filter( path -> checkPath( path ) ).forEach( path -> out.add( path ) );
		files = out;
		
		return out;
	}
	
	protected boolean checkPath( Path path ) {
		Boolean result;

		if ( !inPermittedFolder( path ) ) {
			return false;
		}
		
		result = checkFile( path );
		
		if ( null != result ) {
			return result;
		}
		
		result = checkFolder( path );
		
		if ( null != result ) {
			return result;
		}
		
		result = checkRegex( path );
		
		if ( null != result ) {
			return result;
		}
		
		return inclFiles.isEmpty() && inclFolders.isEmpty() && inclRegex.isEmpty();
	}
	
	protected boolean inPermittedFolder( Path path ) {
		String fileName = path.toString();

		return permittedFolders.stream().anyMatch( restrain -> fileName.contains( restrain ) );
	}
	
	protected Boolean checkFile( Path path ) {
		String fileName = path.getFileName().toString();

		if ( inclFiles.stream().anyMatch( (restrain) -> ( fileName.equals( restrain ) ) ) ) {
			return true;
		}
		
		if ( exclFiles.stream().anyMatch( (restrain) -> ( fileName.equals( restrain ) ) ) ) {
			return false;
		}
		
		return null;
	}

	protected Boolean checkFolder( Path path ) {
		int yesLen = 0;
		int noLen = 0;
		String folderName = path.toString();
		
		for( String folder: inclFolders ) {
			if ( folderName.contains(folder) ) {
				yesLen = Math.max( yesLen, folder.length() );
			}
		}
		
		for( String folder: exclFolders ) {
			if ( folderName.contains(folder) ) {
				noLen= Math.max( noLen, folder.length() );
			}
		}
		
		if ( yesLen == noLen ) {
			return null;
		}
		
		return yesLen > noLen;
	}

	protected Boolean checkRegex( Path path ) {
		String fileName = path.toString();

		if ( inclRegex.stream().anyMatch( restrain -> fileName.matches( restrain ) ) ) {
			return true;
		}
		
		if ( exclRegex.stream().anyMatch( restrain -> fileName.matches( restrain ) ) ) {
			return false;
		}
		
		return null;
	}
	
	public void dumpFiles() {
		if ( null == files ) {
			System.out.println( "Files list is empty" );
		}
		
		files.stream().forEach( System.out::println );
	}
}
