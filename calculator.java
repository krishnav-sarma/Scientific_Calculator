import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class calculator extends JFrame implements ActionListener {
    private JTextField display;
    private String currentInput = "";
    private LinkedList<String> history;
    private JPanel historyPanel;
    private JTextArea historyArea;
    private JPanel scientificPanel;
    private boolean isScientificVisible = false;

    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.setColor(getForeground());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    public calculator() {
        setTitle("Scientific Calculator");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        add(display, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 4, 10, 10));

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", "DEL", "SCI", "HIS"
        };

        for (String text : buttons) {
            RoundedButton button = new RoundedButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(this);
            button.setBackground(new Color(63, 63, 63));
            button.setForeground(Color.WHITE);

            if (text.equals("=") || text.equals("SCI")) {
                button.setBackground(Color.RED);
            }
            mainPanel.add(button);
        }

        scientificPanel = new JPanel();
        scientificPanel.setLayout(new GridLayout(2, 4, 10, 10));

        String[] sciButtons = {
                "sin", "cos", "tan", "√",
                "x^2", "log", "π", "e"
        };

        for (String text : sciButtons) {
            RoundedButton button = new RoundedButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.addActionListener(this);
            button.setBackground(new Color(63, 63, 63));
            button.setForeground(Color.WHITE);
            scientificPanel.add(button);
        }

        scientificPanel.setVisible(false);

        JPanel panelContainer = new JPanel();
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));
        panelContainer.add(mainPanel);
        panelContainer.add(Box.createVerticalStrut(20));
        panelContainer.add(scientificPanel);

        add(panelContainer, BorderLayout.CENTER);

        history = new LinkedList<>();
        historyPanel = new JPanel(new BorderLayout());
        historyArea = new JTextArea(10, 20);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        historyPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        historyPanel.setVisible(false);
        add(historyPanel, BorderLayout.EAST);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "C":
                currentInput = "";
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
                currentInput = display.getText();
                break;
            case "SCI":
                toggleScientific();
                break;
            case "HIS":
                toggleHistory();
                break;
            default:
                currentInput += command;
                display.setText(currentInput);
                break;
        }
    }

    private void calculate() {
        try {
            List<String> tokens = tokenize(display.getText());
            double result = evaluate(tokens);
            String calculation = display.getText() + " = " + result;
            if (history.size() == 20) {
                history.removeFirst();
            }
            history.add(calculation);
            display.setText(String.valueOf(result));
            updateHistory();
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();
        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                currentNumber.append(c);
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {
                if (currentNumber.length() > 0) {
                    tokens.add(currentNumber.toString());
                    currentNumber.setLength(0);
                }
                tokens.add(String.valueOf(c));
            }
        }
        if (currentNumber.length() > 0) {
            tokens.add(currentNumber.toString());
        }
        return tokens;
    }

    private double evaluate(List<String> tokens) {
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();
        for (String token : tokens) {
            if (isNumber(token)) {
                values.push(Double.parseDouble(token));
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.peek().equals("(")) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Remove "("
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && hasPrecedence(token, operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(token);
            }
        }
        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private boolean hasPrecedence(String op1, String op2) {
        if (op2.equals("(") || op2.equals(")")) {
            return false;
        }
        if ((op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"))) {
            return false;
        }
        return true;
    }

    private double applyOperator(String operator, double b, double a) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }

    private void toggleScientific() {
        isScientificVisible = !isScientificVisible;
        scientificPanel.setVisible(isScientificVisible);
        revalidate();
        repaint();
    }

    private void toggleHistory() {
        historyPanel.setVisible(!historyPanel.isVisible());
        revalidate();
        repaint();
    }

    private void updateHistory() {
        historyArea.setText(String.join("\n", history));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(calculator::new);
    }
}
