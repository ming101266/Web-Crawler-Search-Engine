/*
 * Starting: cd engine
 * Build: mvn clean install
 * Run: mvn exec:java -Dexec.mainClass="com.crawlerengine.CrawlerGUI"
 */

package com.crawlerengine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;

public class CrawlerGUI extends JFrame {
    private WebCrawler crawler; // WebCrawler instance
    private JEditorPane resultArea; // Area to display search results
    private JTextField queryField; // Text field for user input

    public CrawlerGUI(String startLink) {
        crawler = new WebCrawler(startLink); // Create a WebCrawler instance
        crawler.crawl(1, startLink); // Start crawling

        // Set up the frame
        setTitle("Web Crawler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Query input panel
        JPanel inputPanel = new JPanel();
        queryField = new JTextField(30);
        JButton searchButton = new JButton("Search");
        JButton quitButton = new JButton("Quit");

        inputPanel.add(queryField);
        inputPanel.add(searchButton);
        inputPanel.add(quitButton);

        // Result area using JEditorPane to support HTML
        resultArea = new JEditorPane();
        resultArea.setEditable(false);
        resultArea.setContentType("text/html");
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button action listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = queryField.getText();
                if (!query.trim().isEmpty()) {
                    List<String> searchResults = crawler.search(query);
                    displayResults(searchResults, query);
                    queryField.setText(""); // Clear the input field
                } else {
                    JOptionPane.showMessageDialog(CrawlerGUI.this, "Please enter a search query.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Quit the application
            }
        });

        // Mouse listener for clickable links
        resultArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                try {
                    // Get the clicked text position
                    int pos = resultArea.viewToModel2D(new Point(x, y));
                    String url = getLinkAtPosition(pos);
                    if (url != null) {
                        Desktop.getDesktop().browse(new URI(url)); // Open link in browser
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // Get the link at the clicked position
    private String getLinkAtPosition(int pos) {
        try {
            String htmlText = resultArea.getText();
            int start = htmlText.indexOf("<a href=\"", pos);
            int end = htmlText.indexOf("\"", start);
            if (end != -1) {
                return htmlText.substring(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Display search results in the text area
    private void displayResults(List<String> searchResults, String query) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body style='font-family: Arial, sans-serif;'>");
        htmlBuilder.append("<h3>Search results for \"").append(query).append("\":</h3>");
        if (searchResults.isEmpty()) {
            htmlBuilder.append("No results found.");
        } else {
            for (String url : searchResults) {
                htmlBuilder.append("<div style='margin-bottom: 10px;'><a href=\"")
                    .append(url).append("\" style='color: blue; text-decoration: underline;'>")
                    .append(url).append("</a></div>");
            }
        }
        htmlBuilder.append("</body></html>");
        resultArea.setText(htmlBuilder.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CrawlerGUI gui = new CrawlerGUI("https://courses.cs.washington.edu/courses/cse123/24au/");
            gui.setVisible(true); // Make the GUI visible
        });
    }
}
