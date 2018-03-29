package ua.com.advertikon.helper;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Stash {

	static public boolean serialize( Object obj, String file ) {
		try {
			try ( FileOutputStream fos = new FileOutputStream( file ); ObjectOutputStream oos = new ObjectOutputStream( fos ) ) {
				oos.writeObject( obj );
			}

		} catch( IOException e ) {
			Log.error( e );

			return false;
		}

		return true;
	}

	static public Object unserialize( String file ) {
		Object ret = null;

		try {
			try ( FileInputStream fis = new FileInputStream( file ); ObjectInputStream ois = new ObjectInputStream( fis ) ) {
				ret = ois.readObject();
			}

		} catch( IOException | ClassNotFoundException e ) {
			Log.error( e );
		}

		return ret;
	}
	
	static public String fileGetContents( String fileName ) {
		StringBuilder in = new StringBuilder();
		int c;

		try ( BufferedReader reader = new BufferedReader( new FileReader( fileName ) ) ) {
			while( -1 != ( c = reader.read() ) ) {
				in.append( (char) c );
			}

		} catch (IOException ex) {
			Logger.getLogger(Stash.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return in.toString();
	}
	
	static public boolean filePutCOntents( String fileName, String content ) {
		try ( BufferedWriter writer = new BufferedWriter( new FileWriter( fileName ) ) ) {
			writer.write( content );

		} catch ( IOException ex ) {
			Logger.getLogger( Stash.class.getName() ).log( Level.SEVERE, null, ex );
			
			return false;
		}
		
		return true;
	}
}