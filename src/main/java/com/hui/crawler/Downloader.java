package com.hui.crawler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class Downloader {

	private static int connectTimeout = 10000;
	private static int requestTimeout = 5000;
	private static int socketTimeout = 15000;
	public static int retry = 1;
	public static int retrySleep = 5000;

	public static boolean redirect = true;

	public static String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0";

	private static CloseableHttpClient myClient() {

		RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(redirect)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(requestTimeout)
				.setSocketTimeout(socketTimeout).build();
		CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		return client;
	}

	public static byte[] readInputStream(InputStream instream) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1204];
		int len = 0;
		while ((len = instream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		instream.close();
		return outStream.toByteArray();
	}

	
	public static InputStream getStream(String url) throws IOException {

		InputStream stream = null;
		HttpGet httpget = new HttpGet(url);

		httpget.addHeader("User-Agent", ua);
		try {

			CloseableHttpResponse response = myClient().execute(httpget);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();

				int resCode = response.getStatusLine().getStatusCode();
				if (200 <= resCode && resCode < 400) {

					stream = instream;

				} else {

					throw new Exception("状态码异常:" + resCode);
				}
			}
		} catch (Exception e) {

			System.out.println("出错原因:" + e);
			stream = null;
			if (retry > 1) {
				retry -= 1;
				try {
					Thread.sleep(retrySleep);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				stream = getStream(url);

			}

		}
	
		myClient().close();
		
		return stream;

	}
	
	public static String getResponse(String url , String  charSet) throws IOException {
		
		InputStream stream = getStream(url);
		String html =null;
		byte[] data = null;
		try {
			data = readInputStream(stream);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			html = new  String(data,charSet);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println(url+"download html错误");
			e.printStackTrace();
		}
		return html;
		
	}
	
	static int[] downloadImg(String url ,String imgName ) throws IOException{
		
		try {
		    InputStream input = getStream(url);

		   
		
			DataInputStream dataInputStream = new DataInputStream(input);
		   
			@SuppressWarnings("resource")
			FileOutputStream fileOutputStream = new FileOutputStream(new File(imgName));
		   byte[] buffer = new byte[1024];  
           int length;  

           while ((length = dataInputStream.read(buffer)) > 0) {  
               fileOutputStream.write(buffer, 0, length);  
           }  
           BufferedImage sourceImg =ImageIO.read(new File(imgName));
           int[] size1  = {sourceImg.getWidth() ,sourceImg.getHeight()};
           return size1;
         
	}catch(Exception e) {
		System.out.println("download错误 图片" +url);
	}
		  return null;
	}
	

}
