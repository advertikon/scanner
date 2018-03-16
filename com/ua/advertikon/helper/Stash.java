package com.ua.advertikon.helper;

import java.io.*;
import com.ua.advertikon.helper.*;

public class Stash {

	static public boolean serialize( Object obj, String file ) {
		try {
			FileOutputStream fos = new FileOutputStream( file );
			ObjectOutputStream oos = new ObjectOutputStream( fos );
			oos.writeObject( obj );
			oos.close();
			fos.close();

		} catch( IOException e ) {
			Log.error( e );

			return false;
		}

		return true;
	}

	static public Object unserialize( String file ) {
		Object ret = null;

		try {
			FileInputStream fis = new FileInputStream( file );
			ObjectInputStream ois = new ObjectInputStream( fis );
			ret = ois.readObject();
			ois.close();
			fis.close();

		} catch( IOException e ) {
			Log.error( e );

		} catch( ClassNotFoundException e ) {
			Log.error( e );
		}

		return ret;
	}
}