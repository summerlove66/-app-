package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hui.crawler.Downloader;
import com.hui.crawler.NewsPro;

public class Sohu extends NewsPro {
	private String startUrl ="https://v2.sohu.com/public-api/feed?scene=CHANNEL&sceneId=15&size=10"; //size可以任意设置
	
	 private List<String> getBlogPage() throws IOException{
		 String htmlSource = Downloader.getResponse(startUrl ,"UTF8");
		 List<String> links = new ArrayList<>();
		 List<Map<String,String>> data =JSON.parseObject(htmlSource,new TypeReference<List<Map<String,String>>>(){});
		 for(Map<String,String> ele :data) {
			 String link = String.format("http://www.sohu.com/a/%s_%s", ele.get("id"),ele.get("authorId"));
			 links.add(link);
		 }
		 return links;
	 }
	 private Map<String ,String> getBlogContent(String url) throws IOException{
		 Map<String ,String> item = new HashMap<String,String>();
		 Document doc = getDocument(url ,"UTF8");
		 item.put("FROMCHANNEL", "sohu");
		 item.put("RESERVE2",doc.select("h1").text());
		 item.put("RESERVE3", doc.select("article.article").first().html());
		 item.put("CONTENT", doc.select("article.article").first().text());
		 item.put("AUTHOR", doc.select("div.user-info h4 a").first().text().trim());
		 item.put("AUTHORIMG", doc.select("div.user-pic a img").first().attr("src"));
		 item.put("SIGNATURE",null );
		 return item;
		 
	 }
	 public  void cralwer() throws IOException, SQLException {
	
	try {
		for (String url : getBlogPage()) {
			System.out.println(url);
			updateBlog(getBlogContent(url));
			}
		
		} catch (IOException e) {
		// TODO Auto-generated catch block
		
		e.printStackTrace();
	}
	}
	 
	 public static void main(String[] args) throws IOException, SQLException {
	  Sohu c = new Sohu();
	 c.cralwer();
	}
}
