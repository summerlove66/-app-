package com.hui.crawler;

import java.io.IOException;
import java.sql.SQLException;

import com.hui.crawler.news.Caijing;

public class Main {
	public static void main(String[] args) throws IOException, SQLException {
		//Todo  导入所有爬虫。。for循环  再加间隔时间
		Caijing c = new Caijing();
		c.crawler();
	}
}
