package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.hui.crawler.NewsPro;

public class NeteaseBlog extends NewsPro {
	private String startUrl ="http://money.163.com/blog/";
	 private List<String> getBlogPage() throws IOException{
		 try {
		 Document doc = getDocument(startUrl ,"Gb2312");
		 return getLinks(doc ,"div.cnt2M ul li span a " ,"","href");
		 }
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
	 }
	 
	 private Map<String ,String> getBlogContent(String url) throws IOException{
		 Map<String ,String> item = new HashMap<String,String>();
		 try {
		 Document doc = getDocument(url ,"GB2312");

		 item.put("RESERVE2",doc.select("h3 span").text());
		 item.put("RESERVE3", doc.select("div[class*=nbw-blog]").first().html());
		 item.put("CONTENT", doc.select("div[class*=nbw-blog]").first().text());
		 item.put("AUTHOR", doc.select("div.nick a").first().text().trim());
		 item.put("AUTHORIMG", doc.select("img#rsavatarimg").first().attr("src"));
		 item.put("SIGNATURE",doc.select("div.m-aboutme div.desc p").first().text() );
		 item.put("FROMCHANNEL", "163");
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
		 return item;
		 
	 }
	 public  void crawler () throws IOException, SQLException {
	
			try {
				for (String ur1 : getBlogPage()) {
					updateBlog(getBlogContent(ur1));
				}
				
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	}

}
	 public static void main(String[] args) throws IOException, SQLException {
		NeteaseBlog c = new NeteaseBlog();
		c.crawler();
	}
}