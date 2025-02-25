import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

public class calculator extends JFrame implements ActionListener {
    private JTextField display;
    private String currentInput = "";
    private double result = 0;
    private String operator = "";

    private JPanel scientificPanel;
    private boolean isScientificVisible = false;
    private LinkedList<String> history;
    private JPanel historyPanel;
    private JTextArea historyArea;

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
                currentInput = String.valueOf(result); // Set currentInput only after "="
                display.setText(String.valueOf(result));
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
                if (!operator.isEmpty()) {
                    calculate();
                } else {
                    result = Double.parseDouble(currentInput);
                }
                operator = command;
                currentInput = "";
                display.setText(String.valueOf(result)); //display the result of the previous operation.
            }
            break;
        default:
            currentInput += command;
            display.setText(currentInput);
            break;
        }
    }

    private void calculate() {
        try  {
            if (!currentInput.isEmpty() && !operator.isEmpty()) {

                double secondOperand = Double.parseDouble(currentInput);
                String fullOperation = result + " " + operator + " " + secondOperand;
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
                String calculation = fullOperation + " = " + result;
                if (history.size() == 20) {
                    history.removeFirst();
                }
                history.add(calculation);
                display.setText(calculation);
                currentInput = String.valueOf(result);
                updateHistory();
            }
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
