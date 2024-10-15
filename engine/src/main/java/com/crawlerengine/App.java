/*
 * Starting: cd engine
 * Build: mvn clean install
 * Run: mvn exec:java -Dexec.mainClass="com.crawlerengine.App"
 */

 package com.crawlerengine;

 import java.util.*;
 
 public class App {
     public static void main(String[] args) {
         WebCrawler crawler = new WebCrawler("https://courses.cs.washington.edu/courses/cse123/24au/");
         crawler.crawl(1, "https://courses.cs.washington.edu/courses/cse123/24au/"); // Start the crawling process
 
         Scanner scanner = new Scanner(System.in);
         String query;
 
         // Continuously accept search queries until the user types "quit"
         while (true) {
             System.out.print("Enter a search query (or type 'quit' to exit): ");
             query = scanner.nextLine();
 
             if ("quit".equalsIgnoreCase(query)) {
                 break; // Exit the loop if the user types "quit"
             }
 
             List<String> searchResults = crawler.search(query);
             System.out.println("\nSearch results for \"" + query + "\":");
             if (searchResults.isEmpty()) {
                 System.out.println("No results found.");
             } else {
                int cnt = 10;
                 for (String url : searchResults) {
                     System.out.println(url);
                     cnt--;
                     if (cnt<=0) break;
                 }
             }
         }
 
         scanner.close(); // Close the scanner when done
         System.out.println("Exiting the search engine. Goodbye!");
     }
 }
 

