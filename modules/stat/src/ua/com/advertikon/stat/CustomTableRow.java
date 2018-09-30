/**
 * Class represents table row for commercial and free modules
 * Responsible for displaying modules added to chart and add/remove them
 */
package ua.com.advertikon.stat;

import ua.com.advertikon.stat.DataRow;
import javafx.scene.control.*;

public class CustomTableRow extends TableRow<DataRow> {
	private String table = "commercial";
	private Stat target = null;

	CustomTableRow( String table, Stat target ) {
		this.table = table;
		this.target = target;

		// Add/remove to chart
		setOnMouseClicked( ( e ) -> {
			if ( getItem() == null ) {
				return; // skip empty rows
			}
			String mod_id = ( String.valueOf( getItem().getId() ) );
			String mod_name = getItem().getName();

			if ( isChart() ) {
				if ( target.db.deleteModuleChart( mod_id, table ) ) {
					unmark();
					target.removeSeriesFromChart( mod_id, table.equals( "free" ) ? target.freeChart : target.commercialChart );
				}

			} else {
				if ( target.db.addModuleChart( mod_id, table ) ) {
					mark();
					target.addSeriesToChart( mod_id, mod_name,  table.equals( "free" ) ? target.freeChart : target.commercialChart );
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