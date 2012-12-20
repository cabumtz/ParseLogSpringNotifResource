package com.wico.parselog;

public class XmlParsed {

	private String mobilePhone;
	private String operation;
	private String channel;
	private String appid;
	private String action;
	private String tariffRes;
	private String eventSource;
	private String protocol;
	private String xmlBody;
	private String carrierId;

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTariffRes() {
		return tariffRes;
	}

	public void setTariffRes(String tariffRes) {
		this.tariffRes = tariffRes;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getXmlBody() {
		return xmlBody;
	}

	public void setXmlBody(String xmlBody) {
		this.xmlBody = xmlBody;
	}

	public String getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}

	@Override
	public String toString() {
		return "XmlParsed [mobilePhone=" + mobilePhone + ", operation="
				+ operation + ", channel=" + channel + ", appid=" + appid
				+ ", action=" + action + ", tariffRes=" + tariffRes
				+ ", eventSource=" + eventSource + ", protocol=" + protocol
				+ ", xmlBody=" + xmlBody + ", carrierId=" + carrierId + "]";
	}


}
