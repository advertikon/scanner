package com.ua.advertikon.console;

import com.ua.advertikon.helper.*;

import java.time.*;
import java.util.*;

public class ConsoleModel {
	final String URL = "https://shop.advertikon.com.ua/support/ticket_button.php";

	public List<Map<String, String>> getInstallation() {
		List<Map<String, String>> ret = null;
		String data = new AUrl().get( URL + "?installation=true" );

		$line['id'],
				$line['name'],
				$line['code'],
				$line['version'],
				$line['oc_version'],
				$line['date_created'],
				$line['date_modified'],
				$line['country'],
				$line['fraud'],
				$line['active'],
				$line['localhost']

		return ret;
	}
}