package com.crusnikatelier.rss.nexon.forums;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.crusnikatelier.rss.Item;

public class ActivityService {
	public static final String BASE_URL = "http://forum2.nexon.net/";
	public static final String ENDPOINT = BASE_URL + "activity.php";
	
	private SimpleDateFormat nexonTimeFormat;
	private SimpleDateFormat dateCalcFormat;
	public ActivityService(){
		nexonTimeFormat = new SimpleDateFormat("hh:mm a");
		dateCalcFormat = new SimpleDateFormat("yyyy/MM/dd");
	}
	
	public List<Item> getCurrentActivities() throws IOException{
		return extractActivities(getDocumentFromNexon());
	}
	
	public Document getDocumentFromNexon() throws IOException{
		return Jsoup.connect(ENDPOINT)
				.userAgent(ActivityService.class.getName())
				.get();
	}
	
	public List<Item> extractActivities(Document document){
		List<Item> items = new ArrayList<Item>();
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
			try {
				current.setPubDate(extractTime(node));
				System.out.println(current.getPubDate());
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
			
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
	
	private static String extractDescription(Element node){
		Elements excerptNodes = node.getElementsByClass("excerpt");
		if(excerptNodes.size() != 1){
			throw new IllegalStateException("Input is in an unknown format");
		}
		Element excerpt = excerptNodes.first();
		return excerpt.html();	
	}
	
	public Date extractTime(Element node) throws ParseException{
		Elements dateElementList = node.getElementsByClass("date");
		Elements timeElementList = node.getElementsByClass("time");

		if(dateElementList.size() < 1){
			throw new IllegalStateException("Unable to find element with class time");
		}
		
		if(dateElementList.size() > 1){
			throw new IllegalStateException("Expecting one element with class time. Found " + dateElementList.size());
		}
		
		if(timeElementList.size() < 1){
			throw new IllegalStateException("Unable to find element with class time");
		}
		
		if(timeElementList.size() > 1){			
			throw new IllegalStateException("Expecting one element with class time. Found " + timeElementList.size());
		}
		
		String time = timeElementList.get(0).text();
		String day = dateElementList.get(0).text();
		
		Date postTime = nexonTimeFormat.parse(time);
		
		Calendar calender = Calendar.getInstance();
		calender.setTime(dateCalcFormat.parse(dateCalcFormat.format(new Date())));
		if(day.contains("Yesterday")){
			calender.add(Calendar.DAY_OF_WEEK, -1);
		}
		
		calender.add(Calendar.MILLISECOND, (int)postTime.getTime());
		return calender.getTime();
	}
}

