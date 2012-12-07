package com.wico.parselog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {
	
	private static int NOTFOUND_NOTIF_STATUS = 0;
	private static int FOUND_NOTIF_STATUS = 1;
	private static int PARTIALFOUND_NOTIF_STATUS = 2;
	
	//private static String URL_NOTIF = "'http://127.0.0.1:8080/br/spring/notif'";
	// dummy in stage
	private static String URL_NOTIF = "'http://64.151.122.73:12010/br/spring/wap'";
	
	private static Pattern headXML = Pattern.compile("<\\?xml\\s+version=\"1\\.0\"");
	private static Pattern initXML = Pattern.compile("<\\s*subscription_request\\s+operation\\s*=\\s*\"notification\"\\s*>");
	private static Pattern endXML = Pattern.compile("</\\s*subscription_request\\s*>");
	
	//private static Pattern SUBSCRIBE_ACTION = Pattern.compile("action\\s*=\\\"SUBSCRIBE\\\"");
	//private static Pattern UNSUBSCRIBE_ACTION = Pattern.compile("action\\s*=\\\"UNSUBSCRIBE\\\"");
	
	
	private static void parselog(String filename, String logFilename, String actionName, String sleepTime) throws IOException {
		FileReader fr;
		BufferedReader br;
		String line;
		String line2;
		Matcher m;
		int foundStatus;
		StringBuilder sb;
		Pattern patternAction;
		
		sb =  new StringBuilder(2048);
		fr = new FileReader(filename);
		br = new BufferedReader(fr);
		
		patternAction = Pattern.compile("action\\s*=\\\""+ actionName +"\\\"");

		
		// searching an xml
		while( (line = br.readLine()) != null ) {
			
			m = headXML.matcher(line);
			
			if( m.find() ) {
				
				// it is an xml
				
				line2 = br.readLine();
				
				// clean and verify if it is a subscription_request operation="notification"
				sb.setLength(0);
				
				if( (foundStatus = tryToReadSubscriptionRequestNotificationByAction(line2, br, sb, patternAction)) != NOTFOUND_NOTIF_STATUS ) {
					sb.insert(0, line);
					sb.insert(line.length(), '\n');
					// do something with the notification xml
					
					
					
					System.out.println("# ------------------------------------------");
					System.out.print("curl --header 'Content-Type:application/xml' -d '");
					System.out.print(sb.toString());
					System.out.print("' " + URL_NOTIF + " >> " + logFilename);
					
					System.out.print("\n\nsleep " + sleepTime + "\n\n");
					continue;
					
				} else {
					// it is not a notification
					continue;
				}
				
				
			} else {
				// it is not an xml
				continue;
			}
			
		}
		
		br.close();
		
	}
	
	private static int tryToReadSubscriptionRequestNotificationByAction(String line, BufferedReader br, StringBuilder sb, Pattern patternAction) throws IOException {
		
		String line2;
		Matcher m;
		boolean containsActionIndicator;
		
		containsActionIndicator = false;
		
		m = initXML.matcher(line);
		
		if( m.find() ) {
			// it is a notification
			
			// append and try to find the end of the xml
			sb.append(line);
			sb.append('\n');
			
			while( (line2=br.readLine()) != null ) {
			
				m = endXML.matcher(line2);
				if( m.find() ) {
					
					// the end of the xml fragment
					
					sb.append(line2);
					sb.append('\n');
					
					return ( (containsActionIndicator) ?  FOUND_NOTIF_STATUS : NOTFOUND_NOTIF_STATUS );
					
				} else {
					// a common xml line
					
					// ask if contains desired action
					m = patternAction.matcher(line2);
					
					if( m.find() ) {
						// a notification with the desired action
						containsActionIndicator = true;
					}
					
					sb.append(line2);
					sb.append('\n');
					
				}
			}
			
			return ( (containsActionIndicator) ?  PARTIALFOUND_NOTIF_STATUS : NOTFOUND_NOTIF_STATUS );
			
			
		} else {
			return NOTFOUND_NOTIF_STATUS;
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
			
			
			switch(args.length) {
			 
			
			
			default:
				
			case 3:
				sleepTime = args[2];
				
			case 2:
				actionName = args[1];
				
			case 1:
				
				filename = args[0];
				break;
				
			case 0:
				System.out.println("ParseLogSpringNotifResource <log_filename> [<action_name>] [<sleep_time>]");
				return;
			}
			
			
			logFilename = actionName + "_" + filename.substring( filename.lastIndexOf('\\') + 1  ) + "_log.log";
			
			System.out.println("#!/bin/bash\n");
			
			System.out.println("# ====================================");
			System.out.println("# - filename: " + filename);
			System.out.println("# - logFilename: " + logFilename);
			System.out.println("# - actionName: " + actionName);
			System.out.println("# - sleepTime: " + sleepTime);
			System.out.println();
			
			parselog(filename, logFilename, actionName, sleepTime);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
