

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;

public class calculator extends JFrame implements ActionListener {
    private JTextField display;
    private String currentInput = "";
    private double result = 0;
    private String operator = "";

    private JPanel scientificPanel; // Panel for scientific buttons
    private boolean isScientificVisible = false; // Track visibility of scientific panel

    // Constructor for setting up the GUI
    public calculator() {
        setTitle("Scientific Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display Area
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        add(display, BorderLayout.NORTH);

        // Main Button Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 4, 10, 10));

        // Main Buttons
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", "DEL", "SCI", ""
        };



        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(this);

            // Set background and text color
            button.setBackground(new Color(63,63,63));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);



            // Highlight '=' button in yellow
            if (text.equals("=")) {
                button.setBackground(Color.RED);
                button.setOpaque(true);
                button.setBorderPainted(false);
            }
            if(text.equals("SCI")) {
                button.setBackground(Color.RED);
                button.setOpaque(true);
                button.setBorderPainted(false);

            }

            mainPanel.add(button);
        }

        // Scientific Buttons Panel (Initially Hidden)
        scientificPanel = new JPanel();
        scientificPanel.setLayout(new GridLayout(2, 4, 10, 10));

        String[] sciButtons = {
                "sin", "cos", "tan", "√",
                "x^2", "log", "π", "e"
        };

        for (String text : sciButtons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.addActionListener(this);
            button.setBackground(new Color(63,63,63));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
            scientificPanel.add(button);

        }

        scientificPanel.setVisible(false); // Hide initially

        // Container Panel to add vertical gap between panels
        JPanel panelContainer = new JPanel();
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS)); // Vertical stacking
        panelContainer.add(mainPanel);
        panelContainer.add(Box.createVerticalStrut(20)); // Add 20px vertical gap
        panelContainer.add(scientificPanel);

        add(panelContainer, BorderLayout.CENTER);

        setVisible(true);
    }

    // Handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "C":
                currentInput = "";
                result = 0;
                operator = "";
                display.setText("");
                break;
            case "DEL":
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    display.setText(currentInput);
                }
                break;
            case "=":
                calculate();
                operator = "";
                break;
            case "SCI":
                toggleScientific();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                if (!currentInput.isEmpty()) {
                    result = Double.parseDouble(currentInput);
                    operator = command;
                    currentInput = "";
                }
                break;
            case "sin":
            case "cos":
            case "tan":
            case "√":
            case "x^2":
            case "log":
            case "π":
            case "e":
                applyFunction(command);
                break;
            default:
                currentInput += command;
                display.setText(currentInput);
                break;
        }
    }

    // Perform calculation
    private void calculate() {
        try {
            double secondOperand = Double.parseDouble(currentInput);
            switch (operator) {
                case "+":
                    result += secondOperand;
                    break;
                case "-":
                    result -= secondOperand;
                    break;
                case "*":
                    result *= secondOperand;
                    break;
                case "/":
                    result /= secondOperand;
                    break;
            }
            display.setText(String.valueOf(result));
            currentInput = String.valueOf(result);
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }

    // Apply scientific functions
    private void applyFunction(String func) {
        try {
            double value = Double.parseDouble(currentInput);
            switch (func) {
                case "sin":
                    result = Math.sin(Math.toRadians(value));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(value));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(value));
                    break;
                case "√":
                    result = Math.sqrt(value);
                    break;
                case "x^2":
                    result = Math.pow(value, 2);
                    break;
                case "log":
                    result = Math.log10(value);
                    break;
                case "π":
                    result = Math.PI;
                    break;
                case "e":
                    result = Math.E;
                    break;
            }
            display.setText(String.valueOf(result));
            currentInput = String.valueOf(result);
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }

    // Toggle the visibility of scientific buttons
    private void toggleScientific() {
        isScientificVisible = !isScientificVisible;
        scientificPanel.setVisible(isScientificVisible);
        revalidate(); // Refresh the layout
        repaint();
    }

    // Main method to run the calculator
    public static void main(String[] args) {
        SwingUtilities.invokeLater(calculator::new);
    }
}