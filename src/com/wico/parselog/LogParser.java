package com.wico.parselog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

	public static int NOTFOUND_NOTIF_STATUS = 0;
	public static int FOUND_NOTIF_STATUS = 1;
	public static int PARTIALFOUND_NOTIF_STATUS = 2;
	
	
	
	private static Pattern headXML = Pattern.compile("<\\?xml\\s+version=\"1\\.0\"");
	private static Pattern initXML = Pattern.compile("<\\s*subscription_request\\s+operation\\s*=\\s*\"notification\"\\s*>");
	private static Pattern endXML = Pattern.compile("</\\s*subscription_request\\s*>");
	
	
	/**
	 * 
	 * 
	 * @param line
	 * @param br
	 * @param sb
	 * @param patternAction
	 * @return
	 * @throws IOException
	 */
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
	
	
	public static List<ParseResult> parselog(String filename, String actionName) throws IOException {
		FileReader fr;
		BufferedReader br;
		String line;
		String line2;
		Matcher m;
		int foundStatus;
		StringBuilder sb;
		Pattern patternAction;
		List<ParseResult> result;
		
		result = new ArrayList<ParseResult>(10);
		sb = null;
		fr = null;
		br = null;
		
		try {
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
						
						result.add(new ParseResult(foundStatus, sb.toString()));
						
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
			
			return result;
			
		} finally {
			if(br != null) 
				br.close();
		}
		
		
		
	}

}
