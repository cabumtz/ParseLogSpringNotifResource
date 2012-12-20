package com.wico.parselog;

import java.util.List;

public class ParseGeneratorMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filename;
		String actionName = "[^\"]*";
		String sleepTime = "5";
		String logFilename;
		List<XmlParsed> messageList;

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

			// System.out.println("#!/bin/bash\n");

			System.out.println("# ====================================");
			System.out.println("# - filename: " + filename);
			System.out.println("# - logFilename: " + logFilename);
			System.out.println("# - actionName: " + actionName);
			System.out.println("# - sleepTime: " + sleepTime);
			System.out.println();

			messageList = ParseGenerator.generateParsedStructures(filename, actionName);

			for (XmlParsed elem : messageList) {
				System.out.println("# --------------------------------------");
				System.out.println(elem.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
