package kz.orda.components.widgetset.client.codecatcher;

import java.io.Serializable;

public class CodeCatchedEvent implements Serializable{
	private static final long serialVersionUID = -5664418502802694263L;
	private String code;
	
	public CodeCatchedEvent() {
	}
	
	public void setCode(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
