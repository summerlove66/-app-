package com.hui.crawler.news;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.hui.crawler.NewsPro;

public class Hexun extends NewsPro {

	private String startUrl = "http://blog.hexun.com/";

	private List<String> getBlogColumn() throws IOException {
		Document doc = getDocument(startUrl, "gb2312");
		return getLinks(doc, "div.allnav dl dd a", "", "href");

	}

	private List<String> getBlogPage(String url) throws IOException {
		Document doc = getDocument(url, "gb2312");
		return getLinks(doc, "h2 a[target=\"_blank\"]", "", "href");
	}

	private Map<String, String> getBlogContent(String url) throws IOException {
		Map<String, String> item = new HashMap<String, String>();
		try {

			Document doc = getDocument(url, "gb2312");
			item.put("RESERVE2", doc.select("span.ArticleTitleText a").text());
			item.put("RESERVE3", doc.select("div#BlogArticleDetail").first().html());
			item.put("CONTENT", doc.select("div#BlogArticleDetail").first().text());
			item.put("AUTHOR", doc.select("div#master_ptoto img").first().attr("alt").split("-")[0].trim());
			item.put("AUTHORIMG", doc.select("div#master_ptoto_1 img").first().attr("src"));
			item.put("SIGNATURE", null);
			item.put("FROMCHANNEL", "hexun");
			item.put("url",url);
			return item;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	public  void crawler() throws SQLException {
	
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
	public static void main(String[] args) throws SQLException {
		Hexun c = new Hexun();
		c.crawler();
	}
}
