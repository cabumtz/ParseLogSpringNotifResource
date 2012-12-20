package com.wico.parselog;

import java.util.List;

import com.csvreader.CsvWriter;
import com.wi.max.service.SpringService;
import com.wi.max.transport.MessageDeliveryException;
import com.wi.max.transport.br.spring.CarrierResponse;
import com.wi.max.transport.br.spring.SpringClient;
import com.wi.max.transport.br.spring.SpringConfig;
import com.wi.max.transport.br.spring.SpringPropertyNames.CarrierLookupFilter;

public class CsvGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filename;
		String beanfactoryPath;
		String actionName = "[^\"]*";
		String csvfilename;
		List<XmlParsed> messageList;

		try {

			switch (args.length) {

			default:

			case 4:
				actionName = args[3];
				
			case 3:
				csvfilename = args[2];
				beanfactoryPath = args[1];
				filename = args[0];
				break;
				
			case 2:	
			case 1:
			case 0:
				System.out
						.println("ParseLogSpringNotifResource <log_filename> <beanfactoryPath> <csv_filename> [<action_name>]");
				return;
			}

			System.out.println("# ====================================");
			
			
			System.out.println("# - filename: " + filename);
			System.out.println("# - beanfactoryPath: " + beanfactoryPath);
			System.out.println("# - csvfilename: " + csvfilename);
			System.out.println("# - actionName: " + actionName);
			
			System.out.println();

			messageList = ParseGenerator.generateParsedStructures(filename, actionName);

			loadCarrierId(messageList, beanfactoryPath);
			writeCsv(messageList, csvfilename);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static void loadCarrierId(List<XmlParsed> messageList, String beanfactoryPath) {
		
		SpringClient client;
		SpringConfig config;
		CarrierResponse carrierResponse;
		
		SpringService.setBeanFactory(beanfactoryPath);
		
		config = (SpringConfig) SpringService.getBeanFactory().getBean("claro.br.config");
				
		client = new SpringClient(config);
		
		for (XmlParsed elem : messageList) {
			
			try {
				carrierResponse = client.carrierLookup(elem.getMobilePhone(), CarrierLookupFilter.CARRIER_GROUP);
				elem.setCarrierId(carrierResponse.getCarrierGroup());
				
			} catch (MessageDeliveryException e) {
				
				System.err.println("Error trying to get carrier_group for " + elem.getMobilePhone() + ": " + e.getMessage());
			}
			
		}
		
	}
	
	private static void writeCsv(List<XmlParsed> messageList, String csvfilename) {
		
		CsvWriter writer;
		String[] record;
		
		
		writer = new CsvWriter(csvfilename);
		writer.setForceQualifier(true);
		
		try {
			
			record = new String[] {
				"mobilePhone",
				"protocol",
				"tariffRes",
				"carrierId",
				"action",
				"appId",
				"channel",
				"eventSource",
				"operation",
				"body"
			};
			
			writer.writeRecord(record, false);
			
			record = new String[record.length];
			
			for( XmlParsed elem : messageList ) {
				record[0] = elem.getMobilePhone();
				record[1] = elem.getProtocol();
				record[2] = elem.getTariffRes();
				record[3] = elem.getCarrierId();
				record[4] = elem.getAction();
				record[5] = elem.getAppid();
				record[6] = elem.getChannel();
				record[7] = elem.getEventSource();
				record[8] = elem.getOperation();
				record[9] = elem.getXmlBody();
				
				writer.writeRecord(record, false);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (writer != null) writer.close();
			
		}
		
		
	}
}
