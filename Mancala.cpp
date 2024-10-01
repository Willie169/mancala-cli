#include <iostream>
#include <vector>
#include <string>

#define MAX_SEEDS 999

void print_board(const std::vector<int>& board, int number_of_pits, int total_pits);
bool make_move(std::vector<int>& board, int number_of_pits, int total_pits, int player, int pit);
bool check_game_over(std::vector<int>& board, int number_of_pits, int total_pits);
void handle_exit();
void print_rules();
void print_info();

int main() {
    int number_of_pits, seeds_per_pit;
    std::string input;

    std::cout << "Type 'rule' to view the rules, 'info' to view the information, or 'exit' to quit anytime.\n\n";

    // Get the number of pits from the user
    while (true) {
        std::cout << "Enter the number of pits each player has: ";
        std::getline(std::cin, input);
        if (input == "exit") {
            handle_exit();
            continue;
        } else if (input == "rule") {
            print_rules();
            continue;
        } else if (input == "info") {
            print_info();
            continue;
        }
        try {
            number_of_pits = std::stoi(input);
            if (number_of_pits > 0) break;
            else std::cout << "Invalid input. Please enter a positive integer.\n";
        } catch (std::exception& e) {
            std::cout << "Invalid input. Please enter a positive integer.\n";
        }
    }

    // Get the number of seeds per pit from the user
    while (true) {
        std::cout << "Enter the number of seeds per pit: ";
        std::getline(std::cin, input);
        if (input == "exit") {
            handle_exit();
            continue;
        } else if (input == "rule") {
            print_rules();
            continue;
        } else if (input == "info") {
            print_info();
            continue;
        }
        try {
            seeds_per_pit = std::stoi(input);
            if (seeds_per_pit > 0 && seeds_per_pit * number_of_pits <= MAX_SEEDS) break;
            else std::cout << "Invalid input. Please enter a positive integer between 1 and " << MAX_SEEDS / number_of_pits << ".\n";
        } catch (std::exception& e) {
            std::cout << "Invalid input. Please enter a positive integer between 1 and " << MAX_SEEDS / number_of_pits << ".\n";
        }
    }

    int total_pits = number_of_pits * 2 + 2; // 2 stores plus the pits
    std::vector<int> board(total_pits, seeds_per_pit);
    board[number_of_pits] = 0; // Player 1's store
    board[total_pits - 1] = 0; // Player 2's store

    // Explain the board setup
    std::cout << "\nGame setup:\n";
    std::cout << "  - Player 1's pits start on the left side of the board.\n";
    std::cout << "  - Player 2's pits start on the right side of the board.\n";
    std::cout << "  - Player 1's store is at the right end of the board.\n";
    std::cout << "  - Player 2's store is at the left end of the board.\n";
    std::cout << "  - The pits and stores are arranged in a line, with Player 1's pits followed by Player 1's store, then Player 2's pits and Player 2's store.\n";
    std::cout << "  - Each player has " << number_of_pits << " pits and " << seeds_per_pit << " seeds in each pit initially.\n";
    std::cout << "Type 'rule' to view the rules, 'info' to view the information, or 'exit' to quit anytime.\n\n";

    int player = 0;
    int pit;
    bool game_over = false;

    while (!game_over) {
        print_board(board, number_of_pits, total_pits);
        
        // Player makes a move
        std::cout << "Player " << player + 1 << ", choose a pit (1-" << number_of_pits << ") : ";
        std::getline(std::cin, input);

        if (input == "exit") {
            handle_exit();
            continue;
        } else if (input == "rule") {
            print_rules();
            print_board(board, number_of_pits, total_pits);
            continue;
        } else if (input == "info") {
            print_info();
            print_board(board, number_of_pits, total_pits);
            continue;
        }

        try {
            pit = std::stoi(input) - 1; // Adjust for 0-indexing
            if (pit < 0 || pit >= number_of_pits) {
                std::cout << "Invalid pit number. Try again.\n";
                continue;
            }

            bool another_turn = make_move(board, number_of_pits, total_pits, player, pit);
            game_over = check_game_over(board, number_of_pits, total_pits);
            
            // Switch players if the last seed didn't land in the player's store
            if (!game_over && !another_turn) {
                player = (player + 1) % 2;
            }
        } catch (std::exception& e) {
            std::cout << "Invalid input. Please enter a pit number, 'exit', or 'rule'.\n";
        }
    }
    
    // Determine the winner
    int player1_store = board[number_of_pits];
    int player2_store = board[total_pits - 1];
    
    if (player1_store > player2_store) {
        std::cout << "Player 1 wins with " << player1_store << " seeds!\n";
    } else if (player2_store > player1_store) {
        std::cout << "Player 2 wins with " << player2_store << " seeds!\n";
    } else {
        std::cout << "It's a tie!\n";
    }
    print_board(board, number_of_pits, total_pits);

    return 0;
}

