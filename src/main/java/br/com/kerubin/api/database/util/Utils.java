package br.com.kerubin.api.database.util;

import java.text.MessageFormat;

import br.com.kerubin.api.database.core.KerubinDatabaseException;

public class Utils {
	
	public static boolean isNotEmpty(Object value) {
		return !isEmpty(value);
	}
	
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		
		if (value instanceof String) {
			return ((String) value).trim().isEmpty();
		}
		
		return false;
	}
	
	public static String getEnvDef(String name, String defValue) {
		String value = System.getenv(name);
		return isNotEmpty(value) ? value : defValue;
	}
	
	public static String getProp(String name, String defVal) {
		String value = System.getProperty(name);
		if (isEmpty(value)) {
			value = System.getenv(name);
		}
		if (isEmpty(value)) {
			value = defVal;
		}
		return value;
	}
	
	public static String getPropStrict(String name, String defVal) {
		String value = getProp(name, defVal);
		
		if (isEmpty(value)) {
			throw new KerubinDatabaseException(MessageFormat.format("Property \"{0}\" does not have a value.", name)) ;
		}
		
		return value;
	}
	
	public static int getPropInt(String name, int defVal) {
		String valueStr = System.getProperty(name);
		if (isEmpty(valueStr)) {
			valueStr = System.getenv(name);
		}
		
		return isNotEmpty(valueStr) ? Integer.parseInt(valueStr) : defVal;
	}

}
