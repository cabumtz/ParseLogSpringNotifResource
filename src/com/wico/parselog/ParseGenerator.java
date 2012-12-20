package com.wico.parselog;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.wi.max.transport.br.spring.SpringPropertyNames;

public class ParseGenerator {

	public static List<XmlParsed> generateParsedStructures(String filename, String actionName) throws IOException {
		
		List<XmlParsed> resultList;
		
		List<ParseResult> items;
		XmlParsed res;
		
		items = LogParser.parselog(filename, actionName);
		resultList = new ArrayList<XmlParsed>(15);
		
		for (ParseResult pr : items) {
			try {
				
				res = parseXMLBody(pr.getBody());
				resultList.add(res);
				
			} catch (Exception e) {
				System.err.println("-------------------------------------");
				e.printStackTrace();
			}
		}
		
		return resultList;
	}
	
	private static XmlParsed parseXMLBody(String responseXml) throws Exception{
		StringReader sr;
		SAXBuilder builder;
		XmlParsed result;
		Document doc;
		Element rootNode;
		Element node;
		String tariffResponse;
		
		result = null;
		
		sr = null;
		
		try {

			builder = new SAXBuilder();
			sr = new StringReader(responseXml);
			doc = builder.build(sr);

			rootNode = doc.getRootElement();

			// it should be subscription_request
			if (!rootNode.getName().equals(
					SpringPropertyNames.NOTIF_SUBSCRIPTION_REQUEST)) {
				// if not =>
				throw new Exception("parseRequest - Error Invalid root tag: "
						+ rootNode.getName() + ", expected: "
						+ SpringPropertyNames.NOTIF_SUBSCRIPTION_REQUEST);
			}
			
			result = new XmlParsed();

			result.setXmlBody(responseXml);
			result.setOperation( rootNode.getAttributeValue(SpringPropertyNames.NOTIF_OPERATION) );
			result.setChannel( rootNode.getChildTextTrim(SpringPropertyNames.NOTIF_CHANNEL) );
			result.setAppid( rootNode.getChildTextTrim(SpringPropertyNames.NOTIF_APPID) );
			
			tariffResponse = null;
			
			if ((node = rootNode.getChild(SpringPropertyNames.NOTIF_PHONE)) != null) {
				
				result.setMobilePhone( node.getTextTrim() );
				result.setAction( node.getAttributeValue(SpringPropertyNames.NOTIF_ACTION) );
				
				tariffResponse = node.getAttributeValue(SpringPropertyNames.NOTIF_TARRIF_RESP);
				
				//result.setTariffRes( node.getAttributeValue(SpringPropertyNames.NOTIF_TARRIF_RESP) );
				
				result.setEventSource( node.getAttributeValue(SpringPropertyNames.NOTIF_EVENT_SOURCE) );
				result.setProtocol( node.getAttributeValue(SpringPropertyNames.NOTIF_PROTOCOL) );
				
			}
			
			// tariff_response
			tariffResponse = (tariffResponse != null && tariffResponse.trim().length() > 0) ?
					tariffResponse : (rootNode.getChildTextTrim(SpringPropertyNames.NOTIF_TARRIF_RESP)) ;

			result.setTariffRes(tariffResponse);
			
			return result;
			
		} finally {
			if (sr != null)
				sr.close();
		}
	}
	
	

}
