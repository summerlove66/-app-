package com.hui.crawler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSON;



public class NewsPro {
	//private static final String headImgPath = "/var/tomcat/apache-tomcat-7.0.57/webapps/upmsgs/images/headimg/";
	//private static final String background = "/var/tomcat/apache-tomcat-7.0.57/webapps/upmsgs/images/background/";
	
	private static final String  HEADBACKGROUNDURL = "https://www.rboshi.com:8443/upmsgs/images/background/" ;

	private static final String HEADURL= "https://www.rboshi.com:8443/upmsgs/images/headimg/" ;
	
	private static final String HEADIMGPATH = "./head/";  //临时测试地址
	private static final String BACKGROUND = ".\\";
	
	protected static final Log log = LogFactory.getLog(NewsPro.class);
	
	//替换emoji表情
	 public static String filterEmoji(String source,String slipStr) {
	        if(StringUtils.isNotBlank(source)){
	            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr).replace("'", "\"");
	        }else{
	            return source;
	        }
	    }
	public static Document getDocument(String url, String charSet) throws IOException {

		String html = filterEmoji( Downloader.getResponse(url, charSet),"");
		Document doc = Jsoup.parse(html);
		return doc;
	}

	private static String getImgName() {
		
		
		
		String random = (Math.random() * 1000 + "").substring(0, 3);

		String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

		return random.concat(dateStr) + ".png";
	}

	public static List<String> getLinks(Document doc, String cssSelect, String prefix, String attr) throws IOException {
		List<String> Links = new ArrayList<String>();

		Elements eles = doc.select(cssSelect);
		for (Element ele : eles) {
			Links.add(prefix + ele.attr(attr));
		}
		return Links;
	}

	
	//判断作者是否重复
	public boolean isRepateAuth(String author) throws SQLException, IOException {
		MysqlUtil mysql = new MysqlUtil();
		Statement stmt = mysql.getConnect().createStatement();
		String query_sql = String.format("SELECT NICKNAME FROM investalarm_user WHERE NICKNAME= '%s'",author);
		ResultSet rs = stmt.executeQuery(query_sql);

		if (rs.next()) {
			return true;
		} else {

			return false;
		}

	}
	
	
	//处理正文 及图片
	public Map<String, String> parseContentImg(Map<String, String> item) throws IOException {
		// Map<String ,String> newItem = new HashMap<String,String>();
		String html = item.get("RESERVE3");
		Pattern p = Pattern.compile("<img.*?src=\"(\\S+?)\".*?>");
		Matcher m = p.matcher(html);
		int i = 0;


		List<Map<String,String>> imgAll = new ArrayList<>();
		Map<String ,String >htmlMap= new HashMap<>();
		while (m.find()) {
			Map<String, String> imgOne = new HashMap<>();
			//System.out.println(html.substring(m.start(), m.end()));
			String imgUrl = m.group(1);
			if (! imgUrl.startsWith("http")) {
				imgUrl = "http:"+ imgUrl;
			}
			System.out.println("imgURL---------"+imgUrl);
			//创建目录
			File dayDir = new File(BACKGROUND  + new SimpleDateFormat("yyyyMMdd").format(new Date()));
			if(! dayDir.isDirectory()) {
				dayDir.mkdir();
				
			}
			
			int[] imgSize = Downloader.downloadImg(imgUrl, dayDir + "\\"+getImgName());
			String mark = String.format("<!--IMG#%d-->", i);
			i += 1;
			html = html.replaceFirst("<img.+?>", mark);
		  
			imgOne.put("ref", mark);
			imgOne.put("width", Integer.toString(imgSize[0]));
			imgOne.put("height", Integer.toString(imgSize[1]));
			imgOne.put("src", imgUrl);
			imgAll.add(imgOne);
		}
		String imgJSON = JSON.toJSONString(imgAll) ;
		
	
		htmlMap.put("body", html);
		
		htmlMap.put("img",imgJSON );
		String htmlStr = JSON.toJSONString(htmlMap);
		//htmlStr = htmlStr.replace("'", "\"");
		item.put("IMAGELIST", imgJSON);
		item.put("RESERVE3",htmlStr);
		item.put("SHOWTIME", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		return item;

	}

	//插入最新文章 写入数据库
	public void insertNEWblog(Map<String, String> item, Statement stmt) throws IOException, SQLException {
		Map<String, String> newItem = parseContentImg(item);
		System.out.println(newItem);
		String insertBlogSql = String.format(
				"INSERT INTO  Investalarm_Showinvests(SHOWTIME,CONTENT,IMAGELIST,FROMCHANNEL,RESERVE2,RESERVE3)"
						+ " VALUES ( '%s' ,'%s' ,'%s' ,'%s', '%s', '%s')",
				newItem.get("SHOWTIME"), newItem.get("CONTENT"), newItem.get("IMAGELIST"), newItem.get("FROMCHANNEL"),
				newItem.get("RESERVE2"), newItem.get("RESERVE3"));
		stmt.execute(insertBlogSql);
	}

	public void insertNewUser(Map<String, String> item, Statement stmt) throws IOException, SQLException {

		String authorImg = item.get("AUTHORIMG");
		String author = item.get("AUTHOR");
		String headImg = getImgName();
		String headFileName = HEADIMGPATH  + "\\"+getImgName();
		String BackUrl= HEADBACKGROUNDURL + headImg;
		String signature = item.get("SIGNATURE");
		String headUrl = HEADURL + headImg;
		if (authorImg != null) {
	
		Downloader.downloadImg(authorImg, headFileName);
		}
		String insertUseSql = String.format(
				"insert into investalarm_user(NICKNAME, SIGNATURE,HEADBACKGROUNDURL,HEADURL) values ( '%s','%s', '%s','%s' )", author,signature,
				BackUrl, headUrl);
		stmt.execute(insertUseSql);
	}

	//更新  
	public void updateBlog(Map<String, String> item) throws SQLException, IOException {
	if (!item.isEmpty()) {
		
	
		MysqlUtil mysql = new MysqlUtil();

		Connection conn = mysql.getConnect();
		Statement stmt = conn.createStatement();
		try {
		String author = item.get("AUTHOR");
		String title = item.get("RESERVE2");
		String query_sql = String.format("SELECT  RESERVE2 from  Investalarm_Showinvests WHERE RESERVE2 = '%s'",
				title);
		if (!isRepateAuth(author)) {
			// 作者没有重复 ，直接插入
			System.out.println("+++++++"+author+"++++未重复");
			insertNewUser(item, stmt);
			insertNEWblog(item, stmt);
		} else {
			ResultSet qrs = stmt.executeQuery(query_sql); // check 文章有没重复
			if (!qrs.next()) {
				
				System.out.println("---------文章没有重复---------------------------------------");
				insertNEWblog(item, stmt);

			}

		}}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		mysql.closeConnection(stmt, conn);
	}
	}
	
	
}
