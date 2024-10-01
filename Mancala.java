import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mancala {
    private static final int MAX_SEEDS = 999;

    public static void main(String[] args) {
        int numberOfPits, seedsPerPit;
        String input;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Type 'rule' to view the rules, 'info' to view the information, or 'exit' to quit anytime.\n\n");

        // Get the number of pits from the user
        while (true) {
            System.out.print("Enter the number of pits each player has: ");
            input = scanner.nextLine();
            if (input.equals("exit")) {
                handleExit();
                continue;
            } else if (input.equals("rule")) {
                printRules();
                continue;
            } else if (input.equals("info")) {
                printInfo();
                continue;
            }
            try {
                numberOfPits = Integer.parseInt(input);
                if (numberOfPits > 0) break;
                else System.out.print("Invalid input. Please enter a positive integer.\n");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a positive integer.\n");
            }
        }

        // Get the number of seeds per pit from the user
        while (true) {
            System.out.print("Enter the number of seeds per pit: ");
            input = scanner.nextLine();
            if (input.equals("exit")) {
                handleExit();
                continue;
            } else if (input.equals("rule")) {
                printRules();
                continue;
            } else if (input.equals("info")) {
                printInfo();
                continue;
            }
            try {
                seedsPerPit = Integer.parseInt(input);
                if (seedsPerPit > 0 && seedsPerPit * numberOfPits <= MAX_SEEDS) break;
                else System.out.print("Invalid input. Please enter a positive integer between 1 and " + MAX_SEEDS / numberOfPits + ".\n");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a positive integer between 1 and " + MAX_SEEDS / numberOfPits + ".\n");
            }
        }

        int totalPits = numberOfPits * 2 + 2; // 2 stores plus the pits
        List<Integer> board = new ArrayList<>(totalPits);
        for (int i = 0; i < totalPits; i++) {
            board.add(seedsPerPit);
        }
        board.set(numberOfPits, 0); // Player 1's store
        board.set(totalPits - 1, 0); // Player 2's store

        // Explain the board setup
        System.out.print("\nGame setup:\n");
        System.out.print("  - Player 1's pits start on the left side of the board.\n");
        System.out.print("  - Player 2's pits start on the right side of the board.\n");
        System.out.print("  - Player 1's store is at the right end of the board.\n");
        System.out.print("  - Player 2's store is at the left end of the board.\n");
        System.out.print("  - The pits and stores are arranged in a line, with Player 1's pits followed by Player 1's store, then Player 2's pits and Player 2's store.\n");
        System.out.print("  - Each player has " + numberOfPits + " pits and " + seedsPerPit + " seeds in each pit initially.\n");
        System.out.print("Type 'rule' to view the rules, 'info' to view the information, or 'exit' to quit anytime.\n\n");

        int player = 0;
        int pit;
        boolean gameOver = false;

        while (!gameOver) {
            printBoard(board, numberOfPits, totalPits);

            // Player makes a move
            System.out.print("Player " + (player + 1) + ", choose a pit (1-" + numberOfPits + ") : ");
            input = scanner.nextLine();

            if (input.equals("exit")) {
                handleExit();
                continue;
            } else if (input.equals("rule")) {
                printRules();
                printBoard(board, numberOfPits, totalPits);
                continue;
            } else if (input.equals("info")) {
                printInfo();
                printBoard(board, numberOfPits, totalPits);
                continue;
            }

            try {
                pit = Integer.parseInt(input) - 1; // Adjust for 0-indexing
                if (pit < 0 || pit >= numberOfPits) {
                    System.out.print("Invalid pit number. Try again.\n");
                    continue;
                }

                boolean anotherTurn = makeMove(board, numberOfPits, totalPits, player, pit);
                gameOver = checkGameOver(board, numberOfPits, totalPits);

                // Switch players if the last seed didn't land in the player's store
                if (!gameOver && !anotherTurn) {
                    player = (player + 1) % 2;
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a pit number, 'exit', or 'rule'.\n");
            }
        }

        // Determine the winner
        int player1Store = board.get(numberOfPits);
        int player2Store = board.get(totalPits - 1);

        if (player1Store > player2Store) {
            System.out.print("Player 1 wins with " + player1Store + " seeds!\n");
        } else if (player2Store > player1Store) {
            System.out.print("Player 2 wins with " + player2Store + " seeds!\n");
        } else {
            System.out.print("It's a tie!\n");
        }
        printBoard(board, numberOfPits, totalPits);
    }

    private static void printBoard(List<Integer> board, int numberOfPits, int totalPits) {
        System.out.print("\nPlayer 2\n");
        System.out.print(" ");
        for (int i = totalPits - 2; i > numberOfPits; i--) {
            System.out.print(" " + board.get(i) + " ");
        }
        System.out.print("\n " + board.get(totalPits - 1));
        for (int i = 0; i < numberOfPits + 1; i++) {
            System.out.print("  ");
        }
        System.out.print("   " + board.get(numberOfPits) + "\n ");
        for (int i = 0; i < numberOfPits; i++) {
            System.out.print(" " + board.get(i) + " ");
        }
        System.out.print("\nPlayer 1\n\n");
    }

    private static boolean makeMove(List<Integer> board, int numberOfPits, int totalPits, int player, int pit) {
        int start = player == 0 ? 0 : numberOfPits + 1;
        int pos = start + pit;
        int seeds = board.get(pos);

        if (seeds == 0) return true; // Move again if the pit is empty

        board.set(pos, 0);

        while (seeds > 0) {
            pos = (pos + 1) % totalPits;
            if (pos == (player == 0 ? totalPits - 1 : numberOfPits)) continue; // Skip opponent's store
            board.set(pos, board.get(pos) + 1);
            seeds--;
        }

        // Check for capture
        if (pos != numberOfPits && pos != totalPits - 1) {  // Last seed not in a store
            if (player == pos / (numberOfPits + 1) && board.get(pos) == 1) {  // Last seed in player's empty pit
                int opposite = totalPits - 2 - pos;
                if (board.get(opposite) > 0) {
                    board.set(player == 0 ? numberOfPits : totalPits - 1, board.get(player == 0 ? numberOfPits : totalPits - 1) + board.get(opposite) + 1);
                    board.set(opposite, 0);
                    board.set(pos, 0);
                }
            }
        }

        // Return true if the last seed was placed in the player's store
        return (player == 0 && pos == numberOfPits) || (player == 1 && pos == totalPits - 1);
    }

    private static boolean checkGameOver(List<Integer> board, int numberOfPits, int totalPits) {
        int sum1 = 0, sum2 = 0;
        for (int i = 0; i < numberOfPits; i++) {
            sum1 += board.get(i);
            sum2 += board.get(numberOfPits + 1 + i);
        }

        if (sum1 == 0) {
            for (int i = 0; i < numberOfPits; i++) {
                board.set(numberOfPits, board.get(numberOfPits) + board.get(i));
                board.set(i, 0);
            }
            return true;
        }

        if (sum2 == 0) {
            for (int i = numberOfPits + 1; i < totalPits; i++) {
                board.set(totalPits - 1, board.get(totalPits - 1) + board.get(i));
                board.set(i, 0);
            }
            return true;
        }

        return false;
    }

    private static void handleExit() {
        Scanner scanner = new Scanner(System.in);
        String choice;

        System.out.print("Are you sure you want to quit? (y/n): ");
        choice = scanner.nextLine();

        if (choice.substring(0, 1).equalsIgnoreCase("y")) {
            System.out.print("Exiting the game...\n");
            System.exit(0);
        } else {
            System.out.print("\n");
        }
    }

    private static void printRules() {
        System.out.print("\nMancala Rules:\n");
        System.out.print("1. The board has two rows of pits, one for each player. Each player has a row of pits and a store.\n");
        System.out.print("2. Each player has a certain number of pits (defined at the beginning of the game) and each pit starts with the same number of seeds (also defined at the beginning).\n");
        System.out.print("3. The board setup is as follows:\n");
        System.out.print("  - Player 1's pits start on the left side of the board.\n");
        System.out.print("  - Player 2's pits start on the right side of the board.\n");
        System.out.print("  - Player 1's store is at the right end of the board.\n");
        System.out.print("  - Player 2's store is at the left end of the board.\n");
        System.out.print("  - The pits and stores are arranged in a line, with Player 1's pits followed by Player 1's store, then Player 2's pits and Player 2's store.\n");
        System.out.print("4. Players take turns selecting one of their pits to move the seeds. The seeds are distributed counter-clockwise around the board.\n");
        System.out.print("5. Seeds are placed one by one in each subsequent pit, including the player's store but excluding the opponent's store.\n");
        System.out.print("6. If the last seed lands in the player's store, they get another turn.\n");
        System.out.print("7. If the last seed lands in an empty pit on the player's side, they capture all seeds in the opposite pit (if it contains seeds) and add them to their store.\n");
        System.out.print("8. The game ends when all pits on one side are empty. The remaining seeds are moved to the respective stores.\n");
        System.out.print("9. The player with the most seeds in their store at the end of the game wins.\n");
        System.out.print("10. If both players have the same number of seeds in their stores, the game is a tie.\n");
        System.out.print("Type 'rule' to view the rules, 'info' to view the information, or 'exit' to quit anytime.\n\n");
    }

    private static void printInfo() {
        System.out.print("\nThis is Mancala made by `https://github.com/Willie169`.\n\n");
    }
}
