package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.hui.crawler.NewsPro;

public class Caijing extends NewsPro {
	private String startUrl = "http://blog.caijing.com.cn/index.html";
	 List<String> getBlogColumn() throws IOException{ 
		 Document doc  = getDocument(startUrl ,"UTF8");
		 List<String>  links = getLinks(doc ,"ul#show_user_index li a" ,"http://blog.caijing.com.cn" ,"href");
		 return links.subList(1, links.size());
	 }
	 
	 
	 private List<String> getBlogPage(String url) throws IOException{
		 Document doc = getDocument(url ,"UTF8");
		 return getLinks(doc ,"div.gxypmgx dt a" ,"http://blog.caijing.com.cn","href");
		 
	 }
	 
	 private Map<String ,String> getBlogContent(String url) throws IOException{
		 Map<String ,String> item = new HashMap<String,String>();
		 try {
		 Document doc = getDocument(url ,"UTF8");

		 item.put("RESERVE2",doc.select("h1#printTitle").text());
		 item.put("RESERVE3", doc.select("div#blog_content").first().html());
		 item.put("CONTENT", doc.select("div#blog_content").first().text());
		 item.put("AUTHOR", doc.select("div.blmjjjimg a").first().text().trim());
		 item.put("AUTHORIMG", null);
		 item.put("SIGNATURE",doc.select("div.blmjjjri").first().ownText() );
		 item.put("FROMCHANNEL", "caijing");
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 return item;
		 
	 }
	 
	 public  void crawler() throws IOException, SQLException {
		 
			try {
				for (String ur :getBlogColumn()) {
					System.out.println("ur ___" + ur);
					for (String ur1 : getBlogPage(ur)) {
						System.out.println("url ___" + ur1);
						updateBlog(getBlogContent(ur1));
					}
				}
			//	log.info("----------------go");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	 
	
//		 List<String> blogs = new ArrayList<>();
//		 try {	
//				
//				for (String ele :getBlogColumn()) {
//					blogs.addAll(getBlogPage(ele));
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		 
//	System.out.println(blogs.size());
//	for (int i=0 ;i <3 ;i++) {
//		Thread th =new Thread (()  ->{
//		 	for (String ele : blogs) {
//		 		try {
//					try {
//						updateBlog(getBlogContnt(ele));
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		 	}
//});
//		th.start();
//	}

	 
	 
	 public static void main(String[] args) throws IOException, SQLException {
		Caijing c = new Caijing();
		c.crawler();
	}
	 
}
