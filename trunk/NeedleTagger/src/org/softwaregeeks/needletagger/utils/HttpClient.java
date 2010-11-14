package org.softwaregeeks.needletagger.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClient
{
	public static String getContents(String url,String encoding)
	{
		return getContents(url,encoding,"GET",null);
	}
	
	public static String getContents(String url,String encoding,String method,List<NameValuePair> datas)
	{
		HttpRequestBase requestBase; 
		StringBuffer buffer = new StringBuffer(); 
		
		if( "POST".equals(method.toUpperCase()) )
		{
			HttpPost httpPost = null;
			try
			{
				httpPost = new HttpPost(url);
				UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(datas, "UTF-8");
				httpPost.setEntity(entityRequest);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			
			requestBase = (HttpRequestBase)httpPost;
		}
		else
			requestBase = new HttpGet(url);
		
	    buffer.setLength(0);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpResponse response;
		
		try
		{
			response = httpclient.execute(requestBase);
			HttpEntity entity = response.getEntity();
			
			BufferedReader br = null;
			String s = null;
			try
			{
				br = new BufferedReader(new InputStreamReader(entity.getContent(),encoding));
				while ((s = br.readLine()) != null)
				{
					buffer.append(s);
					buffer.append("\n");
				}
				br.close();
			}
			finally
			{
				if (br != null)
					try { br.close(); } catch (Exception e) { }
			}
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return buffer.toString();
	}
}