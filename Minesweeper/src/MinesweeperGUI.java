import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinesweeperGUI {
    private JFrame frame;
    private JButton[][] buttons;
    private MinesweeperGame game;
    private Timer timer;
    private JLabel timeLabel;
    private int timeElapsed;

    public MinesweeperGUI(int rows, int cols, int numMines) {
        game = new MinesweeperGame(rows, cols, numMines);
        buttons = new JButton[rows][cols];
        frame = new JFrame("Minesweeper");
        frame.setSize(40 * cols, 40 * rows);
        frame.setLayout(new GridLayout(rows + 1, cols));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setMargin(new Insets(0, 0, 0, 0));
                buttons[i][j].addActionListener(new ButtonListener(i, j));
                frame.add(buttons[i][j]);
            }
        }

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ResetButtonListener());
        frame.add(resetButton);

        timeLabel = new JLabel("Time: 0");
        frame.add(timeLabel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        timer = new Timer(1000, new TimerListener());
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int rows = 8;
            int cols = 8;
            int numMines = 12;
            new MinesweeperGUI(rows, cols, numMines);
        });
    }

    private class ButtonListener implements ActionListener {
        private int row;
        private int col;

        public ButtonListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (game.isMine(row, col)) {
                buttons[row][col].setBackground(Color.RED);
                JOptionPane.showMessageDialog(frame, "Game Over!");
                resetGame();
            } else {
                int adjacentMines = game.getAdjacentMines(row, col);
                buttons[row][col].setText(String.valueOf(adjacentMines));
                buttons[row][col].setEnabled(false);
                buttons[row][col].setBackground(Color.blue);
                if (adjacentMines == 0) {
                    game.revealEmptyCells(row, col);
                    updateButtons();
                }
                if (game.isGameWon()) {
                    JOptionPane.showMessageDialog(frame, "You Win!");
                    resetGame();
                }
            }
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resetGame();
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timeElapsed++;
            timeLabel.setText("Time: " + timeElapsed);
        }
    }

    private void resetGame() {
        game.reset();
        updateButtons();
        timeElapsed = 0;
        timeLabel.setText("Time: 0");
    }

    private void updateButtons() {
        for (int i = 0; i < game.getRows(); i++) {
            for (int j = 0; j < game.getCols(); j++) {
                if (game.isRevealed(i, j)) {
                    int adjacentMines = game.getAdjacentMines(i, j);
                    buttons[i][j].setText(String.valueOf(adjacentMines));
                    buttons[i][j].setEnabled(false);
                    buttons[i][j].setBackground(Color.black);
                } else {
                    buttons[i][j].setText("");
                    buttons[i][j].setEnabled(true);
                    buttons[i][j].setBackground(null);
                }
            }
        }
    }
}

class MinesweeperGame {
    private int rows;
    private int cols;
    private int numMines;
    private boolean[][] mines;
    private boolean[][] revealed;

    public MinesweeperGame(int rows, int cols, int numMines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;
        this.mines = new boolean[rows][cols];
        this.revealed = new boolean[rows][cols];
        generateMines();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public boolean isRevealed(int row, int col) {
        return revealed[row][col];
    }

    public int getAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = Math.max(0, row - 1); i <= Math.min(rows - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(cols - 1, col + 1); j++) {
                if (mines[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    public void revealEmptyCells(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || revealed[row][col]) {
            return;
        }

        revealed[row][col] = true;

        if (getAdjacentMines(row, col) == 0) {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    revealEmptyCells(i, j);
                }
            }
        }
    }

    public void reset() {
        generateMines();
        revealed = new boolean[rows][cols];
    }

    public boolean isGameWon() {
        int totalCells = rows * cols;
        int revealedCells = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (revealed[i][j]) {
                    revealedCells++;
                }
            }
        }
        return revealedCells == totalCells - numMines;
    }

    private void generateMines() {
        int count = 0;
        while (count < numMines) {
            int row = (int) (Math.random() * rows);
            int col = (int) (Math.random() * cols);
            if (!mines[row][col]) {
                mines[row][col] = true;
                count++;
            }
        }
    }
}
