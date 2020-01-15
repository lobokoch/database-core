package br.com.kerubin.api.database.util;

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
	
	public static int getPropInt(String name, int defVal) {
		String valueStr = System.getProperty(name);
		if (isEmpty(valueStr)) {
			valueStr = System.getenv(name);
		}
		
		return isNotEmpty(valueStr) ? Integer.parseInt(valueStr) : defVal;
	}

}
