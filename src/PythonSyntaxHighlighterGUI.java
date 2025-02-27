import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class PythonSyntaxHighlighterGUI extends JFrame {
    private JTextPane textPane;
    private JButton openButton;
    private JButton highlightButton;
    private File currentFile;
    
    public PythonSyntaxHighlighterGUI() {
        setTitle("Python Syntax Highlighter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create components
        textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        
        openButton = new JButton("Open File");
        highlightButton = new JButton("Highlight");
        highlightButton.setEnabled(false);
        
        // Layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(highlightButton);
        
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        // Event handling
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        
        highlightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                highlightContent();
            }
        });
    }
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                // Load file content into text pane
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                
                textPane.setText(content.toString());
                highlightButton.setEnabled(true);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error reading file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void highlightContent() {
        try {
            String content = textPane.getText();
            StyledDocument doc = textPane.getStyledDocument();
            
            // Create styles
            Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            
            Style invalidStyle = textPane.addStyle("invalid", defaultStyle);
            StyleConstants.setForeground(invalidStyle, Color.RED);
            
            // Reset all styling
            doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);
            
            // Create a lexer
            PythonReservedWordsValidator lexer = new PythonReservedWordsValidator(new StringReader(""));
            
            // Process content word by word
            String[] lines = content.split("\n");
            int offset = 0;
            
            for (String line : lines) {
                String[] words = line.split("(\\s+|(?=[\\p{Punct}])|(?<=[\\p{Punct}]))");
                for (String word : words) {
                    if (word.trim().isEmpty()) {
                        offset += word.length();
                        continue;
                    }
                    
                    lexer.yyreset(new StringReader(word));
                    String status = lexer.yylex();
                    
                    if ("invalid".equals(status)) {
                        doc.setCharacterAttributes(offset, word.length(), invalidStyle, true);
                    }
                    offset += word.length();
                }
                offset += 1; // For the newline
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error highlighting content: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PythonSyntaxHighlighterGUI().setVisible(true);
            }
        });
    }
}
