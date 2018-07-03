package com.crfsdi.whm.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public  class CodeUtil {

	/**
	 * 职称中文名称 to code
	 * 
	 * 职称:JT001-教授级高级工程师,JT002-高级工程师任,JT003-工程师,JT004-助理工程师,JT005-见习生
	 * @param jtname
	 * @return
	 */
	public static String convertJobTitle(String jtname) {
		String trimName = StringUtils.trimAllWhitespace(jtname);
		if(StringUtils.isEmpty(trimName)) {
			return null;
		}else {
			return KV_JOBTITLE.get(trimName);
		}
	}





	/**
	 * 职务中文名称 to code
	 * 
	 * 职务:PN001-主任,PN002-副主任,PN003-一般设计人员
	 * @param stringCellValue
	 * @return
	 */
	public static String convertPosition(String pname) {
		String trimName = StringUtils.trimAllWhitespace(pname);
		if(StringUtils.isEmpty(trimName)) {
			return null;
		}else {
			return KV_POSOTION.get(trimName);
		}

	}
	@SuppressWarnings("serial")
	//职务:PN001-主任,PN002-副主任,PN003-一般设计人员
	private static 	Map<String,String> KV_POSOTION = new HashMap<String,String>(){{
		this.put("主任", "PN001");
		this.put("副主任", "PN002");
		this.put("一般设计人员", "PN003");
	}};
	
	@SuppressWarnings("serial")
	//职称:JT001-教授级高级工程师,JT002-高级工程师,JT003-工程师,JT004-助理工程师,JT005-见习生
	private static 	Map<String,String> KV_JOBTITLE = new HashMap<String,String>(){{
		this.put("教授级高级工程师", "JT001");
		this.put("高级工程师", "JT002");
		this.put("工程师", "JT003");
		this.put("助理工程师", "JT004");
		this.put("见习生", "JT005");
	}};
}
