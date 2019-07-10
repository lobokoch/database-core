package br.com.kerubin.api.database.util;

import java.util.Arrays;
import java.util.Collection;

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
		
		if (value instanceof Collection) {
			return ((Collection<?>) value).isEmpty();
		}
		
		if (value.getClass().isArray()) {
			return Arrays.asList(value).isEmpty();
		}
		
		return false;
	}
	
	public static String getEnvDef(String name, String defValue) {
		String value = System.getenv(name);
		return isNotEmpty(value) ? value : defValue;
	}

}
