package com.customized.tools.po.version;

public class Version {

	public static final String V_1 = "1.0";
	
	public static final String V_2 = "2.0";
	
	public static final String V_CURRENT = V_2 ;
	
	public static final String NAME = "CustomizedTools";
	
	public static final String VERSION_STRING = NAME + " 'KylinSoong' " + V_CURRENT ;
	
	public static String version() {
		return V_CURRENT ;
	}
	
	public static String name() {
		return NAME ;
	}
	
	public static String versionString() {
		return VERSION_STRING ;
	}
}