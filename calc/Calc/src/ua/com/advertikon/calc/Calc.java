package ua.com.advertikon.calc;

import java.io.BufferedReader;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Calc {
    public static void main(String[] args) throws Exception {
        String inputFile = null;

        if ( args.length > 0 ) {
			inputFile = args[ 0 ];
		}
		
        InputStream is = System.in;
		
        if ( inputFile != null ) {
			is = new FileInputStream( inputFile );
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String expr = br.readLine();

        while ( expr!= null ) {
			System.out.println( result( expr ) );
			
			expr = br.readLine();
        }
    }
	
	static public Double result( String expr ) {
		ANTLRInputStream input;
		CalculatorLexer lexer;
		CommonTokenStream tokens;
		CalculatorParser parser;
		ParseTree tree;
		MyVisitor eval = new MyVisitor();
		input = new ANTLRInputStream( expr + "\n" );
		lexer = new CalculatorLexer(input);
		tokens = new CommonTokenStream(lexer);
		parser = new CalculatorParser(tokens);
		tree = parser.stat(); // parse
		
		return eval.visit(tree);
	}
}