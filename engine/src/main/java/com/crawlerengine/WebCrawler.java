package com.crawlerengine;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebCrawler {

    private static final int MAX_DEPTH = 10;
    private static final int MAX_THREADS = 5; // Number of threads to use
    private static final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    private String startLink;
    private Set<String> visitedLinks = ConcurrentHashMap.newKeySet();
    private Map<String, String> crawledPages = new ConcurrentHashMap<>();

    public WebCrawler(String link) {
        startLink = link;
    }

    public void crawl(int level, String url) {
        if (level <= MAX_DEPTH) {
            executorService.submit(() -> {
                Document doc = request(url);
                if (doc != null) {
                    for (Element link : doc.select("a[href]")) {
                        String nextLink = link.absUrl("href");

                        if (nextLink.startsWith(startLink) && !visitedLinks.contains(nextLink)) {
                            visitedLinks.add(nextLink);
                            crawl(level + 1, nextLink); // Crawl next link
                        }
                    }
                }
            });
        }
    }

    private Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {
                System.out.printf("Crawled: %s\n", url);
                String content = doc.text();
                crawledPages.put(url, content); // Store the page content
                return doc;
            }
            return null;
        } catch (IOException e) {
            System.err.println("Error fetching the page: " + url);
            return null;
        }
    }

    // Search function to find relevant pages based on a query
    public List<String> search(String query) {
        Map<String, Integer> relevanceMap = new HashMap<>();

        // Calculate relevance based on term frequency
        for (Map.Entry<String, String> entry : crawledPages.entrySet()) {
            String url = entry.getKey();
            String content = entry.getValue();

            int occurrences = countOccurrences(content, query);
            if (occurrences > 0) {
                relevanceMap.put(url, occurrences);
            }
        }

        // Sort URLs by relevance (occurrences of the search term)
        return relevanceMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Helper function to count occurrences of a word in the content
    private int countOccurrences(String content, String word) {
        int count = 0;
        String[] words = content.split("\\s+");
        for (String w : words) {
            if (w.equalsIgnoreCase(word)) {
                count++;
            }
        }
        return count;
    }
}
