import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

/**
 * MemoryMatchGame class implements a memory matching game with GUI using Java Swing.
 */
public class MemoryMatchGame extends JFrame implements ActionListener {
    private static final int GRID_SIZE = 4; // Grid size for a 4x4 card layout
    private JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE]; // Button grid for cards
    private String[][] cardValues = new String[GRID_SIZE][GRID_SIZE]; // Card values behind each button
    private JButton firstSelected = null; // First selected button
    private JButton secondSelected = null; // Second selected button
    private int pairsFound = 0; // Track number of pairs found
    private static HashMap<String, Integer> scores = new HashMap<>(); // Store player scores
    private String username; // Current player's username
    private JButton hintButton; // Button for hint feature
    private Timer gameTimer; // Timer to track game duration
    private JLabel timerLabel; // Label to display remaining time
    private int timeLeft = 60; // Time left for the game
    private long startTime; // Start time for timing

    /**
     * Constructor to initialize the MemoryMatchGame.
     * username Player's name.
     */
    public MemoryMatchGame(String username) {
        this.username = username;
        setTitle("Memory Match Game - Player: " + username);
        setSize(700, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        initializeCards(); // Set up card values
        initializeButtons(gamePanel); // Set up buttons

        timerLabel = new JLabel("Time left: " + timeLeft + " seconds", SwingConstants.CENTER);
        hintButton = new JButton("Hint");
        JButton quitButton = new JButton("Quit");
        hintButton.addActionListener(epppppppppp -> showHint());
        quitButton.addActionListener(e -> quitGame());

        JPanel controlPanel = new JPanel();
        controlPanel.add(timerLabel);
        controlPanel.add(hintButton);
        controlPanel.add(quitButton);

        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        startTimer();
        startTime = System.currentTimeMillis(); // Record the start time
    }

    /**
     * Initializes the card values by shuffling pairs of numbers.
     */
    private void initializeCards() {
        ArrayList<String> values = new ArrayList<>();
        for (int i = 1; i <= (GRID_SIZE * GRID_SIZE) / 2; i++) {
            values.add(String.valueOf(i));
            values.add(String.valueOf(i));
        }
        Collections.shuffle(values);

        int index = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cardValues[row][col] = values.get(index++); // Assign shuffled values
            }
        }
    }

    /**
     * Initializes buttons and adds them to the game panel.
     * gamePanel The panel to hold the buttons.
     */
    private void initializeButtons(JPanel gamePanel) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.setBackground(Color.LIGHT_GRAY);
                button.addActionListener(this);
                buttons[row][col] = button;
                gamePanel.add(button);
            }
        }
    }

    /**
     * Handles button click events.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        int row = -1, col = -1;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (buttons[i][j] == clickedButton) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        if (clickedButton.getText().length() > 0) {
            return; // Ignore already revealed cards
        }

        clickedButton.setText(cardValues[row][col]); // Show card value
        if (firstSelected == null) {
            firstSelected = clickedButton;
        } else if (secondSelected == null) {
            secondSelected = clickedButton;
            Timer timer = new Timer(500, event -> checkMatch());
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Checks if the selected pair of cards match.
     */
    private void checkMatch() {
        if (firstSelected.getText().equals(secondSelected.getText())) {
            firstSelected.setEnabled(false);
            secondSelected.setEnabled(false);
            firstSelected.setBackground(Color.GRAY);
            secondSelected.setBackground(Color.GRAY);
            pairsFound++;
            playSound("match.wav"); // Play match sound
            if (pairsFound == (GRID_SIZE * GRID_SIZE) / 2) {
                playSound("win.wav");
                saveScore();
                long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
                JOptionPane.showMessageDialog(this, "Congratulations, " + username + "! You won! Time taken: " + timeTaken + " seconds");
                quitGame();
            }
        } else {
            playSound("wrong.wav");
            firstSelected.setText("");
            secondSelected.setText("");
        }
        firstSelected = null;
        secondSelected = null;
    }

    /**
     * Quits the game and returns to the main menu.
     */
    private void quitGame() {
        new MainMenu();
        dispose();
    }

    /**
     * Shows a hint by revealing all card values temporarily.
     */
    private void showHint() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                buttons[row][col].setText(cardValues[row][col]);
            }
        }
        Timer hintTimer = new Timer(1000, e -> hideAllCards());
        hintTimer.setRepeats(false);
        hintTimer.start();
    }

    /**
     * Hides all card values, resetting them to blank.
     */
    private void hideAllCards() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (buttons[row][col].isEnabled()) {
                    buttons[row][col].setText("");
                }
            }
        }
    }

    /**
     * Plays a sound from the provided file.
     * soundFile The sound file to play.
     */
    private void playSound(String soundFile) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(soundFile)));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the player's score and time taken to a file.
     */
    private void saveScore() {
        long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            writer.write(username + ": " + pairsFound + " pairs, Time Taken: " + timeTaken + " seconds\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the game timer and updates the remaining time.
     */
    private void startTimer() {
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft + " seconds");
            if (timeLeft <= 0) {
                playSound("lose.wav");
                JOptionPane.showMessageDialog(this, "Time's up! You lost.");
                quitGame();
            }
        });
        gameTimer.start();
    }

    /**
     * Main method to launch the game from the main menu.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }

    /**
     * Inner class for the main menu interface.
     */
    public static class MainMenu extends JFrame {
        public MainMenu() {
            setTitle("Memory Match Game Main Menu");
            setSize(500, 400);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            JLabel promptLabel = new JLabel("Enter your username:", SwingConstants.CENTER);
            JTextField usernameField = new JTextField();
            JButton startButton = new JButton("Start Game");
            JTextArea resultArea = new JTextArea("Previous Scores:\n", 5, 20);
            resultArea.setEditable(false);
            updateResults(resultArea);

            startButton.setFont(new Font("Arial", Font.BOLD, 18));
            startButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    new MemoryMatchGame(username).setVisible(true);
                    dispose();
                }
            });

            panel.add(promptLabel);
            panel.add(usernameField);
            panel.add(startButton);
            JScrollPane scrollPane = new JScrollPane(resultArea);
            add(panel, BorderLayout.CENTER);
            add(scrollPane, BorderLayout.SOUTH);
            setVisible(true);
        }

        /**
         * Updates the result area with previous scores from the file.
         * resultArea Text area to display scores.
         */
        private void updateResults(JTextArea resultArea) {
            try (BufferedReader reader = new BufferedReader(new FileReader("scores.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    resultArea.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
