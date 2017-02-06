import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient; //simulates a web browser
import com.gargoylesoftware.htmlunit.html.*; //way too many elements to do it individualy lol
import com.gargoylesoftware.htmlunit.javascript.host.dom.Node;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.tumblr.jumblr.*;
import com.tumblr.jumblr.types.*; 

public class Monitor 
{
	private JumblrClient client = new JumblrClient(
			"xmHAWsN1lRng5IOyxMBijxmNtrwdAE9VCSqfcITBnxi0BitvOc",
			"V91snqcARLP1XznQt7vc4nsLtcQZzoK5r3Rtgr7DvTULkDxiHT");
	private Blog blog = client.blogInfo("thelotusmaiden.tumblr.com");
	

	private Monitor()
	{
		
	} //end Constructor
	
	//because I'm getting more into java, I'm trying to handle main in a more java way
	//found this on stackexchange: make main simply call monitor, which will parse input
	//create objects and the other program startup stuff
	public static void main(String args[])
	{
		//new Monitor().Run();
		//new Monitor().Test();
		ArrayList names = new ArrayList();
		ArrayList count = new ArrayList();
		names.add("I");
		names.add("don't");
		names.add("wanna");
		names.add("live");
		names.add("no");
		names.add("mo");
		names.add("sometimes");
		count.add(11);
		count.add(10);
		count.add(4);
		count.add(2);
		count.add(5);
		count.add(19);
		count.add(8);
		
		int test = Integer.parseInt(count.get(0).toString());
		int test2 = Integer.parseInt(count.get(1).toString());

		for (int i = 0; i < count.size(); i++)
		{
			System.out.println("names and count " + names.get(i).toString() + " " + count.get(i).toString());
		} //end for
		
		int start = 0, end = (count.size() - 2);
		
		System.out.println("test and test2 " + test + " " + test2);
		System.out.println(Integer.compare(test, test2));
		Monitor myTest = new Monitor();
		myTest.quickSort(names, count, start, end);
		
		for (int i = 0; i < count.size(); i++)
		{
			System.out.println("names and count " + names.get(i).toString() + " " + count.get(i).toString());
		} //end for
	} //end main
	
	//test stuff here
	private void Test()
	{
		List <Post> posts = blog.posts();
		Post newest = posts.get(4); //put the index of whatever post you want in here
		
		String postHref = newest.getPostUrl(); //get the href to find the anchor using jumblr
		
		final WebClient client = new WebClient(BrowserVersion.CHROME); //simulating chrome because that's what she uses
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setThrowExceptionOnScriptError(false);
		//String url = "http://thelotusmaiden.tumblr.com/post/155180649080/thelotusmaiden-late-night-shopping-high-res";
		String url = "http://thelotusmaiden.tumblr.com";
		
		try 
		{
			final HtmlPage page = client.getPage(url);
			client.waitForBackgroundJavaScript(500);
			System.out.println(page.getTitleText());

			HtmlAnchor link = page.getAnchorByHref(postHref); //get the page for the individual post
			HtmlPage notePage = link.click(); //this is the page you will pull your notes from
			client.waitForBackgroundJavaScript(500);

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
					client.waitForBackgroundJavaScript(500);
				} //end try
				
				catch(Exception e) //make your catches more specific rather than gotta catch em all every time
				{
					System.out.println("No more notes to display.");
					System.out.println("Clicked show more " + clickCount + " times.");
					keepClicking = false;
				} //end catch
			} //end while
		
			final List<?> reblogs = notePage.getByXPath("//li[starts-with(@class, 'note reblog')]");
			
			System.out.println(reblogs.get(10).getClass());
			//testing

			//initialize the iterable to the first reblog so that we've initialized it properly
			Iterable<DomElement> children = ((DomElement) reblogs.get(0)).getChildElements();
			
			Map reblogSources = new LinkedHashMap(); //holds the sources of reblogs
			
