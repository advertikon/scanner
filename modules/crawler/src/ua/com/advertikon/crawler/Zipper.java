/*
 * Class that ZIP's package
 */
package ua.com.advertikon.crawler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Advertikon
 */
public class Zipper {
	protected Path mBase;
	protected Path mSource;
	protected IOException mError;
	
	public Zipper( Path source, Path base ) {
		mBase = base;
		mSource = source;
	}
	
	/**
	 * Saves archive under specific name
	 * @param target Name to be given to the archive
	 * @throws IOException
	 */
	public void save( String target ) throws IOException, Exception {
		mError = null;
		Profiler.record( "ZIP" );
		Files.createDirectories( Paths.get( target ).getParent() );

		try (ZipOutputStream zip = new ZipOutputStream( new FileOutputStream( target ) )) {
			Files.walk( mSource ).filter( path -> Files.isRegularFile( path ) ).forEach( path -> {
				try {
					zip.putNextEntry( new ZipEntry( mBase.relativize( path ).toString() ) );
					zip.write( Files.readAllBytes( path ) );
					zip.closeEntry();
					
				} catch (IOException ex) {
					Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex );
					mError = ex;
				}
			} );
		}

		Profiler.record( "ZIP" );
		
		if ( null != mError ) {
			throw mError;
		}
	}
	
	/**
	 * Adds CRC32 control sum to file name
	 * @param file
	 * @throws IOException 
	 */
	protected void crc( String file ) throws IOException {
		Path oldFileName = Paths.get( file );
		CRC32 crc = new CRC32();
		crc.update( Files.readAllBytes( oldFileName ) );
		Path newFileName = Paths.get( file.replace( "{crc}", String.valueOf( crc.getValue() ) ) );
		Files.move( oldFileName, newFileName );
	}
}