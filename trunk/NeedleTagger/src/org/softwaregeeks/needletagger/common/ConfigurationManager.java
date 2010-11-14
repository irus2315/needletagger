package org.softwaregeeks.needletagger.common;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;

public class ConfigurationManager {
	
	private static boolean isLoaded = false;
	public static final String CONFIGURATION_KEY = "preference";
	public static final String CONFIGURATION_REPORTING = "reporting";
	public static final String CONFIGURATION_PLAYER_LINK = "playerLink";
	public static final String CONFIGURATION_ENABLE_SYSTEM_FONT = "font";
	
	public static ConfigurationManager instance = new ConfigurationManager();
	private static Locale systemLocale;
	private static Typeface font;
	private static boolean isReporting = false;
	private static boolean isPlayerLink = false;
	private static boolean isEnableSystemFont = false;
	private ConfigurationManager(){}
	
	public static ConfigurationManager getInstance()
	{
		return instance;
	}
	
	public static Typeface getFont(AssetManager assetManager)
	{
		if( font != null )
			return font;
		
		font = Typeface.createFromAsset(assetManager,"NanumGothicBold.otf");
		return font;
	}
	
	public static void checkLoad(Context context)
	{
		if( !isLoaded() )
			load(context);
	}
	
	public static void load(Context context)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIGURATION_KEY,Context.MODE_PRIVATE);
		setReporting(sharedPreferences.getBoolean(CONFIGURATION_REPORTING,true));
		setPlayerLink(sharedPreferences.getBoolean(CONFIGURATION_PLAYER_LINK,true));
		setEnableSystemFont(sharedPreferences.getBoolean(CONFIGURATION_ENABLE_SYSTEM_FONT,true));
		setSystemLocale(context.getResources().getConfiguration().locale);
	}

	public static void setReporting(boolean isReporting) {
		ConfigurationManager.isReporting = isReporting;
	}

	public static boolean isReporting() {
		return isReporting;
	}

	public static void setPlayerLink(boolean isPlayerLink) {
		ConfigurationManager.isPlayerLink = isPlayerLink;
	}

	public static boolean isPlayerLink() {
		return isPlayerLink;
	}

	public static void setEnableSystemFont(boolean isEnableSystemFont) {
		ConfigurationManager.isEnableSystemFont = isEnableSystemFont;
	}

	public static boolean isEnableSystemFont() {
		return isEnableSystemFont;
	}

	public static void setLoaded(boolean isLoaded) {
		ConfigurationManager.isLoaded = isLoaded;
	}

	public static boolean isLoaded() {
		return isLoaded;
	}

	public static void setSystemLocale(Locale systemLocale) {
		ConfigurationManager.systemLocale = systemLocale;
	}

	public static Locale getSystemLocale() {
		return systemLocale;
	}
}
