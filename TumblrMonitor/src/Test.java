import java.io.IOException;

import org.jsoup.*;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//testing space, obviously

public class Test 
{
	HtmlToPlainText test = new HtmlToPlainText();
} //end Test

//here's some old test code I had to throw away because Jsoup doesn't know how to handle javascript
/*


try 
{
	Document doc = Jsoup.connect("http://revolverlolicon.tumblr.com/post/155154047636#notes").get();
	System.out.println(doc.title());
	
	//this performs a "css-like query". I'm not sure why it's called that.
	//recall that "a" denotes an anchor tag, it defines a hyperlink. href denotes the link's destination
	Elements links = doc.select("a[href]");
	for (Element link : links)
	{
		System.out.println("\nlink: " + link.attr("href"));
		System.out.println("text: " + link.text());
	}
} 

catch (IOException e)
{
	e.printStackTrace();
}
*/