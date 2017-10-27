package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import com.hui.crawler.NewsPro;

public class NeteaseNew extends NewsPro {
	private String startUrl ="http://money.163.com/";
	
	private List<String> getBlogPage() throws IOException{
		Document doc = getDocument(startUrl,"GB2312");
		return NewsPro.getLinks(doc, "div[class^=topnews] h3 a","", "href");
	}
	private  Map<String ,String> getBlogContent(String url) throws IOException{
		 Map<String ,String> item = new HashMap<String,String>();
		 try {
		 Document doc = getDocument(url ,"GB2312");

		 item.put("RESERVE2",doc.select("h1").text());
		 item.put("RESERVE3", doc.select("div#endText").first().html());
		 item.put("CONTENT", doc.select("div#endText").first().text());
		 item.put("AUTHOR", doc.select("a#ne_article_source").first().text());
		 item.put("AUTHORIMG", null);
		 item.put("SIGNATURE",null);
		 item.put("FROMCHANNEL", "163");
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 return item;	 
	
}
	
	public  void crawler() throws SQLException, IOException {
	
		
		try {
			for (String ur1 : getBlogPage()) {
				updateBlog(getBlogContent(ur1));
				}
			
			} catch (IOException e) {
			// TODO Auto-generated catch block
		
			e.printStackTrace();
		}
	
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		NeteaseNew c = new NeteaseNew();
		c.crawler();
	}
	
	}

