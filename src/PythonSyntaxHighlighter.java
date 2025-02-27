import java.io.*;
import java.util.*;

public class PythonSyntaxHighlighter {
    // ANSI color codes for terminal output
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java PythonSyntaxHighlighter <filename>");
            return;
        }
        
        String filename = args[0];
        try {
            highlightFile(filename);
        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void highlightFile(String filename) throws Exception {
        // Read the entire file into a string
        String content = readFile(filename);
        
        // Process and highlight the content
        String highlighted = highlightContent(content);
        
        // Output the highlighted content
        System.out.println(highlighted);
    }
    
    private static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private static String highlightContent(String content) throws Exception {
        StringBuilder result = new StringBuilder();
        StringReader stringReader = new StringReader(content);
        
        // Create the lexer
        PythonReservedWordsValidator lexer = new PythonReservedWordsValidator(stringReader);
        
        // Tokenize the input
        String[] words = content.split("(\\s+|(?=[\\p{Punct}])|(?<=[\\p{Punct}]))");
        
        for (String word : words) {
            if (word.trim().isEmpty()) {
                // Preserve whitespace
                result.append(word);
                continue;
            }
            
            // Reset the lexer with the current word
            lexer.yyreset(new StringReader(word));
            String status = lexer.yylex();
            
            // Highlight invalid words in red
            if ("invalid".equals(status)) {
                result.append(ANSI_RED).append(word).append(ANSI_RESET);
            } else {
                result.append(word);
            }
        }
        
        return result.toString();
    }
    
    // HTML version for output to a file instead of terminal
    public static void highlightToHtml(String inputFile, String outputFile) throws Exception {
        String content = readFile(inputFile);
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>Python Syntax Highlighting</title>\n");
        html.append("<style>\n");
        html.append(".invalid { color: red; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        html.append("<pre>\n");
        
        StringReader stringReader = new StringReader(content);
        PythonReservedWordsValidator lexer = new PythonReservedWordsValidator(stringReader);
        
        String[] words = content.split("(\\s+|(?=[\\p{Punct}])|(?<=[\\p{Punct}]))");
        
        for (String word : words) {
            if (word.trim().isEmpty()) {
                html.append(word);
                continue;
            }
            
            lexer.yyreset(new StringReader(word));
            String status = lexer.yylex();
            
            if ("invalid".equals(status)) {
                html.append("<span class=\"invalid\">").append(escapeHtml(word)).append("</span>");
            } else {
                html.append(escapeHtml(word));
            }
        }
        
        html.append("</pre>\n");
        html.append("</body>\n</html>");
        
        // Write the HTML output
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.print(html.toString());
        }
    }
    
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;");
    }
}
