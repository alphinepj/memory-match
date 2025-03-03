# Memory Match Game

## Overview
Memory Match Game is a simple Java-based memory card game implemented using Swing. The game challenges players to match pairs of cards within a limited time.

## Features
- **4x4 Grid**: A 4x4 card layout where players must find matching pairs.
- **Timer**: Players must complete the game within 60 seconds.
- **Hints**: A hint button reveals all cards temporarily.
- **Sound Effects**: Audio feedback for correct and incorrect matches.
- **Score Tracking**: Saves player scores and time taken to a file.
- **Main Menu**: Allows users to enter their name and view past scores.

## Requirements
- Java 8 or later

## How to Run
1. Compile the Java file:
   ```sh
   javac MemoryMatchGame.java
   ```
2. Run the game:
   ```sh
   java MemoryMatchGame
   ```

## Controls
- Click on a card to reveal its value.
- Match pairs to clear the board.
- Use the **Hint** button for assistance.
- Click **Quit** to exit to the main menu.

## File Structure
- `MemoryMatchGame.java` - Main game logic and GUI.
- `scores.txt` - Stores player scores.
- Sound files (`match.wav`, `win.wav`, `wrong.wav`, `lose.wav`) for game effects.

## Future Improvements
- Add more difficulty levels.
- Implement a multiplayer mode.
- Enhance UI with better graphics.

## License
This project is open-source and free to use for educational purposes.

