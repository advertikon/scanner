/*
 * Package files collector
 * Collects files depends on restrains
 */
package ua.com.advertikon.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	
	protected ArrayList<Path> mFiles;
	
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
	
	/**
	 * initializes class
	 */
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
	
    /**
     * Collects package files into {@link #mFiles}
     * @return List of files
     * @throws java.io.IOException on error
     */
	public ArrayList<Path> get() throws IOException {
        Profiler.record( "Collect files" );
		init();
		ArrayList<Path> out = new ArrayList<>();
		Files.walk( Paths.get( "." ) ).filter( path -> checkPath( path ) ).forEach( path -> out.add( path ) );
		mFiles = out;
        
        addSources();
        Profiler.record( "Collect files" );
		
		return out;
	}
	
    /**
	 * Checks file against set of restrains
	 * @param path Target file
	 * @return 
	 */
	protected boolean checkPath( Path path ) {
		Boolean result;

		if ( !Files.isRegularFile( path , LinkOption.NOFOLLOW_LINKS ) || !inPermittedFolder( path ) ) {
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
	
	/**
	 * Checks if target folder in the hardcoded list of permitted folders
	 * @param path Target folder
	 * @return 
	 */
	protected boolean inPermittedFolder( Path path ) {
		String fileName = path.toString();

		return permittedFolders.stream().anyMatch( restrain -> fileName.contains( restrain ) );
	}
	
	/**
	 * Checks file against set of file restrains
	 * @param path Target file
	 * @return 
	 */
	protected Boolean checkFile( Path path ) {
		String fileName = path.resolve( "." ).normalize().toString();

		if ( inclFiles.stream().anyMatch( restrain -> fileName.equals( restrain ) ) ) {
			return true;
		}
		
		if ( exclFiles.stream().anyMatch( restrain -> fileName.equals( restrain ) ) ) {
			return false;
		}
		
		return null;
	}

	/**
	 * Checks file against list of folder restrains
	 * @param path Target file
	 * @return 
	 */
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
	
	/**
	 * Checks file against list of regex restrains
	 * @param path Target file
	 * @return 
	 */
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
    
	/**
	 * Adds @source files to list of {@link #mFiles}
	 * @throws java.io.IOException
	 */
    protected void addSources() throws IOException {
        if ( null == mFiles ) {
            return;
        }
        
        ArrayList<Path> tmp = new ArrayList<>();
        
        for( Path path: mFiles ) {
            if ( !path.toString().contains( "/controller/" ) ) {
                continue;
            }
            
            tmp.addAll( fetchSources( path ) );
        }
        
        tmp.stream().filter( path -> !mFiles.contains( path ) ).forEach( path -> mFiles.add( path ) );
    }
    
	/**
	 * Fetches @source's from specific file
	 * @param path Target file
	 * @return 
	 * @throws java.io.IOException
	 */
    protected ArrayList<Path> fetchSources( Path path ) throws IOException {
        Pattern p = Pattern.compile( ".*@source\\s+(\\S+)\\s*$" );
        ArrayList<Path> out = new ArrayList<>();

        for( String line: Files.readAllLines( path ) ) {
            // Read only header
            if ( line.contains( "*/" ) ) {
                break;
            }

            Matcher m = p.matcher( line );
            
            if ( m.matches() ) {
                // Recursion required
                if ( m.group( 1 ).endsWith( "/*" ) ) {
                    out.addAll( fetchRecursive( Paths.get( m.group( 1 ) ).getParent() ) );

                } else {
                    out.add( Paths.get( m.group( 1 ) ) );
                }
            }
        }
        
        return out;
    }
    
	/**
	 * Gets files for @source with wildcard (path/*)
	 * @param path Target folder
	 * @return
	 * @throws IOException 
	 */
    protected ArrayList<Path> fetchRecursive( Path path ) throws IOException {
		ArrayList<Path> out = new ArrayList<>();
		Files.walk( path )
                .filter( p -> Files.isRegularFile( p , LinkOption.NOFOLLOW_LINKS ) )
                .forEach( p -> out.add( p ) );
 
		return out;
    }
	
	/**
	 * Dumps {@link #mFiles} into STDOUT
	 */
	public void dumpFiles() {
		if ( null == mFiles ) {
			System.out.println( "Files list is empty" );
		}
		
		mFiles.stream().forEach( System.out::println );
	}
}
