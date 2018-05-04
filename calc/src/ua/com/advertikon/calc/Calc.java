/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.calc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mariuszgromada.math.mxparser.Expression;

/**
 *
 * @author max
 */
public class Calc {
	public static char ROOT_SQUARE = '\u221a';
	public static char ROOT_QUBIC  = '\u221b';
	public static char ROOT_QUADRO = '\u221c';

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Calc c = new Calc();
		
		String in = "2+\u221a25 + \u221b8 + \u221c16 + -1 * 1";
		String inter = c.fixRaw( in );
		String out = c.fix( inter );
		Expression e = new Expression( out );
		double result = e.calculate();
		System.out.println( in );
		System.out.println( inter );
		System.out.println( out );
		System.out.println( result );
		
//		showUnicode();
	}
	
	public String fixRaw( String s ) {
		s = fixNegation( s );

		return s;
	}
	
	public String fix( String s ) {
		s = fixRoot( s );

		return s;
	}
	
	private String fixRoot( String s ) {
		s = s.replaceAll( Calc.ROOT_SQUARE + "(\\d+)", "root(2,$1)" );
		s = s.replaceAll( Calc.ROOT_QUBIC  + "(\\d+)", "root(3,$1)" );
		s = s.replaceAll( Calc.ROOT_QUADRO + "(\\d+)", "root(4,$1)" );

		return s;
	}
	
	private String fixNegation( String s ) {
		return s.replaceAll( "\\+(\\s*)-(\\s*)(\\d+)", "+$1(-$3)" );
	}
	
	private static void showUnicode() {
		char start = '\u2200';
		char stop  = '\u2300';
		
		while( start++ < stop ) {
			System.out.println( String.format( "%c - 0x%h", start, (int)start ) );
		}
	}
	
}
