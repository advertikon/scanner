package ua.com.advertikon.scanner;

// import com.ua.advertikon.scanner.*;

import java.io.*;
import java.util.*;

class thread extends Thread {
	private Scanner target = null;

	thread( String name ) {
		super( name );
	}

	thread( ThreadGroup group, String name ) {
		super( group, name );
	}

	@Override
	public void run() {
		int page = -1;

		try {
			while( -1 != ( page = target.pick() ) ) {
				HashMap<String, String> data = target.connect( page );
				target.saveData( data );
			}
			
		} catch ( IOException e ) {
			System.out.println( e );

			return;
		}

		target.destruct();
	}

	public void go( Scanner target ) {
		this.target = target;
		start();
	}
}