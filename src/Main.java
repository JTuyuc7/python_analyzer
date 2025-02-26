import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class Main {

    private JFrame frame;
    private JTextArea outputArea;
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

        outputArea = new JTextArea();
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

    private void processData(File file){
        try {
            // Create a reader for the input file
            Reader reader = new FileReader(file);

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
                    System.out.println(token.getLexeme() + "aca");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
//        private static final String REGEX = "^[0-9]+(\\.[0-9]+)?E[+-]?[0-9]+$";
    }
}