package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.hui.crawler.NewsPro;

public class Caixin extends NewsPro {
	private String startUrl ="http://blog.caixin.com";
	
	private List<String> getColumn() throws IOException{
		Document doc = getDocument(startUrl,"UTF8");
		return NewsPro.getLinks(doc, "div#subnav8 a","", "href");
	}
	
	
	private List<Map<String, String>> getBlogPage(String url) throws IOException{

	   List<Map<String,String>> page =new  ArrayList<>();
	   try {
		Document doc = getDocument(url,"UTF8");
		 for (Element ele :doc.select("div.stutitXwenBlog dl")) {
			 HashMap<String, String> div = new HashMap<>();
			 div.put("NICKNAME", ele.select("div.txt span a").first().text());
			 
			 div.put("RESERVE2", ele.select("h4 a").first().text());
			 div.put("FROMCHANNEL", "caixin");
			 div.put("AUTHORIMG", ele.select("div.pic a img").first().attr("src"));
			 div.put("url", ele.select("h4 a").first().attr("href"));
			 page.add(div);
		
			 }
		 	 
	}catch (Exception e){
		e.printStackTrace();
	}
		 return page;
	}
	
	private Map<String ,String> getBlogContent(Map<String ,String> item) throws IOException{
		try {
		
		Document doc = getDocument(item.get("url"),"UTF8");
		item.put("RESERVE3", doc.select("div.blog_content").first().html());
		item.put("CONTENT", doc.select("div.blog_content").first().text());

		item.put("SIGNATURE", doc.select("div[class*=author] p").last().ownText());
		System.out.println("ok--------"+item.get("url"));
		return item;
		
	}catch(Exception e) {
		System.out.println("报错--------"+item.get("url"));
		e.printStackTrace();
		item.clear();
	}
	return item;
	

}
	public  void crawler() throws IOException, SQLException {
		 
		for (String url :getColumn()) {
			for (Map<String,String> ele :getBlogPage(url)){
		
				 updateBlog(getBlogContent(ele));
				
			}
		}
	}
	
	 public static void main(String[] args) throws IOException, SQLException   {
		Caixin c = new Caixin();
		c.crawler();
	}
	
}
