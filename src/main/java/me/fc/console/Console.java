package me.fc.console;

import me.fc.console.command.CommandManager;
import me.fc.console.files.ConfigFile;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Console {

    private List<String> recentUsed = new ArrayList<String>();
    private int recentUsedId = 0;
    private int recentUsedMax = 10;
    private JTextField input;
    private JFrame frame;
    private SpringLayout springLayout;
    private JScrollPane scrollPane;
    private JTextPane jTextPane;
    private StyledDocument document;
    private Logger logger = Logger.getLogger("FCConsole");

    private CommandManager commandManager;

    private Color backgroundColor = new Color(50, 50, 50);

    private ConfigFile settings;

    private JMenuBar menuBar = new JMenuBar();

    public Console(String consoleName) {
        setupSettings();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            logger.severe(e.getMessage());
        }

        frame = new JFrame();
        addMenuBar();
        springLayout = new SpringLayout();
        frame.setLayout(springLayout);
        frame.setTitle(consoleName);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                onWindowClose(e);
            }
        });

        jTextPane = new JTextPane();
        jTextPane.setEditable(false);
        jTextPane.setFont(new Font("Courier New", Font.PLAIN, 12));
        jTextPane.setOpaque(false);

        document = jTextPane.getStyledDocument();

        scrollPane = new JScrollPane(jTextPane);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());

        input = new JTextField();
        input.setFont(new Font("Courier New", Font.PLAIN, 12));
        input.setForeground(Color.GREEN);
        input.setCaretColor(Color.GREEN);
        input.setOpaque(false);

        input.addActionListener(e -> {
            String text = input.getText();
            onMessageSend(text);
        });

        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { // This method is not used because we don't have a use for it.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int history = 0;
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    input.setText(recentUsed.get(recentUsedId));
                    if (recentUsedId > 0) recentUsedId--;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    input.setText(recentUsed.get(recentUsedId));
                    if (recentUsedId < recentUsed.size() - 1) {
                        recentUsedId++;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // We do not have a need for this method.
            }
        });

        springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.NORTH, frame.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -5, SpringLayout.NORTH, input);
        springLayout.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, frame.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, frame.getContentPane());

        springLayout.putConstraint(SpringLayout.SOUTH, input, -5, SpringLayout.SOUTH, frame.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, input, -5, SpringLayout.EAST, frame.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, input, 5, SpringLayout.WEST, frame.getContentPane());

        frame.add(input);
        frame.add(scrollPane);

        frame.getContentPane().setBackground(settings.getColor("backgroundColor"));

        frame.setSize(660, 350);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        commandManager = new CommandManager(this);
    }

    private void setupSettings() {
        settings = new ConfigFile("settings");
        if (!settings.exists()) {
            settings.setColor("backgroundColor", backgroundColor);
        }
    }

    public void addMenuBar() {
        JMenu settings = new JMenu("Settings");

        JMenuItem choose_background_color = new JMenuItem("Choose Background Color");
        choose_background_color.addActionListener((ActionEvent event) -> {
            backgroundColor = JColorChooser.showDialog(null, "Choose Background Color", Color.CYAN);
            this.settings.setColor("backgroundColor", backgroundColor);
            frame.getContentPane().setBackground(this.settings.getColor("backgroundColor"));
        });

        settings.add(choose_background_color);
        menuBar.add(settings);

        frame.setJMenuBar(menuBar);
    }

    public void addMenu(JMenu menu) {
        menuBar.add(menu);
    }

    public void onWindowClose(WindowEvent e) {
        System.exit(0);
    }

    private void changeTextColor(Color color) {
        try {
            String text = document.getText(0, document.getLength());
            clearConsole();
            print(text, false, color);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void onMessageSend(String msg) {
        if (msg.length() > 0) {
            if (recentUsed.isEmpty() || !recentUsed.get(recentUsed.size() - 1).equalsIgnoreCase(msg))
                recentUsed.add(msg);
            recentUsedId = recentUsed.size() - 1;
            proccessText(msg);
            input.setText("");
        }
    }

    public List<String> getRecentUsed() {
        return recentUsed;
    }

    public void setRecentUsedId(int recentUsedId) {
        this.recentUsedId = recentUsedId;
    }

    public JTextField getInput() {
        return input;
    }

    public void print(String s, boolean trace) {
        print(s, trace, Color.WHITE);
    }

    public void print(String s, boolean trace, Color c) {
        Style style = jTextPane.addStyle("Style", null);
        StyleConstants.setForeground(style, c);

        if (trace) {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();
            String caller = elements[0].getClassName();

            s = "[" + caller + "]: " + s;
        }

        try {
            document.insertString(document.getLength(), s, style);
        } catch (BadLocationException e) {
            logger.severe(e.getMessage());
        }

        scrollToBottom();
    }

    public void println(String s) {
        println(s, false, Color.WHITE);
    }

    public void println(String s, Color color) {
        println(s, false, color);
    }

    public void println(String s, boolean trace) {
        println(s, trace, Color.WHITE);
    }

    public void println(String s, boolean trace, Color c) {
        print(s + "\n", trace, c);
    }

    public void error(String s) {
        println(s, new Color(255, 155, 155));
    }

    public void info(String s) {
        println(s, new Color(244, 179, 66));
    }

    public void scrollToTop() {
        jTextPane.setCaretPosition(0);
    }

    public void scrollToBottom() {
        jTextPane.setCaretPosition(jTextPane.getDocument().getLength());
    }

    public void clearConsole() {
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException e) {
            logger.severe(e.getMessage());
        }
    }

    public void proccessText(String s) {
        final String[] args = s.split(" ");

        if (args.length == 0) return;

        if (args[0].startsWith("/")) {
            commandManager.onCommand(args);
        } else {
            println(s, false);
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
