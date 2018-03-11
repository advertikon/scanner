/**
 * Class represents table row for commercial and free modules
 * Responsible for displaying modules added to chart and add/remove them
 */
package com.ua.advertikon.scanner;

import javafx.scene.control.*;
import com.ua.advertikon.helper.*;
import javafx.application.*;

public class CustomTableRow extends TableRow<DataRow> {
	private String table = "commercial";
	private stat target = null;

	CustomTableRow( String table, stat target ) {
		this.table = table;
		this.target = target;

		// Add/remove to chart
		setOnMouseClicked( ( e ) -> {
			if ( getItem() == null ) return; // skip empty rows
			String mod_id = ( String.valueOf( getItem().getId() ) );

			if ( isChart() ) {
				if ( target.db.deleteModuleChart( mod_id, table ) ) {
					unmark();
					target.removeSeriesFromChart( mod_id, table.equals( "free" ) ? target.freeChart : target.commercialChart );
				}

			} else {
				if ( target.db.addModuleChart( mod_id, table ) ) {
					mark();
					target.addSeriesToChart( mod_id, table.equals( "free" ) ? target.freeChart : target.commercialChart );
				}
			}
		} );
	}

	// Check added
	// public boolean checkInChart() {
	// 	boolean found = false;

	// 	if ( getItem() != null ) {
	// 		if ( db.isModuleInChart( String.valueOf( getItem().getId() ), table ) ) {
	// 			// getStyleClass().add( "row-selected" );
	// 			// setStyle( "-fx-background-color: red" );
	// 			found = true;
	// 			// getItem().setChart( "*" );
	// 			mark();
	// 		}
	// 	}

	// 	if ( !found ) {
	// 		// getStyleClass().remove( "row-selected" );
	// 		// setStyle( "" );
	// 	}

	// 	return found;
	// }

	public boolean isChart() {
		return getItem() != null && !getItem().getChart().equals( "" );
	}

	protected void mark() {
		getItem().setChart( true );
		getTableView().refresh();
	}

	protected void unmark() {
		getItem().setChart( false );
		getTableView().refresh();
	}
}