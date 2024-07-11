import java.util.Scanner;

public class ChessGame {
    private static char[][] board;
    private static boolean whiteTurn = true;
    private static boolean gameOver = false;

    public static void main(String[] args) {
        initializeBoard();
        printBoard();

        Scanner scanner = new Scanner(System.in);

        while (!gameOver) {
            String currentPlayer = whiteTurn ? "White" : "Black";
            System.out.println(currentPlayer + " to move.");
            System.out.println("Enter move (e.g., 'e2 e4'): ");
            String move = scanner.nextLine().trim();

            if (isValidInputFormat(move)) {
                int[] moveIndices = convertToIndices(move);
                if (isValidMove(moveIndices[0], moveIndices[1], moveIndices[2], moveIndices[3])) {
                    makeMove(moveIndices[0], moveIndices[1], moveIndices[2], moveIndices[3]);
                    printBoard();
                    if (isCheckmate(!whiteTurn)) {
                        System.out.println("Checkmate! " + currentPlayer + " wins!");
                        gameOver = true;
                    } else if (isStalemate(!whiteTurn)) {
                        System.out.println("Stalemate! The game is a draw.");
                        gameOver = true;
                    }
                    whiteTurn = !whiteTurn; // Switch turns
                } else {
                    System.out.println("Invalid move! Try again.");
                }
            } else {
                System.out.println("Invalid input format! Please enter move in the format 'e2 e4'.");
            }
        }

        scanner.close();
    }

    // Check if the input move format is valid
    public static boolean isValidInputFormat(String move) {
        return move.matches("[a-h][1-8] [a-h][1-8]");
    }

    // Convert move string to row and column indices
    public static int[] convertToIndices(String move) {
        char[] moveChars = move.toCharArray();
        int fromCol = moveChars[0] - 'a';
        int fromRow = 8 - (moveChars[1] - '0');
        int toCol = moveChars[3] - 'a';
        int toRow = 8 - (moveChars[4] - '0');
        return new int[]{fromRow, fromCol, toRow, toCol};
    }