			//store the child elements of each reblog, so we can more easily navigate it and pull most popular
			//person to reblog from
			for (int i = 0; i < reblogs.size(); ++i)
			{
				children = ((DomElement) reblogs.get(i)).getChildElements();
				List<DomElement> target = new ArrayList<DomElement>(); //create array list to hold contents of iterable
				children.forEach(target :: add); //add each iterable to the list
				children = ((DomElement) target.get(1)).getChildElements();
				children.forEach(target :: add);
				for (int j = 0; j < target.size(); j++)
				{
					System.out.println(target.get(j));
				}
				if (target.size() == 4) //because there is no "reblogged from field" the op has a smaller list
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
					
					String reblogSource = split[1]; //name of person reblogged from
					ArrayList<String> rebloggedFrom = new ArrayList<String>();

					rebloggedFrom.add(split[1]);
					
					int reblogCount = 0; //this person was reblogged from at least once
					boolean append = true; //checks if we need to add the blog name to the end
					
					if (reblogSources.size() == 0) //I wonder if I can just say iter.hasNext here? Does it return false if there's nothing there to begin with?
					{
						reblogSources.put(reblogSource, 1); //first item so value is 1
					} //end if
					
					else
					{
						Set set = reblogSources.entrySet(); //what is set?
						Iterator iter = set.iterator();
						
						while (iter.hasNext())
						{
							Map.Entry keyValue = (Map.Entry)iter.next();
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
	
						//add the blog to the end with a value of one if it wasn't in the list
						if (append)
						{
							reblogSources.put(reblogSource, reblogCount); 
							System.out.println("Added " + reblogSource + " to the list.\n\n\n");
						} //end if
						
					} //end else
					
					//going to store everything in the reblog name array. Also need to do checks
					//if the name isn't in the list, add it to the end. If it is, 
				} //end else
			} //end for

			//is there a better kind of map to use?
			Map topSources = new LinkedHashMap(); //holds the sources of reblogs
			if (reblogSources.size() > 0)
			{
				Set set = reblogSources.entrySet();
				Iterator iter = set.iterator();
				
				while (iter.hasNext())
				{
					Map.Entry keyValue = (Map.Entry)iter.next();
					
					for (int i = 0; i < reblogSources.size(); i++)
					{
						
					} //end for
				} //end while
			} //end if

		} //end try
		
		
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		
		finally
		{
			client.close();
		}
	}
	
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
	
	//quick sort algorithm to sort both most popular person to reblog from and most popular tag
	//basically anything in this program which will be fed into a list which will need to be sorted will go through this
	private void quickSort(ArrayList<String> names, ArrayList<Integer> count, int start, int end)
	{
		int pivot = (end + 1), valueA, valueB;

		valueA = Integer.parseInt(count.get(start).toString()); //jesus just accessing these values is a pain.
		valueB = Integer.parseInt(count.get(pivot).toString()); //compare pivot and start values first
		
		int partition;
		
		pivot = Integer.parseInt(count.get(end).toString());
		partition = partition(names, count, start, end);
		quickSort(names, count, start, partition - 1);
		quickSort(names, count, partition + 1, end);
		
	} //end quickSort
	
	//preliminary 
	private int partition(ArrayList<String> names, ArrayList<Integer> count, int start, int end)
	{
		System.out.println(Integer.parseInt(count.get(0).toString()) + 2);
		System.out.println(names.get(0));
		
		//index locations we're checking each time
		int pivot = (end + 1), valueA, valueB;
		boolean keepGoing = true;

		System.out.println("HERE IS END AND PIVOT " + end + " " + pivot);
		
		//should this be strictly lesser than or <=?
		while (start < end)
		{
			System.out.println("start of loop: start and end " + start + " " + end);
			
			valueA = Integer.parseInt(count.get(start).toString()); //jesus just accessing these values is a pain.
			valueB = Integer.parseInt(count.get(pivot).toString()); //compare pivot and start values first
			
			//while the values on the left are smaller than the pivot, access the next element.
			//same goes for larger values, just access the previous element
			while (Integer.compare(valueA, valueB) < 0) //while a is less than b
			{
				System.out.println(Integer.parseInt(count.get(start).toString()) + " > " + Integer.parseInt(count.get(pivot).toString()));
				start++;
				valueA = Integer.parseInt(count.get(start).toString()); //move to the next value in the list for continued comparisons
			} //end while
			
			System.out.println("valueA is apparently not smaller than B. Here's A and B " + valueA + " " + valueB);
			System.out.println("start and end " + start + " " + end);
			
			valueA = Integer.parseInt(count.get(end).toString()); //we're now comparing the last values in the array
			while (Integer.compare(valueA, valueB) > 0) //while b is less than a
			{
				System.out.println(Integer.parseInt(count.get(end).toString()) + " > " + Integer.parseInt(count.get(pivot).toString()));
				end--;
				valueA = Integer.parseInt(count.get(end).toString());
			} //end while
			
			System.out.println("start and end " + start + " " + end);

			valueA = Integer.parseInt(count.get(start).toString());
			valueB = Integer.parseInt(count.get(end).toString());
			int valueC = Integer.parseInt(count.get(pivot).toString());
			//swap the values if they're in the wrong position based on relative size
			if (Integer.compare(valueA, valueB) > 0)
			{
				System.out.println("names size " + names.size());
				for (int i = 0; i < names.size(); i++)
				{
					System.out.println(count.get(i).toString());
				}
				System.out.println("BEFORE SWAP");
				swap(start, end, names, count);
				System.out.println("AFTER SWAP");
				for (int i = 0; i < names.size(); i++)
				{
					System.out.println(count.get(i).toString());
				}
				start++;
				end--;
			} //end if

			System.out.println("end of loop");
			for (int i = 0; i < names.size(); i++)
			{
				System.out.println(count.get(i).toString());
			}
		} //end while
		end++;
		
		valueA = Integer.parseInt(count.get(start).toString());
		valueB = Integer.parseInt(count.get(end).toString());
		int valueC = Integer.parseInt(count.get(pivot).toString());
		
		System.out.println("Here are start and end and pivot " + valueA + " " + valueB + " " + valueC);
		
		//only swap the values if either of them are larger: this is because the list is already split in half
		//so honestly you only really need to switch the values if valueA is larger than valueC right?
		//maybe look at this again later.
	    if (Integer.compare(valueA, valueC) >= 0)
		{
			System.out.println("start is greater than pivot " + valueA + " " + valueC);
			swap(start, pivot, names, count);
		} //end else if
		
		else if (Integer.compare(valueB, valueC) >= 0) //otherwise we're at the end and last is large
		{
			System.out.println("end is greater than pivot " + valueB + " " + valueC);
			swap(end, pivot, names, count);
		} //end else
	    
	    return end;
	} //end quickSort
	
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
	
	private void run()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("notes_info", "True");
		List <Post> posts = blog.posts(map);
		//Post newest = posts.get(4); //put the index of whatever post you want in here
		
		Post newest = blog.getPost(154931927092L);
		List <LinkedHashMap> popularTags = filterTags(newest);
		//System.out.println(popularTags.get(0).get("a"));
		
		//System.out.println(blog.getPostCount());
		//System.out.println(posts.get(0).getId());
		//System.out.println(posts.get(posts.size()).getId());
