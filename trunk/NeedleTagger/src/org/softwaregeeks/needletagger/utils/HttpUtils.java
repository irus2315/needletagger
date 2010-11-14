package org.softwaregeeks.needletagger.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

	public static String download(String urlString) throws IOException
	{
		String html = null;
		HttpURLConnection urlCon = null;
		
		URL url = new URL(urlString);
		urlCon = (HttpURLConnection) url.openConnection();
		urlCon.setUseCaches(false);
		urlCon.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.2 (KHTML, like Gecko) Chrome/5.0.342.3 Safari/533.2");
		
		html = getContents(urlCon.getInputStream());
		urlCon.disconnect();
		
		return html;
	}
	
	public static String download(String urlString,String encode) throws IOException
	{
		String html = null;
		HttpURLConnection urlCon = null;
		
		URL url = new URL(urlString);
		urlCon = (HttpURLConnection) url.openConnection();
		urlCon.setUseCaches(false);
		urlCon.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/533.2 (KHTML, like Gecko) Chrome/5.0.342.3 Safari/533.2");
		
		html = getContents(urlCon.getInputStream(),encode);
		urlCon.disconnect();
		
		return html;
	}
	
	public static String getContents(InputStream is) throws IOException
	{
		return getContents(is,"EUC-KR");
	}
	
	public static String getContents(InputStream is,String encode) throws IOException
	{
		if( is == null )
			return null;
		
		String s = null;
		StringBuffer dataBuffer = new StringBuffer();
		
		BufferedReader resultStreamBuffer = new BufferedReader(new InputStreamReader(is,encode));
		while((s = resultStreamBuffer.readLine()) != null)
		{
			dataBuffer.append(s);
			dataBuffer.append("\r\n");
		}
		resultStreamBuffer.close();
		
		return dataBuffer.toString();
	}	
}