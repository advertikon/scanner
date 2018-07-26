import org.mariuszgromada.math.mxparser.*;

public class Calc {

	public static void main( String[] args ) {
		new Calc().run();
	}

	public void run() {
		Expression e = new Expression( "2+2" );
		System.out.println( e.calculate() );
	}

	
}