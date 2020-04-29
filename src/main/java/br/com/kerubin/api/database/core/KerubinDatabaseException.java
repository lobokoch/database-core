package br.com.kerubin.api.database.core;

import java.text.MessageFormat;

public class KerubinDatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
    public KerubinDatabaseException() {
        super();
    }

    public KerubinDatabaseException(String message) {
        super(decorateMessage(message));
    }

    public KerubinDatabaseException(String message, Throwable cause) {
        super(decorateMessage(message), cause);
    }
    
    private static String decorateMessage(String message) {
    	return MessageFormat.format("{0}: {1}", 
    			KerubinDatabaseException.class.getName(), message);
    }

}
