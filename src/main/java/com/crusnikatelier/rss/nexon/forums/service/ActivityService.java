package com.crusnikatelier.rss.nexon.forums.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.crusnikatelier.rss.Item;

public class ActivityService {
	public static final String BASE_URL = "http://forum2.nexon.net/";
	public static final String ENDPOINT = BASE_URL + "activity.php";
	
	public List<Item> getCurrentActivities() throws IOException{
		List<Item> items = new ArrayList<Item>();
		
		Document document = Jsoup.connect(ENDPOINT)
				.userAgent(getClass().getName()).get();
		
		Element activityStream = document.body().getElementById("activitylist");
		
		
		for(Element node : activityStream.children()){
			
			//This is an unnecessary UI component
			if(node.id().equals("olderactivity")){
				continue;	
			}
			
			Item current = new Item();
			current.setLink(extractLink(node));
			current.setTitle(extractTitle(node));
			current.setDescription(extractDescription(node));
			
			items.add(current);
		}
		return items;
		
	}
	
	private String extractTitle(Element node){
		Elements titleNodes = node.getElementsByClass("title");
		if(titleNodes.size() < 1){
			String err = "Unable to extract title. No title node found";
			throw new IllegalArgumentException(err);
		}
		return titleNodes.first().text();
	}
	
	private String extractLink(Element node){
		Elements linkNodes = node.getElementsByClass("fulllink");
		if(linkNodes.size() != 1){
			throw new IllegalStateException("Input is in an unknown format");
		}
		Element link = linkNodes.first();
		if (link.childNodeSize() != 1){
			throw new IllegalStateException("Input is in an unknown format");
		}
		link = link.child(0);
		return (BASE_URL + link.attr("href"));
	}
	
	private String extractDescription(Element node){
		Elements excerptNodes = node.getElementsByClass("excerpt");
		if(excerptNodes.size() != 1){
			throw new IllegalStateException("Input is in an unknown format");
		}
		Element excerpt = excerptNodes.first();
		return excerpt.html();	
	}
}
