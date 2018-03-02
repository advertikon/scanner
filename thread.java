import java.io.*;
import java.util.*;

class thread extends Thread {
	scanner target = null;

	thread( String name ) {
		super( name );
	}

	public void run() {
		int page = -1;
		target.thread_run++;

		try {
			while( -1 != ( page = target.pick() ) ) {
				HashMap<String, String> data = target.connect( page );
				target.saveData( data );
			}
			
		} catch ( IOException e ) {
			System.out.println( e );

			return;
		}

		target.thread_run--;

		if ( 0 == target.thread_run ) {
			target.destruct();
		}
	}

	public void go( scanner target ) {
		this.target = target;
		start();
	}
}