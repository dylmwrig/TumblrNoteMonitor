import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient; //simulates a web browser
import com.gargoylesoftware.htmlunit.html.*; //way too many elements to do it individualy lol
import com.tumblr.jumblr.*;
import com.tumblr.jumblr.types.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Monitor 
{
	private static final JumblrClient TUMBLR_CLIENT = new JumblrClient(
			"xmHAWsN1lRng5IOyxMBijxmNtrwdAE9VCSqfcITBnxi0BitvOc",
			"V91snqcARLP1XznQt7vc4nsLtcQZzoK5r3Rtgr7DvTULkDxiHT");
	private static final Blog MASTER_BLOG = TUMBLR_CLIENT.blogInfo("thelotusmaiden.tumblr.com");
	
	private static final int WAIT_TIME = 7000;

	//no reason to not make the web client global imo
//	final WebClient webClient = new WebClient(BrowserVersion.CHROME); //simulating chrome because that's what she uses
	private static final WebClient WEB_CLIENT = new WebClient(BrowserVersion.FIREFOX_45);
	
	private Monitor()
	{
		WEB_CLIENT.getOptions().setThrowExceptionOnScriptError(false);
	} //end Constructor
	
	public static void main(String args[])
	{	
//		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
/*
		if (new Monitor().visitAttempt("nakkilapsi.tumblr.com"))
		{
			System.out.println("url is cool");
		}
		
		else
		{
			System.out.println("nah it's bad");
		}
*/
		new Monitor().scan();
//		new Monitor().test();
	} //end main
	
	private void test()
	{
		HashMap<String, Integer> toSort = new HashMap<String, Integer>();
		toSort.put("three", 3);
		toSort.put("eleven", 11);
		toSort.put("thirteen", 13);
		toSort.put("four", 4);
		toSort.put("one", 1);
//		Map.Entry<String, Integer> [] entryArr = toSort.entrySet().toArray(new Map.Entry<String, Integer>[toSort.size()]);
		Integer[] values = toSort.values().toArray(new Integer[toSort.size()]);
		String[] keys = toSort.keySet().toArray(new String[toSort.size()]);
//		for (Map.Entry<String, Integer> i : entryArr)
		for (int i = 0; i < values.length; i++)
		{
			for (int j = 0; j < values.length; j++)
			{
				if (values[i] < values[j])
				{
					int temp = values[i];
					values[i] = values[j];
					values[j] = temp;
					String tempStr = keys[i];
					keys[i] = keys[j];
					keys[j] = tempStr;
				}
			}
		}
		LinkedHashMap <String, Integer> rtn = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < values.length; i++)
		{
			rtn.put(keys[i], values[i]);
		}

		Iterator it = rtn.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pair = (Map.Entry)it.next();
			System.out.println(pair.getKey() + " " + pair.getValue());
		}
	}
	
	//generic "do stuff" method that I'll refactor after implementation 
	private void scan()
	{
		System.out.println("Inside scan");
		List <Post> posts = MASTER_BLOG.posts();
		Post newest = posts.get(3); //put the index of whatever post you want in here
		
		String postHref = newest.getPostUrl(); //get the href to find the anchor using jumblr
		
		//webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		//webClient.getOptions().setThrowExceptionOnScriptError(false);
		
		//java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		
		final String baseURL = "http://thelotusmaiden.tumblr.com";
//		final String baseURL = "http://www.reddit.com/r/awwnime";
		System.out.println("Before try");
		try 
		{
			final HtmlPage page = WEB_CLIENT.getPage(baseURL);
			System.out.println("Got that page");
			WEB_CLIENT.waitForBackgroundJavaScript(WAIT_TIME);
			System.out.println("Here is the page title: ");
			System.out.println(page.getTitleText());

			HtmlAnchor link = page.getAnchorByHref(postHref); //get the page for the individual post
			final List<?> reblogs = getAllNotes(link); 
			System.out.println("reblogs count: " + reblogs.size());
			System.out.println("type of reblogs: " + reblogs.get(0).getClass().getName());
			
			//initialize the iterable to the first reblog so that we've initialized it properly
			Iterable<DomElement> children = ((DomElement) reblogs.get(0)).getChildElements();
			
			Map reblogSources = new LinkedHashMap(); //holds the sources of reblogs

			String reblogURL, reblogID;
			
			long reblogLong; //convert reblogID to a usable type
			
			Blog reblogger;  //blog who reblogged the post
			Post reblog; 
			
			//hold the amount of reblogs between certain times
			int zeroToSix = 0, sixToNoon = 0, noonToSix = 0, sixToMidnight = 0, hour = 0; 
			HashMap <String, Integer> tagMap = new HashMap<String, Integer>(); //holds every tag and the amount of times each was reblogged
			
			//store the child elements of each reblog, so we can more easily navigate it and pull most popular
		
			//person to reblog from
			for (int i = 0; i < reblogs.size(); ++i)
			{
				children = ((DomElement) reblogs.get(i)).getChildElements();
				List<DomElement> target = new ArrayList<DomElement>(); //create array list to hold contents of iterable
				children.forEach(target :: add); 					   //add each iterable to the list
				children = ((DomElement) target.get(1)).getChildElements();
				children.forEach(target :: add);
				
				for (int j = 0; j < target.size(); j++)
				{
					System.out.println("This is target: " + target.get(j));
				}
				
				reblogURL = target.get(1).toString();
				
				System.out.println("ReblogURL at first assignment: " + reblogURL);
				
				String[] reblogSplit;
				reblogSplit = reblogURL.split("post/|\"");
				
				reblogID = reblogSplit[4]; //current location of the reblog's id
				System.out.println("reblogID: " + reblogID);
				
				for (int j = 0; j < reblogSplit.length; j++)
				{
					System.out.println("j " + j + " , " + reblogSplit[j]);
				}
				
				System.out.println("URL of the reblog: " + reblogURL);
				
				reblogLong = Long.valueOf(reblogID).longValue();
				reblogSplit = reblogSplit[3].split("://|/"); //remove the "http://" from the blog name so it can be fed into tumblrClient
				reblogURL = reblogSplit[1];

				System.out.println("reblogUrl after reblogSplit[1]: " + reblogURL);
				
				//if the url redirects or is otherwise invalid, just skip the rest of the loop
				if (!checkURL(reblogURL) || !visitAttempt(reblogURL))
				{
					continue;
				} //end if
				
				reblogger = TUMBLR_CLIENT.blogInfo(reblogURL);
				reblog = reblogger.getPost(reblogLong); //get the reblog by post ID
				System.out.println("source title of reblog: " + reblog.getBlogName());
				System.out.println("timestamp: " + reblog.getTimestamp());
				List<String> reblogTags = reblog.getTags();
				
				for (String tag : reblogTags)
				{
					System.out.println("Here is your tag: " + tag);
				}
				
				addTags(reblogTags, tagMap);
				
				//timestamp is returned as long, representing milliseconds since epoch
				//multiply by 1000 and set the eastern time zone. The result is a date you can get different info from
				//"EST" is supported by DateTimeZone, just tested it
				DateTimeZone timeZone = DateTimeZone.forID("EST"); 
				DateTime dateTime = new DateTime((reblog.getTimestamp() * 1000), timeZone);
				
				//dateTime = dateTime.plus(reblog.getTimestamp());
				System.out.println("getHourOfDay: " + dateTime.getHourOfDay());
				System.out.println("getDayOfMonth: " + dateTime.getDayOfMonth());
				System.out.println("getMonth: " + dateTime.getMonthOfYear());
				System.out.println("getYear: " + dateTime.getYear());
				
				hour = dateTime.getHourOfDay();
				if (hour < 6)
				{
					zeroToSix++;
				} //end if
				
				else if (hour < 12)
				{
					sixToNoon++;
				} //end else if

				else if (hour < 18)
				{
					noonToSix++;
				} //end else if
				
				else
				{
					sixToMidnight++;
				} //end else
				
				//because there is no "reblogged from field" the op has a smaller list
				//this can really just be a "if target.size() != 4" instead of an if else but I'll just do this for now for testing
				if (target.size() == 4) 
				{
					System.out.println("Original Post");
				}
				
				else
				{
					String brute = target.get(4).toString();
					String[] split = brute.split("/*");
					//I want to make this http:// just for specificity but this breaks with https://
					//maybe find some regex operator for this? Idk
					split = brute.split("://|\\.tumblr"); 
					
					String reblogSource = split[1]; //name of person reblogged from, the stuff between http:// and .tumblr
					ArrayList<String> rebloggedFrom = new ArrayList<String>();

					rebloggedFrom.add(split[1]);
					
					int reblogCount = 1;   //this person was reblogged from at least once
					boolean append = true; //checks if we need to add the blog name to the end
					
					//there is no reason to check the contents of the list if it is empty
					if (reblogSources.size() == 0) 
					{
						reblogSources.put(reblogSource, 1); //first item so value is 1
					} //end if
					
					else
					{
						Set set = reblogSources.entrySet(); 
						Iterator iter = set.iterator();
						
						while (iter.hasNext())
						{
							//map is the java equivalent of the python dictionary
							Map.Entry keyValue = (Map.Entry)iter.next();
							//if there is another item in the list which is equal to the item we are checking against
							//iterate the amount of reblogs which it has and break from the loop
							if (keyValue.getKey().toString().equals(reblogSource))
							{
								reblogCount = (int) keyValue.getValue();
								reblogCount++;
								keyValue.setValue(reblogCount);
								append = false;
								System.out.println(reblogSource + " has " + reblogCount + " reblogs\n\n\n");
								break; //no need to keep checking if it's in there once
							} //end if
						} //end while
						
						//otherwise, if it was nowhere to be found, add it to the end of the list
						//add the blog to the end with a value of one if it wasn't in the list
						if (append)
						{
							reblogSources.put(reblogSource, reblogCount); 
							System.out.println("Added " + reblogSource + " to the list.\n\n\n");
						} //end if
					} //end else
				} //end else	
			} //end for
			
			LinkedHashMap<String, Integer> sortedTags = bubbleSortHashMap(tagMap);
			Iterator it = sortedTags.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pair = (Map.Entry)it.next();
				System.out.println(pair.getKey() + " " + pair.getValue());
			} //end while
			/*
			//TODO refactor
			//I realize now that I just did basically the same thing more efficiently in the addTags method
			//see if you can combine functionality
			ArrayList <String> names = new ArrayList();
			ArrayList <Integer> count = new ArrayList();
			
			//separate the reblogs into separate name and count lists, then sort them
			if (reblogSources.size() > 0)
			{
				Set set = reblogSources.entrySet();
				Iterator iter = set.iterator();
				
				while (iter.hasNext())
				{
					Map.Entry keyValue = (Map.Entry)iter.next();
				
					names.add(keyValue.getKey().toString());
					count.add(Integer.parseInt(keyValue.getValue().toString()));
				} //end while
			} //end if
//			bubbleSort(names, count);

			for (int i = 0; i < count.size(); i++)
			{
				System.out.println("names and count " + names.get(i).toString() + " " + count.get(i).toString());
			} //end for
			
			System.out.println("0-6, 6-12, 12-18, 18-24: " + zeroToSix + " " + sixToNoon + " " + noonToSix + " " + sixToMidnight);
			*/
		} //end try
		
		catch (Exception e)
		{
			e.printStackTrace();
		} //end catch
		
		finally
		{
			WEB_CLIENT.close();
		} //end finally
	} //end scan
	
	//classic bubble sort using a hashmap
	//
	//convert toSort to separate key and value arrays at the beginning to make sorting easier
	//perform the usual bubble sort based on the value array, switching the appropriate items in the key array at the same time
	//when sorting is complete, add each item from each array into a linked hashmap
	//return a linked hashmap, which is a hashmap which maintains the order in which items were inserted
	private LinkedHashMap<String, Integer> bubbleSortHashMap(HashMap<String, Integer> toSort)
	{
		Integer[] values = toSort.values().toArray(new Integer[toSort.size()]);
		String[] keys = toSort.keySet().toArray(new String[toSort.size()]);
		
		for (int i = 0; i < values.length; i++)
		{
			for (int j = 0; j < values.length; j++)
			{
				if (values[i] < values[j])
				{
					int temp = values[i];
					values[i] = values[j];
					values[j] = temp;
					
					String tempStr = keys[i];
					keys[i] = keys[j];
					keys[j] = tempStr;
				} //end if
			} //end for
		} //end for
		
		LinkedHashMap <String, Integer> rtn = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < values.length; i++)
		{
			rtn.put(keys[i], values[i]);
		} //end for
		
		return rtn;
	} //bubbleSortHashMap
	
	//sort hashmap based on value, descending order
	//also sort by the amount of reblogs that we're even interested in;
	//if we're interested in storing reblog sources outside of those, we can just make a copy of the hashmap
	//before calling this and don't modify that. But this is meant to both sort and restrict the number of
	//values in the hashmap
	private HashMap sortSources(HashMap toSort, int toSortSize)
	{
		//throw exception if wrong size, don't feel like figuring out syntax rn lmao
		if (toSort.size() < 1)
		{
			//throw IllegalArgumentExcpetion;
		} //end if
		
		Set set = toSort.entrySet();
		Iterator iter = set.iterator();
		
		//access each respective list by just using the index in some sort of for loop
		//so move element at location i in keys if you're moving element in location i in values
		List keys = new ArrayList();
		List values = new ArrayList();
		
		//add each key and value pairing to their respective arrays from the hashmap
		while (iter.hasNext())
		{
			Map.Entry value = (Map.Entry)iter.next();
			keys.add(value.getKey().toString());
			values.add(Integer.parseInt(value.getValue().toString())); //did I just get hacked?
		} //end while
		
		int start = 0, end = (values.size() - 1);
		//TODO quicksort
		
		return toSort;
	} //end sortSources
	
	//method for swapping the values using usual temp value strategy
	private void swap(int i, int j, List <String>names, List<Integer> count)
	{
		System.out.println("swapping these values: " + count.get(i).toString() + " " + count.get(j).toString());
		
		int temp = Integer.parseInt(count.get(i).toString()); //hold the values of start during the  swap
		String tempStr = names.get(i).toString();

		count.set(i, Integer.parseInt(count.get(j).toString()));
		names.set(i, names.get(j).toString());
		
		count.set(j, temp);
		names.set(j, tempStr);
	} //end swap
	
	//the purpose of this method is to add a list of tags into the running tagMap
	//tagMap is the entire hashmap of tags, where the key is the tag and the value is the total running count
	//tags is the list of tags from an individual reblog to be added to the tagMap
	//
	//for each item in tags, check if that item is held as a key in the tagMap
	//--if it is, iterate that key's value
	//--otherwise, add the tag to the map
	private void addTags(List<String> tags, HashMap<String, Integer> tagMap)
	{
		for (String tag : tags)
		{
			if (tagMap.containsKey(tag))
			{
				tagMap.put(tag, tagMap.get(tag) + 1);
			} //end if
			else
			{
				tagMap.put(tag, 1);
			} //end else
		} //end for
	} //end addTags
	
	private void run()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("notes_info", "True");
		List <Post> posts = MASTER_BLOG.posts(map);
		//Post newest = posts.get(4); //put the index of whatever post you want in here
		
		Post newest = MASTER_BLOG.getPost(154931927092L);
		List <LinkedHashMap> popularTags = filterTags(newest);
	} //end Run
	
	//I'd like to cast the return of this to the type you're using anyway
	//which is HtmlListItem
	//however, looking at the conversion, the type returned from 
	//notePage.getByXPath is really strange so I'd rather not risk it
	private final List<?> getAllNotes(HtmlAnchor link) throws IOException
	{
		HtmlPage notePage = link.click(); //this is the page you will pull your notes from
		WEB_CLIENT.waitForBackgroundJavaScript(500);

		//while there are more notes buttons to click, keep clicking
		//sometimes the post will not have more notes to load: if you try to find an anchor tag which is not there
		//the program will crash. So put it in a try block
		//I may try putting all of the "most popular person to reblog from" section into here but the list you
		//get from this could be valuable for other stuff like most popular tags.
		boolean keepClicking = true;
		int clickCount = 0; //keeps track of amount of show more notes, mainly for testing
		while (keepClicking)
		{
			try
			{
				System.out.println("Page name: " + notePage.getBaseURL());
				HtmlAnchor showMore = notePage.getAnchorByText("Show more notes");
				notePage = showMore.click(); //load the extra notes
				System.out.println("Clicked " + clickCount + " times");
				clickCount++;
				WEB_CLIENT.waitForBackgroundJavaScript(WAIT_TIME);
			} //end try
			
			catch(Exception e) //make your catches more specific rather than gotta catch em all every time
			{
				System.out.println("No more notes to display.");
				System.out.println("Clicked show more " + clickCount + " times.");
				keepClicking = false;
			} //end catch
		} //end while
	
		return notePage.getByXPath("//li[starts-with(@class, 'note reblog')]");
	} //end getAllNotes
	
	private List <LinkedHashMap> filterTags(Post newest)
	{
		List <Note> notes = newest.getNotes();
		List <String> allTags = new ArrayList();
		List <LinkedHashMap> results = new ArrayList(20);
		
		Post reblog = null;
		long reblogID = 0;
		Blog reblogger = null;
		String reblogString = "";
		List<String> reblogTags;
		for (int i = 0; i < notes.size(); i++)
		{
			if (notes.get(i).getType().equals("reblog"))
			{
				reblogString = notes.get(i).getBlogUrl();
				reblogString = reblogString.substring(7, reblogString.length() - 1); //remove http:// and final /
				
				//this next bit is for the blogs whose urls start with https://, as the other parsing method leaves a final /
				char[] splitString = reblogString.toCharArray();
				
				if (splitString[0] == '/')
				{
					reblogString = reblogString.substring(1, reblogString.length());
				} //end if
				
				if (checkURL(reblogString))
				{
					reblogger = TUMBLR_CLIENT.blogInfo(reblogString);
					reblogID = notes.get(i).getPostId();
					reblog = reblogger.getPost(reblogID);
					allTags.addAll(reblog.getTags());
					System.out.println(reblogString + " does not redirect");
					System.out.println(reblogID + " is their id");
				} //end if
				
				else
				{
					System.out.println(reblogString + " redirects because tumblr programmers are lazy");
					System.out.println("Here is their id " + reblogID);
					
					//TODO
					//wanted to just modify the reblogString if the return code started with a 3, indicating a redirect, indicating that the 
					//blog page went to tumblr.com/dashboard/blog/blogName, for the mobile version of the blog.
					//I'll try looking at this again later
					/*
					reblogString = reblogString.substring(0, reblogString.length() - 11);
					reblogString = "tumblr.com/dashboard/blog/" + reblogString;// + "/" + reblogID;
					System.out.println("Here is the altered reblog string " + reblogString);
					reblogger = client.blogInfo(reblogString);
					reblogID = notes.get(i).getPostId();
					reblog = reblogger.getPost(reblogID);
					allTags.addAll(reblog.getTags());
					*/
				} //end else
				
			} //end if
			
		} //end for
		
		for (int i = 0; i < allTags.size(); i++)
		{
			System.out.println(allTags.get(i));
		}

		LinkedHashMap add = new LinkedHashMap();
		add.put("a", "HEY YOU ");
		results.add(0, add);

		return results;
	} //end filterTags
	
	//there is an issue where certain blogs, for no discernible reason will just redirect to that view where you're on the dashboard
	//but the blog just sort of appears in the right side and you have to browse in this mini view
	//My program crashes when it hits that because it can't properly read in tags from that url, so this method is the band aid
	//it reads in each url and checks the response code: if it starts with a 3, it is a redirect, so just ditch that blog
	//
	//the existence of this method is a flaw in the program for obvious reasons: I am not counting tags which are used, kind of undermining
	//the point of the program. But I don't know a workaround for this mostly undocumented problem.
	private boolean checkURL(String url)
	{
		//in the filterTags function, I remove the http:// and the end / because that's the format required by jumblr, so just add it back in
		url = "http://" + url + "/";
		
		try
		{
			URL testURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) testURL.openConnection();

			int response = con.getResponseCode();
			
			//2xx is an HTTP status code indicating success
			final int successLow = 199, successHigh = 300;
			return ((successLow < response) && (response < successHigh)) ? true : false;
		} //end try
		
		catch (Exception e)
		{
			System.out.println("There was an error checking the url.");
			return false;
		} //end catch
	} //end checkURL
	
	//similar to checkURL in that this is to check the validity of the url
	//the difference is that certain urls will break jumblr/htmlUnit but will still pass checkURL
	//so this will actually try to visit the url
	private boolean visitAttempt(String url)
	{
		System.out.println("inside visitAttempt, url: " + url);
		try 
		{
			Blog test = TUMBLR_CLIENT.blogInfo(url);
			System.out.println("blog test done");
			url = "http://" + url + "/";
			System.out.println("url: " + url);
			
			//I want to check for htmlpage too but this shit breaks hard, even though it's in a try catch block.
			HtmlPage page = WEB_CLIENT.getPage(url);
			System.out.println("html test done");
			
			//System.out.println("htmlPage test: " + page.getTitleText());
			System.out.println("jumblr name: " + test.getName());
			System.out.println("title has been printed");
			return true;
		} //end try
		
		//catch (FailingHttpStatusCodeException | IOException | MalformedURLException e) 
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("\n\n\n\n\n\n\n\n\nvisit attempt failed on " + url + "\n\n\n\n");
			return false;
		} //end catch
	} //end visitAttempt
} //end Monitor
