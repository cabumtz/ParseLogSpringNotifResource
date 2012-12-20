package com.wico.parselog;

import java.io.IOException;
import java.util.List;

public class CurlGenerator {

	// private static String URL_NOTIF = "'http://127.0.0.1:8080/br/spring/notif'";
	// dummy in stage
	private static String URL_NOTIF = "'http://64.151.122.73:12010/br/spring/wap'";

	private static void generateCurl(String filename, String logFilename,
			String actionName, String sleepTime) throws IOException {
		
		int j = 0;
		List<ParseResult> items = LogParser.parselog(filename, actionName);

		for (ParseResult pr : items) {

			j++;
			System.out.println(String.format("# %4d ------------------------------------------", j) );
			
			System.out
					.print("curl --header 'Content-Type:application/xml' -d '");
			System.out.print(pr.getBody());
			System.out.print("' " + URL_NOTIF + " >> " + logFilename);

			System.out.print("\n\nsleep " + sleepTime + "\n\n");

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filename;
		String actionName = "SUBSCRIBE";
		String sleepTime = "5";
		String logFilename;

		try {

			switch (args.length) {

			default:

			case 3:
				sleepTime = args[2];

			case 2:
				actionName = args[1];

			case 1:

				filename = args[0];
				break;

			case 0:
				System.out
						.println("ParseLogSpringNotifResource <log_filename> [<action_name>] [<sleep_time>]");
				return;
			}

			logFilename = actionName + "_"
					+ filename.substring(filename.lastIndexOf('\\') + 1)
					+ "_log.log";

			System.out.println("#!/bin/bash\n");

			System.out.println("# ====================================");
			System.out.println("# - filename: " + filename);
			System.out.println("# - logFilename: " + logFilename);
			System.out.println("# - actionName: " + actionName);
			System.out.println("# - sleepTime: " + sleepTime);
			System.out.println();

			generateCurl(filename, logFilename, actionName, sleepTime);


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
