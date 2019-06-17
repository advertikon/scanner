package ua.com.advertikon.calc;

import java.util.HashMap;
import java.util.Map;
import ua.com.advertikon.calc.CalculatorParser.ExprContext;

public class MyVisitor extends CalculatorBaseVisitor<Double> {
    /** "memory" for our calculator; variable/value pairs go here */
    Map<String, Double> memory = new HashMap<>();
	boolean isDegree = true;
	
	public MyVisitor( boolean degree ) {
		isDegree = degree;
	}

    /** ID '=' expr NEWLINE
	 * @param ctx
	 * @return  */
    @Override
    public Double visitAssign(CalculatorParser.AssignContext ctx) {
        String id = ctx.ID().getText();  // id is left-hand side of '='
        double value = visit(ctx.expr());   // compute value of expression on right
        memory.put(id, value);           // store it in our memory
        return value;
    }

    /** expr NEWLINE
	 * @param ctx
	 * @return  */
    @Override
    public Double visitPrintExpr(CalculatorParser.PrintExprContext ctx) {
		ExprContext expr = ctx.expr();
		
		if ( null != expr ) {
			return visit( ctx.expr() );
		}
  
        return 0D;
    }

    /** INT
	 * @param ctx
	 * @return  */
    @Override
    public Double visitInt(CalculatorParser.IntContext ctx) {
        return Double.valueOf( ctx.INT().getText() );
    }

    /** ID
	 * @param ctx
	 * @return  */
    @Override
    public Double visitId(CalculatorParser.IdContext ctx) {
        String id = ctx.ID().getText();
        if ( memory.containsKey(id) ) {
			return memory.get(id);
		}
        return 0.0;
    }

    /** expr op=('*'|'/') expr
	 * @param ctx
	 * @return  */
    @Override
    public Double visitMulDiv(CalculatorParser.MulDivContext ctx) {
        Double left = visit( ctx.expr( 0 ) );
        Double right = visit( ctx.expr( 1 ) );
		
		if ( null == left || null == right ) {
			return 0D;
		}
		
        return ctx.op.getType() == CalculatorParser.MUL ? left * right : left / right;
    }

    /** expr op=('+'|'-') expr
	 * @param ctx
	 * @return  */
    @Override
    public Double visitAddSub(CalculatorParser.AddSubContext ctx) {
        Double left = visit( ctx.expr( 0 ) );
        Double right = visit( ctx.expr( 1 ) );
		
		if ( null == left || null == right ) {
			return 0D;
		}
		
        return ctx.op.getType() == CalculatorParser.ADD ? left + right : left - right;
    }

    /** '(' expr ')'
	 * @param ctx
	 * @return  */
    @Override
    public Double visitParens(CalculatorParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }

	@Override
	/** expr ( PERSENT | POW | ROOT ) expr */
	public Double visitPriority1(CalculatorParser.Priority1Context ctx) {
		Double left = visit( ctx.expr( 0 ) );
		Double right = visit( ctx.expr( 1 ) );
		
		if ( null == left || null == right ) {
			return 0D;
		}
		
		switch( ctx.op.getType() ) {
			case CalculatorParser.PERSENT:
				return left * ( right / 100 );
			case CalculatorParser.POW:
				return Math.pow( left, right );
			case CalculatorParser.ROOT:
				return root( left, right );
		}
		
		System.err.println( "Ubsupported operation" );
		
		return 0D;
	}
	
	protected double root( double a, double b ) {
//		return Math.pow( Math.E, Math.log( a )/ b );
		return Math.pow( a, ( 1 / b ) );
	}

	@Override
	/** ( SIN | ASIN | COS | ACOS | TAN | ATAN | LN | LOG | SQRT ) expr */
	public Double visitUnaryRight(CalculatorParser.UnaryRightContext ctx) {
		Double expr = visit( ctx.expr() );
		
		if ( null == expr ) {
			return 0D;
		}
		
		double e = isDegree ? ( expr * Math.PI ) / 180 : expr; // to radians
		
		switch( ctx.op.getType() ) {
			case CalculatorParser.SIN:
				return Math.sin( e );
			case CalculatorParser.ASIN:
				return Math.asin( e );
			case CalculatorParser.COS:
				return Math.cos( e );
			case CalculatorLexer.ACOS:
				return Math.acos( e );
			case CalculatorLexer.TAN:
				return Math.tan( e );
			case CalculatorLexer.ATAN:
				return Math.atan( e );
			case CalculatorLexer.LN:
				return Math.log( expr );
			case CalculatorLexer.LOG:
				return Math.log10( expr );
			case CalculatorLexer.SQRT:
				return Math.sqrt( expr );
			case CalculatorLexer.ROOT3:
				return root( expr, 3 );
			case CalculatorLexer.ROOT4:
				return root( expr, 4 );
		}
		
		System.err.println( "Ubsupported operation" );
		
		return 0.0;
	}

	@Override
	public Double visitFactorial(CalculatorParser.FactorialContext ctx) {
		Double expr = visit( ctx.expr() );
		Double res = 1D;
		
		if ( null == expr ) {
			return 1D;
		}
		
		for( int i = 1; i <= expr; i++ ) {
			res *= i;
		}
		
		return res;
	}

	@Override
	public Double visitE(CalculatorParser.EContext ctx) {
		return Math.E;
	}

	@Override
	public Double visitPi(CalculatorParser.PiContext ctx) {
		return Math.PI;
	}

	@Override
	/** FLOAT */
	public Double visitFloat(CalculatorParser.FloatContext ctx) {
		return Double.valueOf( ctx.FLOAT().getText() );
	}
}