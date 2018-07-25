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
			return KV_JOBTITLE_R.get(trimName);
		}
	}
	
	/**
	 * code to 职称中文名称
	 * 
	 * 职称:JT001-教授级高级工程师,JT002-高级工程师任,JT003-工程师,JT004-助理工程师,JT005-见习生
	 * @param jtcode
	 * @return
	 */
	public static String getJobTitle(String jtcode) {
		String trimCode = StringUtils.trimAllWhitespace(jtcode);
		if(StringUtils.isEmpty(trimCode)) {
			return null;
		}else {
			return KV_JOBTITLE.get(trimCode);
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
			return KV_POSOTION_R.get(trimName);
		}

	}
	
	/**
	 * code to 职务中文名称
	 * 
	 * 职务:PN001-主任,PN002-副主任,PN003-一般设计人员
	 * @param stringCellValue
	 * @return
	 */
	public static String getPosition(String pcode) {
		String trimCode = StringUtils.trimAllWhitespace(pcode);
		if(StringUtils.isEmpty(trimCode)) {
			return null;
		}else {
			return KV_POSOTION.get(trimCode);
		}

	}
	
	@SuppressWarnings("serial")
	//职务:PN001-主任,PN002-副主任,PN003-一般设计人员
	private static 	Map<String,String> KV_POSOTION = new HashMap<String,String>(){{
		this.put("PN001", "主任");
		this.put("PN002", "副主任");
		this.put("PN003", "一般设计人员");
	}};
	
	@SuppressWarnings("serial")
	private static 	Map<String,String> KV_POSOTION_R = new HashMap<String,String>(){{
		KV_POSOTION.forEach((k, v)->{
			this.put(v,k);
		});
	}};
	
	@SuppressWarnings("serial")
	//职称:JT001-教授级高级工程师,JT002-高级工程师,JT003-工程师,JT004-助理工程师,JT005-见习生
	private static 	Map<String,String> KV_JOBTITLE = new HashMap<String,String>(){{
		this.put("JT001", "教授级高级工程师");
		this.put("JT002", "高级工程师");
		this.put("JT003", "工程师");
		this.put("JT004", "助理工程师");
		this.put("JT005", "见习生");
	}};
	
	@SuppressWarnings("serial")
	private static 	Map<String,String> KV_JOBTITLE_R = new HashMap<String,String>(){{
		KV_JOBTITLE.forEach((k, v)->{
			this.put(v,k);
		});
	}};
}
