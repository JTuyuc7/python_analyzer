import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Arrays;

public class Main {

    private JFrame frame;
    private JTextPane outputArea;
    private JButton loadButton;
    private File selectedFile;
    private List<String> validNumberes;

    public Main(){
        initialize();
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

// Define styles with Python IDLE-like colors
            Style defaultStyle = outputArea.addStyle("default", null);
            StyleConstants.setForeground(defaultStyle, new Color(0, 0, 0));

            // Keywords (orange like Python)
            Style keywordStyle = outputArea.addStyle("keyword", null);
            StyleConstants.setForeground(keywordStyle, new Color(255, 119, 0));
            StyleConstants.setBold(keywordStyle, true);

            // Control flow keywords (same as keywords)
            Style controlStyle = outputArea.addStyle("control", null);
            StyleConstants.setForeground(controlStyle, new Color(255, 119, 0));
            StyleConstants.setBold(controlStyle, true);

            // Identifiers (black, default color)
            Style identifierStyle = outputArea.addStyle("identifier", null);
            StyleConstants.setForeground(identifierStyle, new Color(0, 0, 0));

            // Numbers (blue like Python)
            Style numberStyle = outputArea.addStyle("number", null);
            StyleConstants.setForeground(numberStyle, new Color(0, 0, 255));

            // Strings (green like Python)
            Style stringStyle = outputArea.addStyle("string", null);
            StyleConstants.setForeground(stringStyle, new Color(0, 128, 0));

            // Operators (black like Python)
            Style operatorStyle = outputArea.addStyle("operator", null);
            StyleConstants.setForeground(operatorStyle, new Color(0, 0, 0));

            // Comments (red like Python)
            Style commentStyle = outputArea.addStyle("comment", null);
            StyleConstants.setForeground(commentStyle, new Color(200, 0, 0));
            StyleConstants.setItalic(commentStyle, true);

            // Decorators (blue like Python)
            Style decoratorStyle = outputArea.addStyle("decorator", null);
            StyleConstants.setForeground(decoratorStyle, new Color(0, 0, 255));

            // Built-in functions and constants (purple like Python)
            Style builtinStyle = outputArea.addStyle("builtin", null);
            StyleConstants.setForeground(builtinStyle, new Color(160, 32, 240));

            // Class names (dark blue)
            Style classStyle = outputArea.addStyle("class", null);
            StyleConstants.setForeground(classStyle, new Color(0, 0, 128));
            StyleConstants.setBold(classStyle, true);

            // Function names (dark green)
            Style functionStyle = outputArea.addStyle("function", null);
            StyleConstants.setForeground(functionStyle, new Color(0, 128, 0));
            StyleConstants.setBold(functionStyle, true);

            // Error highlighting
            Style errorStyle = outputArea.addStyle("error", null);
            StyleConstants.setForeground(errorStyle, new Color(204, 0, 0));
            StyleConstants.setBackground(errorStyle, new Color(255, 200, 200));

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
                    // Control flow keywords
                    case "IF": case "ELSE": case "ELIF": case "FOR": case "WHILE":
                    case "TRY": case "EXCEPT": case "FINALLY": case "BREAK": case "CONTINUE":
                        tokenStyle = controlStyle;
                        break;
                    // Definition keywords
                    case "DEF":
                        tokenStyle = functionStyle;
                        break;
                    case "CLASS":
                        tokenStyle = classStyle;
                        break;
                    case "LAMBDA":
                        tokenStyle = keywordStyle;
                        break;
                    // Standard keywords
                    case "AND": case "AS": case "ASSERT": case "DEL": case "FROM":
                    case "GLOBAL": case "IMPORT": case "IN": case "IS":
                    case "NONLOCAL": case "NOT": case "OR": case "PASS": case "RAISE":
                    case "RETURN": case "WITH": case "YIELD":
                        tokenStyle = keywordStyle;
                        break;
                    case "TRUE": case "FALSE": case "NONE":
                        tokenStyle = builtinStyle;
                        break;
                    case "IDENTIFIER":
                        // Check if it's a built-in function
                        if (isBuiltInFunction(token.getLexeme())) {
                            tokenStyle = builtinStyle;
                        } else {
                            tokenStyle = identifierStyle;
                        }
                        break;
                    case "INTEGER_LITERAL": case "FLOAT_LITERAL": case "SCIENTIFIC_LITERAL":
                        tokenStyle = numberStyle;
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

    private boolean isBuiltInFunction(String identifier) {
        String[] builtIns = {
            "abs", "all", "any", "ascii", "bin", "bool", "bytearray", "bytes", "callable", "chr",
            "classmethod", "compile", "complex", "delattr", "dict", "dir", "divmod", "enumerate",
            "eval", "exec", "filter", "float", "format", "frozenset", "getattr", "globals", "hasattr",
            "hash", "help", "hex", "id", "input", "int", "isinstance", "issubclass", "iter", "len",
            "list", "locals", "map", "max", "memoryview", "min", "next", "object", "oct", "open",
            "ord", "pow", "print", "property", "range", "repr", "reversed", "round", "set", "setattr",
            "slice", "sorted", "staticmethod", "str", "sum", "super", "tuple", "type", "vars", "zip"
        };
        return Arrays.asList(builtIns).contains(identifier);
    }
}