    // Check if a move is valid
    public static boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Check if coordinates are within bounds
        if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7 ||
            toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) {
            return false;
        }

        // Check if the piece belongs to the current player
        char piece = board[fromRow][fromCol];
        boolean isWhitePiece = Character.isUpperCase(piece);
        if ((whiteTurn && !isWhitePiece) || (!whiteTurn && isWhitePiece)) {
            return false;
        }

        // Check specific piece movements
        switch (Character.toLowerCase(piece)) {
            case 'p':
                return isValidPawnMove(fromRow, fromCol, toRow, toCol);
            case 'r':
                return isValidRookMove(fromRow, fromCol, toRow, toCol);
            case 'n':
                return isValidKnightMove(fromRow, fromCol, toRow, toCol);
            case 'b':
                return isValidBishopMove(fromRow, fromCol, toRow, toCol);
            case 'q':
                return isValidQueenMove(fromRow, fromCol, toRow, toCol);
            case 'k':
                return isValidKingMove(fromRow, fromCol, toRow, toCol);
            default:
                return false;
        }
    }

    // Check if a pawn move is valid
    public static boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol) {
        char piece = board[fromRow][fromCol];
        boolean isWhite = Character.isUpperCase(piece);
        int direction = isWhite ? -1 : 1;

        // Normal pawn move (one square forward)
        if (toCol == fromCol && board[toRow][toCol] == '-') {
            if (toRow == fromRow + direction) {
                return true;
            }
            // Pawn double move from starting position
            if ((isWhite && fromRow == 6 && toRow == 4 && board[5][toCol] == '-' && board[4][toCol] == '-') ||
                (!isWhite && fromRow == 1 && toRow == 3 && board[2][toCol] == '-' && board[3][toCol] == '-')) {
                return true;
            }
        }

        // Pawn capture move
        if (Math.abs(toCol - fromCol) == 1 && toRow == fromRow + direction) {
            char targetPiece = board[toRow][toCol];
            return (isWhite && Character.isLowerCase(targetPiece)) || (!isWhite && Character.isUpperCase(targetPiece));
        }

        return false;
    }

    // Check if a rook move is valid
    public static boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Rook moves horizontally or vertically
        if (fromRow == toRow || fromCol == toCol) {
            int rowStep = (fromRow == toRow) ? 0 : (fromRow < toRow) ? 1 : -1;
            int colStep = (fromCol == toCol) ? 0 : (fromCol < toCol) ? 1 : -1;

            for (int r = fromRow + rowStep, c = fromCol + colStep; r != toRow || c != toCol; r += rowStep, c += colStep) {
                if (board[r][c] != '-') {
                    return false; // Path blocked
                }
            }
            char targetPiece = board[toRow][toCol];
            return (board[toRow][toCol] == '-' || (Character.isUpperCase(board[fromRow][fromCol]) != Character.isUpperCase(targetPiece)));
        }
        return false;
    }

    // Check if a knight move is valid
    public static boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Knight moves in an L-shape: 2 squares in one direction and 1 square perpendicular
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    // Check if a bishop move is valid
    public static boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Bishop moves diagonally
        if (Math.abs(toRow - fromRow) == Math.abs(toCol - fromCol)) {
            int rowStep = (fromRow < toRow) ? 1 : -1;
            int colStep = (fromCol < toCol) ? 1 : -1;
            for (int r = fromRow + rowStep, c = fromCol + colStep; r != toRow; r += rowStep, c += colStep) {
                if (board[r][c] != '-') {
                    return false; // Path blocked
                }
            }
            char targetPiece = board[toRow][toCol];
            return (board[toRow][toCol] == '-' || (Character.isUpperCase(board[fromRow][fromCol]) != Character.isUpperCase(targetPiece)));
        }
        return false;
    }

    // Check if a queen move is valid
    public static boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Queen moves horizontally, vertically, or diagonally
        return isValidRookMove(fromRow, fromCol, toRow, toCol) || isValidBishopMove(fromRow, fromCol, toRow, toCol);
    }

    // Check if a king move is valid
    public static boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        // King moves one square in any direction
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        return rowDiff <= 1 && colDiff <= 1;
    }

    // Check if the specified color's king is in check
    public static boolean isKingInCheck(boolean isWhite) {
        int kingRow = -1, kingCol = -1;
        char king = isWhite ? 'K' : 'k';

        // Locate the king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == king) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }

        // Check for threats from all opponent pieces
        return isThreatened(kingRow, kingCol, isWhite);
    }

    // Check if a specific square is threatened by the opponent
    private static boolean isThreatened(int row, int col, boolean isWhite) {
        // Check for pawns
        int direction = isWhite ? 1 : -1;
        if (isValidPosition(row + direction, col - 1) && board[row + direction][col - 1] == (isWhite ? 'p' : 'P')) return true;
        if (isValidPosition(row + direction, col + 1) && board[row + direction][col + 1] == (isWhite ? 'p' : 'P')) return true;

        // Check for knights
        int[] knightMoves = {-2, -1, 1, 2};
        for (int dr : knightMoves) {
            for (int dc : knightMoves) {
                if (Math.abs(dr) != Math.abs(dc)) {
                    if (isValidPosition(row + dr, col + dc) && Character.toLowerCase(board[row + dr][col + dc]) == 'n') {
                        return true;
                    }
                }
            }
        }

        // Check for bishops, rooks, queens
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] d : directions) {
            int r = row, c = col;
            while (true) {
                r += d[0];
                c += d[1];
                if (!isValidPosition(r, c)) break;
                char piece = board[r][c];
                if (piece != '-') {
                    if (Character.isUpperCase(piece) != isWhite) {
                        if ((Math.abs(d[0]) == Math.abs(d[1]) && (piece == 'b' || piece == 'q' || piece == 'B' || piece == 'Q')) ||
                            ((d[0] == 0 || d[1] == 0) && (piece == 'r' || piece == 'q' || piece == 'R' || piece == 'Q'))) {
                            return true;
                        }
                    }
                    break; // Blocked by another piece
                }
            }
        }

        // Check for opposing king
        int[][] kingMoves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] move : kingMoves) {
            int r = row + move[0];
            int c = col + move[1];
            if (isValidPosition(r, c) && Character.toLowerCase(board[r][c]) == 'k') {
                return true;
            }
        }

        return false;
    }

    // Validate if a position is within board boundaries
    private static boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }


    // Initialize the chess board
    public static void initializeBoard() {
        // Create an 8x8 chess board with initial piece positions
        board = new char[][] {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
    }

    // Print the current state of the board
    public static void printBoard() {
        System.out.println("   a b c d e f g h");
        System.out.println(" +-----------------+");
        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + "| ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("| " + (8 - i));
        }
        System.out.println(" +-----------------+");
        System.out.println("   a b c d e f g h");
        System.out.println();
    }

    // Make a move on the board
    public static void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Perform the move
        char piece = board[fromRow][fromCol];
        board[fromRow][fromCol] = '-';
        board[toRow][toCol] = piece;
    }

    public static boolean isCheckmate(boolean isWhite) {
        // Check if the king of the specified color is in check
        if (!isKingInCheck(isWhite)) {
            return false; // King is not in check, not in checkmate
        }
    
        // Attempt to make every possible move for the current player
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                if ((isWhite && Character.isUpperCase(board[fromRow][fromCol])) || (!isWhite && Character.isLowerCase(board[fromRow][fromCol]))) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            if (isValidMove(fromRow, fromCol, toRow, toCol)) {
                                // Simulate the move
                                char originalPiece = board[toRow][toCol];
                                board[toRow][toCol] = board[fromRow][fromCol];
                                board[fromRow][fromCol] = '-';
    
                                // Check if the king is still in check after the move
                                boolean stillInCheck = isKingInCheck(isWhite);
    
                                // Undo the move
                                board[fromRow][fromCol] = board[toRow][toCol];
                                board[toRow][toCol] = originalPiece;
    
                                // If any move removes the king from check, it's not checkmate
                                if (!stillInCheck) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
    
        // If no moves can remove the king from check, it's checkmate
        return true;
    }

    // Check if the game is in stalemate for the specified color
    public static boolean isStalemate(boolean isWhite) {
        // To implement: check if the specified color is in stalemate
        // For now, return false to avoid immediate stalemate
        return false;
    }
}
