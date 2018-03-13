/**
 * Class represents table row for commercial and free modules
 * Responsible for displaying modules added to chart and add/remove them
 */
package com.ua.advertikon.console;

import javafx.scene.control.*;
import com.ua.advertikon.helper.*;
import javafx.application.*;

public class InstallationTableRow extends TableRow<InstallationRow> {
	private Application target = null;

	InstallationTableRow( Application target ) {
		this.target = target;

		// Add/remove to chart
		setOnMouseClicked( ( e ) -> {
			if ( getItem() == null ) return; // skip empty rows
			String mod_id = ( String.valueOf( getItem().getId() ) );

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

	// public boolean isChart() {
	// 	return getItem() != null && !getItem().getChart().equals( "" );
	// }

	// protected void mark() {
	// 	getItem().setChart( true );
	// 	getTableView().refresh();
	// }

	// protected void unmark() {
	// 	getItem().setChart( false );
	// 	getTableView().refresh();
	// }
}