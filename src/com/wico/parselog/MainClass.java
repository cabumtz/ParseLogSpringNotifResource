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
	
	private static String URL_NOTIF = "\"http://127.0.0.1:8080/br/spring/notif\"";
	
	private static Pattern headXML = Pattern.compile("<\\?xml\\s+version=\"1\\.0\"");
	private static Pattern initXML = Pattern.compile("<\\s*subscription_request\\s+operation\\s*=\\s*\"notification\"\\s*>");
	private static Pattern endXML = Pattern.compile("</\\s*subscription_request\\s*>");
	private static Pattern UNSUBSCRIBE = Pattern.compile("UNSUBSCRIBE");
	
	private static void parselog(String filename) throws IOException {
		FileReader fr;
		BufferedReader br;
		String line;
		String line2;
		Matcher m;
		int foundStatus;
		StringBuilder sb;
		
		sb =  new StringBuilder(2048);
		fr = new FileReader(filename);
		br = new BufferedReader(fr);
		
		// searching an xml
		while( (line = br.readLine()) != null ) {
			
			m = headXML.matcher(line);
			
			if( m.find() ) {
				
				// it is an xml
				
				line2 = br.readLine();
				
				// clean and verify if it is a subscription_request operation="notification"
				sb.setLength(0);
				
				if( (foundStatus = tryToReadSubscriptionRequestNotification_SUBSCRIBE(line2, br, sb)) != NOTFOUND_NOTIF_STATUS ) {
					sb.insert(0, line);
					sb.insert(line.length(), '\n');
					// do something with the notification xml
					
					
					
					System.out.println("# ------------------------------------------");
					System.out.print("curl -m '");
					System.out.print(sb.toString());
					System.out.print("' " + URL_NOTIF);
					System.out.println();
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
	
	private static int tryToReadSubscriptionRequestNotification_SUBSCRIBE(String line, BufferedReader br, StringBuilder sb) throws IOException {
		
		String line2;
		Matcher m;
		
		m = initXML.matcher(line);
		
		if( m.find() ) {
			// it is a notification
			
			// append and try to find the end of the xml
			sb.append(line);
			while( (line2=br.readLine()) != null ) {
			
				m = endXML.matcher(line2);
				if( m.find() ) {
					
					// the end of the xml fragment
					
					sb.append(line2);
					sb.append('\n');
					return FOUND_NOTIF_STATUS;
					
				} else {
					// a common xml line
					
					// ask if it is UNSUBSCRIBE
					m = UNSUBSCRIBE.matcher(line2);
					
					if( m.find() ) {
						// an unsubscribe notification
						// TODO Do some logic to process in a different way
					}
					
					sb.append(line2);
					sb.append('\n');
					
				}
			}
			
			return PARTIALFOUND_NOTIF_STATUS;
			
			
		} else {
			return NOTFOUND_NOTIF_STATUS;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			System.out.println("filename: " + args[0]);
			parselog(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
