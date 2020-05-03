package br.com.kerubin.api.database.util;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.StringTemplate;

public class SQLUtils {
	
	private SQLUtils() {
		// Utils
	}
	
	public static StringTemplate buildUnaccent(StringPath field) {
		return Expressions.stringTemplate("unaccent({0})", field);		
	}
	
	public static StringTemplate buildUnaccent(String value) {
		return Expressions.stringTemplate("unaccent({0})", value);		
	}

}
