package test.com.crusnikatelier.rss.nexon.forums;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.crusnikatelier.rss.Item;
import com.crusnikatelier.rss.nexon.forums.ActivityService;

public class ActivityServiceTest {
	public static final String sampleFileLocation = "src/main/resources/sample.html";

	@Test
	public void extractActivitiesSuccessTest() throws IOException {
		String expected = "";
		
		//Get the activity Items
		File file = new File(sampleFileLocation);
		Document document = Jsoup.parse(file, "UTF-8");
		ActivityService service = new ActivityService();
		List<Item> items = service.extractActivities(document);
		
		//Check title of first entry
		expected = "firefang2215 started a thread PC on gunner boots in Ruairi";
		assertEquals(expected, items.get(0).getTitle());

		//Check description of last entry
		expected = "git gud skrb";
		assertEquals(expected, items.get(items.size() - 1).getDescription());
		
		//Check date of first and last.
		assertEquals("Thu Aug 18 16:44:00 EDT 2016", items.get(0).getPubDate().toString());
		assertEquals("Thu Aug 18 15:59:00 EDT 2016", items.get(items.size() - 1).getPubDate().toString());
		
		//check the number of entries is correct
		assertEquals(34,  items.size());
	}
}
