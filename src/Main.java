import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class Main {

    private JFrame frame;
    private JTextPane outputArea;
    private JButton loadButton;
    private File selectedFile;
    private List<String> validNumberes;

    public Main(){
        initialize();
    }

    public static void main2(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java PythonAnalyzer <input-file>");
            System.exit(1);
        }

        try {
            // Create a reader for the input file
            Reader reader = new FileReader(args[0]);

            // Create the lexer
            PythonLexer lexer = new PythonLexer(reader);

            // Analyze the input
            Token token;
            boolean hasErrors = false;

            while (true) {
                token = lexer.yylex();

                if (token == null) {
                    break;
                }

                if (token.getType().equals("ERROR")) {
                    System.out.printf("Lexical error at line %d, column %d: Unexpected character '%s'%n",
                            token.getLine(), token.getColumn(), token.getLexeme());
                    hasErrors = true;
                } else {
                    // For debugging/verification, print all tokens
                    System.out.println(token);
                }
            }

            if (!hasErrors) {
                System.out.println("Lexical analysis completed successfully.");
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: Input file not found");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initialize(){
        frame = new JFrame();
        frame.setTitle("Python Analyzer");
        frame.setBounds(100, 100, 450, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        outputArea = new JTextPane();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        loadButton = new JButton("Seleccionar archivo");
        buttonPanel.add(loadButton);

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFileToAnalize();
            }
        });

        frame.setVisible(true);
    }

    private void selectFileToAnalize(){
        JFileChooser fileChoosen = new JFileChooser();
        int opt = fileChoosen.showOpenDialog(frame);
        if(opt == JFileChooser.APPROVE_OPTION){
            selectedFile = fileChoosen.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
            System.out.println(selectedFile.getName());
            System.out.println(selectedFile);
            processData(selectedFile);
        }
    }

    private void processData(File file) {
        try {
            // Read the entire file content
            String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            
            // Create a reader for the input file
            Reader reader = new StringReader(fileContent);

            // Create the lexer
            PythonLexer lexer = new PythonLexer(reader);

            // Analyze the input
            Token token;
            boolean hasErrors = false;

            // Clear previous output
            outputArea.setText("");
            StyledDocument doc = outputArea.getStyledDocument();

            // Define styles with enhanced colors
            Style defaultStyle = outputArea.addStyle("default", null);
            
            Style keywordStyle = outputArea.addStyle("keyword", null);
            StyleConstants.setForeground(keywordStyle, new Color(0, 0, 255)); // Blue
            StyleConstants.setBold(keywordStyle, true);
            
            Style identifierStyle = outputArea.addStyle("identifier", null);
            StyleConstants.setForeground(identifierStyle, new Color(0, 128, 0)); // Green
            
            Style literalStyle = outputArea.addStyle("literal", null);
            StyleConstants.setForeground(literalStyle, new Color(128, 0, 128)); // Purple
            
            Style stringStyle = outputArea.addStyle("string", null);
            StyleConstants.setForeground(stringStyle, new Color(200, 0, 0)); // Red
            
            Style operatorStyle = outputArea.addStyle("operator", null);
            StyleConstants.setForeground(operatorStyle, new Color(128, 128, 0)); // Olive
            
            Style commentStyle = outputArea.addStyle("comment", null);
            StyleConstants.setForeground(commentStyle, new Color(128, 128, 128)); // Gray
            StyleConstants.setItalic(commentStyle, true);
            
            Style errorStyle = outputArea.addStyle("error", null);
            StyleConstants.setForeground(errorStyle, Color.RED);
            StyleConstants.setBackground(errorStyle, new Color(255, 200, 200)); // Light red background

            int lastIndex = 0;

            while (true) {
                token = lexer.yylex();

                if (token == null) {
                    break;
                }

                // Find the token in the file content
                String tokenLexeme = token.getLexeme();
                int startIndex = fileContent.indexOf(tokenLexeme, lastIndex);
                int endIndex = startIndex + tokenLexeme.length();

                // Add any skipped text with default style
                if (startIndex > lastIndex) {
                    String skippedText = fileContent.substring(lastIndex, startIndex);
                    doc.insertString(doc.getLength(), skippedText, defaultStyle);
                }

                // Add token text with appropriate style
                Style tokenStyle;
                switch (token.getType()) {
                    case "AND": case "AS": case "ASSERT": case "BREAK": case "CLASS":
                    case "CONTINUE": case "DEF": case "DEL": case "ELIF": case "ELSE":
                    case "EXCEPT": case "FALSE": case "FINALLY": case "FOR": case "FROM":
                    case "GLOBAL": case "IF": case "IMPORT": case "IN": case "IS":
                    case "LAMBDA": case "NONE": case "NONLOCAL": case "NOT": case "OR":
                    case "PASS": case "RAISE": case "RETURN": case "TRUE": case "TRY":
                    case "WHILE": case "WITH": case "YIELD":
                        tokenStyle = keywordStyle;
                        break;
                    case "IDENTIFIER":
                        tokenStyle = identifierStyle;
                        break;
                    case "INTEGER_LITERAL": case "FLOAT_LITERAL": case "SCIENTIFIC_LITERAL":
                        tokenStyle = literalStyle;
                        break;
                    case "STRING_LITERAL":
                        tokenStyle = stringStyle;
                        break;
                    case "PLUS": case "MINUS": case "MULTIPLY": case "DIVIDE": case "INTEGER_DIVIDE":
                    case "MODULO": case "POWER": case "ASSIGN": case "EQUALS": case "NOT_EQUALS":
                    case "LESS": case "GREATER": case "LESS_EQUALS": case "GREATER_EQUALS":
                    case "LPAREN": case "RPAREN": case "LBRACKET": case "RBRACKET": case "LBRACE":
                    case "RBRACE": case "COMMA": case "DOT": case "COLON": case "SEMICOLON":
                        tokenStyle = operatorStyle;
                        break;
                    case "COMMENT":
                        tokenStyle = commentStyle;
                        break;
                    case "ERROR": case "INVALID_IDENTIFIER": case "INVALID_KEYWORD":
                        tokenStyle = errorStyle;
                        hasErrors = true;
                        break;
                    default:
                        tokenStyle = defaultStyle;
                }
                doc.insertString(doc.getLength(), tokenLexeme, tokenStyle);

                lastIndex = endIndex;
            }

            // Add any remaining text
            if (lastIndex < fileContent.length()) {
                doc.insertString(doc.getLength(), fileContent.substring(lastIndex), defaultStyle);
            }

            // Show analysis result
            Style resultStyle = outputArea.addStyle("result", null);
            StyleConstants.setBold(resultStyle, true);
            if (!hasErrors) {
                StyleConstants.setForeground(resultStyle, new Color(0, 128, 0)); // Green
                doc.insertString(doc.getLength(), "\n\nLexical analysis completed successfully.", resultStyle);
            } else {
                StyleConstants.setForeground(resultStyle, Color.RED);
                doc.insertString(doc.getLength(), "\n\nLexical analysis found errors.", resultStyle);
            }

            reader.close();

        } catch (FileNotFoundException e) {
            outputArea.setText("Error: Input file not found");
        } catch (IOException e) {
            outputArea.setText("Error reading input file: " + e.getMessage());
        } catch (Exception e) {
            outputArea.setText("Unexpected error: " + e.getMessage());
        }
    }

    private int countNewlines(String text) {
        return (int) text.chars().filter(ch -> ch == '\n').count();
    }

    private int getLastLineLength(String text) {
        int lastNewlineIndex = text.lastIndexOf('\n');
        return lastNewlineIndex == -1 ? text.length() : text.length() - lastNewlineIndex - 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
//        private static final String REGEX = "^[0-9]+(\\.[0-9]+)?E[+-]?[0-9]+$";
    }
}
