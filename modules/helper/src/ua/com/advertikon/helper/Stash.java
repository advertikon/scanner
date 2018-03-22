package ua.com.advertikon.helper;

import java.io.*;

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
}