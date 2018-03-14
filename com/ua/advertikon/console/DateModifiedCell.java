package com.ua.advertikon.console;

import com.ua.advertikon.helper.*;

import javafx.scene.control.*;
import java.time.*;
import java.time.format.*;
// import javafx.graphics.*;
import javafx.scene.paint.*;

public class DateModifiedCell extends TableCell<InstallationRow, String> {
	@Override
	protected void updateItem( String value, boolean isEmpty ) {
		super.updateItem( value, isEmpty );
		
		setText( null == value ? "" : value );

		if ( null != value ) {
			LocalDateTime date = LocalDateTime.parse( value, DateTimeFormatter.ofPattern( "yyy-MM-dd H:m:s" ) );

			Color color = Color.RED;

			if ( LocalDateTime.now().minusDays( 1 ).compareTo( date ) <= 0 ) {
				color = Color.GREEN;

			} else if ( LocalDateTime.now().minusDays( 7 ).compareTo( date ) <= 0 ) {
				color = Color.BLUE;

			} else if ( LocalDateTime.now().minusDays( 14 ).compareTo( date ) <= 0 ) {
				color = Color.MAROON;
			}
			
			setTextFill( isSelected() ? Color.WHITE : color );
		}
	}
}