package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.hui.crawler.NewsPro;

public class Tonghuashun extends NewsPro {
	private String startUrl = "http://t.10jqka.com.cn/view/";

	List<String> getBlogPage() throws IOException {
		Document doc = getDocument(startUrl, "UTF8");
		return getLinks(doc, "ul.sp-body-content li.sp-body-articleItem", "", "data-href");
	}
	private String getAuthor(String url) throws IOException {
		Document doc = getDocument(url,"UTF8");
		try {
		   return doc.select("div.hpage-uintroduce").first().text();
		}catch (Exception e){
			return null;
		}
	}
	private Map<String, String> getBlogContent(String url) throws IOException {
		Map<String, String> item = new HashMap<String, String>();
		try {
			Document doc = getDocument(url, "UTF8");
			String  authorUrl = doc.select("a.post-author").first().attr("href");
			item.put("RESERVE2", doc.select("div.tac.detail-title").first().text());
			item.put("RESERVE3", doc.select("div.post-detail-text").first().html());
			item.put("CONTENT", doc.select("div.post-detail-text").first().text());
			item.put("AUTHOR", doc.select("a.post-author").first().text());
			item.put("AUTHORIMG", doc.select("a.avatar img").first().attr("src"));
			item.put("FROMCHANNEL", "tonghuashun");
			item.put("SIGNATURE", getAuthor(authorUrl));
			// log.info("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;

	}

	public void crawler() throws IOException, SQLException {
		
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
		Tonghuashun c = new Tonghuashun();
		c.crawler();
	}
}
