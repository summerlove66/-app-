package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.hui.crawler.NewsPro;

public class Ynet extends NewsPro{
	private String[] columnUrls = {"http://finance.ynet.com/" ,"http://home.ynet.com/"};
	
	
	 
	 private List<String> getBlogColumn() throws IOException{
		 List<String> pages = new ArrayList<>();
		 for (String url :columnUrls) {
		 Document doc = getDocument(url ,"UTF8");
		 pages.addAll(getLinks(doc ,"ul.cfix.fRight a" ,"","href"));
		 
		 }
		 return pages;
	 }
	 private  List<String> getBlogPage(String url) throws IOException{
		 Document doc =getDocument(url ,"UTF8");
		 return getLinks (doc,"h2 a" ,"" ,"href");
	 }
	 
	 
	 
	 private Map<String ,String> getBlogContent(String url) throws IOException{
		 Map<String ,String> item = new HashMap<String,String>();
		 try {
		 Document doc = getDocument(url ,"UTF8");

		 item.put("RESERVE2",doc.select("div.articleTitle h1").text());
		 item.put("RESERVE3", doc.select("div#articleAll").first().html());
		 item.put("CONTENT", doc.select("div#articleAll").first().text());
		 item.put("AUTHOR", doc.select("span.sourceMsg").first().text().trim());
		 item.put("AUTHORIMG", null);
		 item.put("SIGNATURE",null );
		 item.put("FROMCHANNEL", "ynet");
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 
		 return item;
		 
	 }
	 
	 public  void crawler() throws SQLException, IOException {
		
		
		 try {
				for (String ur : getBlogColumn()) {
					System.out.println("ur ___" + ur);
					for (String ur1 : getBlogPage(ur)) {
						System.out.println("url ___" + ur1);
						updateBlog(getBlogContent(ur1));
					}
				}
				log.info("----------------go");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	 
	 
	 public static void main(String[] args) throws SQLException, IOException {
		Ynet c = new Ynet();
		c.crawler();
	}
}
