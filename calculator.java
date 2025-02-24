import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.lang.Math;

public class calculator extends JFrame implements ActionListener {
    private JTextField display;
    private String currentInput = "";
    private double result = 0;
    private String operator = "";

    private JPanel scientificPanel;
    private JPanel historyPanel; // Panel for history
    private boolean isScientificVisible = false;
    private boolean isHistoryVisible = false; // Track visibility of history panel
    private DefaultListModel<String> historyModel; // Model to store history

    // Constructor for setting up the GUI
    public calculator() {
        setTitle("Scientific Calculator");
        setSize(500, 700);
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
                "C", "DEL", "SCI", "HIS"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(this);

            // Set background and text color
            button.setBackground(new Color(63, 63, 63));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);

            // Highlight '=' and special buttons in red
            if (text.equals("=") || text.equals("SCI") || text.equals("HIS")) {
                button.setBackground(Color.RED);
            }

            mainPanel.add(button);
        }

        // Scientific Buttons Panel
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
            button.setBackground(new Color(63, 63, 63));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
            scientificPanel.add(button);
        }

        scientificPanel.setVisible(false);

        // History Panel
        historyModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(historyModel);
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("History"), BorderLayout.NORTH);
        historyPanel.add(new JScrollPane(historyList), BorderLayout.CENTER);
        historyPanel.setVisible(false);

        // Container Panel to add vertical gap
        JPanel panelContainer = new JPanel();
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));
        panelContainer.add(mainPanel);
        panelContainer.add(Box.createVerticalStrut(20));
        panelContainer.add(scientificPanel);
        panelContainer.add(historyPanel);

        add(panelContainer, BorderLayout.CENTER);

        setVisible(true);
    }

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
            case "HIS":
                toggleHistory();
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
            historyModel.addElement(currentInput + " " + operator + " " + secondOperand + " = " + result);
            currentInput = String.valueOf(result);
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }

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
            historyModel.addElement(func + "(" + value + ") = " + result);
            currentInput = String.valueOf(result);
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }

    private void toggleScientific() {
        isScientificVisible = !isScientificVisible;
        scientificPanel.setVisible(isScientificVisible);
        revalidate();
        repaint();
    }

    private void toggleHistory() {
        isHistoryVisible = !isHistoryVisible;
        historyPanel.setVisible(isHistoryVisible);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(calculator::new);
    }
}