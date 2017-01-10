# TumblrNoteMonitor
Monitors important "note" information from Tumblr for my girlfriend.

I still have a lot of work to do on this one, most of my time with this has been spent researching and testing out different solutions, many of which did not work. I still have not gotten to the meat of the program due to some issues explained below.

##Intended Usage
My girlfriend is an artist who frequently posts her art on her blog on tumblr.com. She often spends a lot of time going through "reblogs" to see what people say in their "tags" (similar to twitter hashtags). On tumblr, "reblogging" is how users share posts with others, so this should hopefully provide some useful analytics for her to more carefully plan her posts and maximize exposure. This will be a desktop application which allows the user to see different information about a post, including the following:
1. Top 20 most popular "tags" used when reblogging
2. Most popular time to reblog
3. Who was reblogged from the most
4. Direct replies to the post

##General Info
This program will be written in Java. It uses Jumblr for access to the Tumblr API. I'm currently using HTMLUnit for web scraping. I was originally using JSoup for this, but JSoup is unable to handle Javascript. Because additional notes are only accessible through a dynamically loaded "load more" button, I was unable to use this.

##Issues
I was originally using Tumblr's API to pull my information primarily, but as it turns out Tumblr's API is a bit barebones. It only allows you to pull the 50 most recent reblogs. However, the user's posts often get hundreds of notes, with a few having over a thousand, so this wouldn't be very valuable information. For this reason I'm currently looking into web scraping, and have some basic testing implementation of this in the code already. It may take me a bit, as I didn't know anything about web scraping before this project but this is meant to be a learning experience anyway. I don't believe that this will effect tumblr's servers in any even marginal way as this program will only have one user and she won't be using it more than a few times following each post.

One other problem raised by me choosing to web scrape is that the fundamental libraries involved aren't supported by Android, so I'll no longer be able to port this as I originally intended.
