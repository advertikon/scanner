/**
 * Class represents table row for commercial and free modules
 * Responsible for displaying modules added to chart and add/remove them
 */
package ua.com.advertikon.console;

import javafx.scene.control.*;

public class InstallationTableRow extends TableRow<InstallationRow> {
	private Console target = null;

	InstallationTableRow( Console target ) {
		this.target = target;

		// Add/remove to chart
		setOnMouseClicked( ( e ) -> {
			InstallationRow data = getItem();

			if ( data == null ) return; // skip empty rows


			String url = "http://" + data.getName() + "/index.php?route=" + ( data.getOcVersion().compareTo( "2.3.0.0" ) >= 0 ? "extension/" : "" ) + ( data.getCode().equals( "adk_mail") ? "module/" : "payment/" ) + data.getCode() + "/log";
			target.connect( url );

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