/*		for (Post post : blog.posts())
		{
			
		} //end for
*/
/*
		List myList = new ArrayList();
		myList.add("Hello");
		myList.add("How");
		myList.add("Are");
		myList.add("You");
		
		List l = new ArrayList();
		l.add("Hello");
		l.add("I'm");
		l.add("Good");
		l.add("And");
		l.add("you");
		myList.retainAll(l);
		for (int i = 0; i < myList.size(); i++)
		{
			System.out.println("myList: " + myList.get(i));
			System.out.println("l: " + l.get(i));
		} //end for
		
*/
	} //end Run
	
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
					reblogger = client.blogInfo(reblogString);
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
	
	//there is an issue where certain blogs, for no discernible reason (other than probably shitty tumblr programming)
	//will just redirect to that view where you're on the dashboard but the blog just sort of appears in the right side and you have to browse
	//in this mini view. My program crashes when it hits that because it can't properly read in tags from that url.
	//so this method is the fix. It reads in each url and checks the response code: if it starts with a 3, it is a redirect, so
	//just ditch that blog. This is a flaw in the program for obvious reasons: I am not counting tags which are used, kind of undermining
	//the point of the program. But I don't know a workaround for this mostly undocumented problem.
	private boolean checkURL(String url)
	{
		boolean result = true;
	
		//in the filterTags function, I remove the http:// and the end / because that's the format required by jumblr, so just add it back in
		url = "http://" + url + "/";
		
		try
		{
			URL testURL = new URL(url);
			HttpURLConnection con = (HttpURLConnection) testURL.openConnection();

			int response = con.getResponseCode();
			
			//I'm just doing this as a placeholder, really. I should probably just be checking that the response code starts with 2
			if ((299 < response) && (response < 400))
			{
				result = false;
			} //end if
			
			else
			{
				result = true;
			} //end else
		} //end try
		
		catch (Exception e)
		{
			System.out.println("There was an error checking the url.");
			result = false;
		} //end catch
		
		return result;
	} //end checkURL
} //end Monitor