void print_board(const std::vector<int>& board, int number_of_pits, int total_pits) {
    std::cout << "\nPlayer 2\n";
    std::cout << " ";
    for (int i = total_pits - 2; i > number_of_pits; i--) {
        std::cout << " " << board[i] << " ";
    }
    std::cout << "\n " << board[total_pits - 1];
    for (int i = 0; i < number_of_pits + 1; i++) {
        std::cout << "  ";
    }
    std::cout << "   " << board[number_of_pits] << "\n ";
    for (int i = 0; i < number_of_pits; i++) {
        std::cout << " " << board[i] << " ";
    }
    std::cout << "\nPlayer 1\n\n";
}

bool make_move(std::vector<int>& board, int number_of_pits, int total_pits, int player, int pit) {
    int start = player == 0 ? 0 : number_of_pits + 1;
    int pos = start + pit;
    int seeds = board[pos];

    if (seeds == 0) return true; // Move again if the pit is empty

    board[pos] = 0;
    
    while (seeds > 0) {
        pos = (pos + 1) % total_pits;
        if (pos == (player == 0 ? total_pits - 1 : number_of_pits)) continue; // Skip opponent's store
        board[pos]++;
        seeds--;
    }

    // Check for capture
    if (pos != number_of_pits && pos != total_pits - 1) {  // Last seed not in a store
        if (player == pos / (number_of_pits + 1) && board[pos] == 1) {  // Last seed in player's empty pit
            int opposite = total_pits - 2 - pos;
            if (board[opposite] > 0) {
                board[player == 0 ? number_of_pits : total_pits - 1] += board[opposite] + 1;
                board[opposite] = 0;
                board[pos] = 0;
            }
        }
    }

    // Return true if the last seed was placed in the player's store
    return (player == 0 && pos == number_of_pits) || (player == 1 && pos == total_pits - 1);
}

bool check_game_over(std::vector<int>& board, int number_of_pits, int total_pits) {
    int sum1 = 0, sum2 = 0;
    for (int i = 0; i < number_of_pits; i++) {
        sum1 += board[i];
        sum2 += board[number_of_pits + 1 + i];
    }

    if (sum1 == 0) {
        for (int i = 0; i < number_of_pits; i++) {
            board[number_of_pits] += board[i];
            board[i] = 0;
        }
        return true;
    }

    if (sum2 == 0) {
        for (int i = number_of_pits + 1; i < total_pits; i++) {
            board[total_pits - 1] += board[i];
            board[i] = 0;
        }
        return true;
    }

    return false;
}

void handle_exit() {
    std::string choice;
    
    std::cout << "Are you sure you want to quit? (y/n): ";
    std::getline(std::cin, choice);

    if (std::cin.fail()) {
        std::cout << "\n";
        return;
    }
    else if (choice.substr(0, 1) == "y" || choice.substr(0, 1) == "Y") {
        std::cout << "Exiting the game...\n";
        exit(0);
    } else {
        std::cout << "\n";
        return;
    }
}

void print_rules() {
    std::cout << "\nMancala Rules:\n";
    std::cout << "1. The board has two rows of pits, one for each player. Each player has a row of pits and a store.\n";
    std::cout << "2. Each player has a certain number of pits (defined at the beginning of the game) and each pit starts with the same number of seeds (also defined at the beginning).\n";
    std::cout << "3. The board setup is as follows:\n";
    std::cout << "  - Player 1's pits start on the left side of the board.\n";
    std::cout << "  - Player 2's pits start on the right side of the board.\n";
    std::cout << "  - Player 1's store is at the right end of the board.\n";
    std::cout << "  - Player 2's store is at the left end of the board.\n";
    std::cout << "  - The pits and stores are arranged in a line, with Player 1's pits followed by Player 1's store, then Player 2's pits and Player 2's store.\n";
    std::cout << "4. Players take turns selecting one of their pits to move the seeds. The seeds are distributed counter-clockwise around the board.\n";
    std::cout << "5. Seeds are placed one by one in each subsequent pit, including the player's store but excluding the opponent's store.\n";
    std::cout << "6. If the last seed lands in the player's store, they get another turn.\n";
    std::cout << "7. If the last seed lands in an empty pit on the player's side, they capture all seeds in the opposite pit (if it contains seeds) and add them to their store.\n";
    std::cout << "8. The game ends when all pits on one side are empty. The remaining seeds are moved to the respective stores.\n";
    std::cout << "9. The player with the most seeds in their store at the end of the game wins.\n";
    std::cout << "10. If both players have the same number of seeds in their stores, the game is a tie.\n";
    std::cout << "Type 'rule' to view the rules, 'info' to view the information, or 'exit' to quit anytime.\n\n";
}

void print_info() {
    std::cout << "\nThis is Mancala made by `https://github.com/Willie169`.\n\n";
}
