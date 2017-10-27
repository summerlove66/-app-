package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.hui.crawler.NewsPro;

public class QQ extends NewsPro {
	private String startUrl ="http://finance.qq.com/";
	 private List<String> getBlogPage() throws IOException{
		 Document doc = getDocument(startUrl ,"GB2312");
		 return getLinks(doc ,"li.item.one a.linkto" ,"","href");	 
	 }
	 
	 private Map<String ,String> getBlogContent(String url) throws IOException{
		 Map<String ,String> item = new HashMap<String,String>();
		 try {
		 Document doc = getDocument(url ,"GB2312");
		 
		 item.put("RESERVE2",doc.select("h1").text());
		 item.put("RESERVE3", doc.select("div#Cnt-Main-Article-QQ").first().html());
		 item.put("CONTENT", doc.select("div#Cnt-Main-Article-QQ").first().text());
		 item.put("AUTHOR", doc.select("div.a_Info span.a_source").first().text().trim());
		 item.put("AUTHORIMG", null);
		 item.put("SIGNATURE","" );
		 item.put("FROMCHANNEL", "qq");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return item;
		 
	 }
	 public  void  crawler() throws SQLException, IOException {

		try {
			for (String url : getBlogPage()) {
				System.out.println(url);
				updateBlog(getBlogContent(url));
				}
			
			} catch (IOException e) {
			// TODO Auto-generated catch block
		
			e.printStackTrace();
		}
//		System.out.println(c.getBlogPage());
	}
	 
	 
	 public static void main(String[] args) throws SQLException, IOException {
	   QQ c = new QQ();
	   c.crawler();
	}
}
