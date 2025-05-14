package org.manuel.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// No need for ArrayList if using Arrays.asList for fixed shape definitions

class MovimientoTetris {

    // Static nested class for representing a point (block) in a piece's shape
    static class Point {
        int r, c; // Relative row and column from the piece's top-left

        Point(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    // Board dimensions
    static final int NUM_COLS = 12;
    // MAX_ROWS should be enough for fill levels (up to 12) + piece height (up to 4) + some buffer
    static final int MAX_ROWS = 24; 

    // Tetris piece shapes and their rotations
    // Key: Character representing the piece type
    // Value: List of rotations. Each rotation is a List of Points.
    // Points are (row, col) offsets from the top-left of the piece's bounding box for that rotation.
    // 'r' increases downwards, 'c' increases to the right.
    static final Map<Character, List<List<Point>>> PIECES = new HashMap<>();

    static {
        // I piece
        PIECES.put('I', Arrays.asList(
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3)), // Horizontal: ----
                Arrays.asList(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0))  // Vertical: |
        ));

        // L piece
        PIECES.put('L', Arrays.asList(
                Arrays.asList(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(2, 1)), // L shape
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 0)), // Rotated 90 deg clockwise
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)), // Rotated 180
                Arrays.asList(new Point(0, 2), new Point(1, 0), new Point(1, 1), new Point(1, 2))  // Rotated 270
        ));

        // J piece
        PIECES.put('J', Arrays.asList(
                Arrays.asList(new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)), // J shape
                Arrays.asList(new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(1, 2)), // Rotated 90
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(2, 0)), // Rotated 180
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2))  // Rotated 270
        ));

        // T piece
        PIECES.put('T', Arrays.asList(
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 1)), // T shape
                Arrays.asList(new Point(0, 1), new Point(1, 0), new Point(1, 1), new Point(2, 1)), // Rotated 90
                Arrays.asList(new Point(0, 1), new Point(1, 0), new Point(1, 1), new Point(1, 2)), // Rotated 180
                Arrays.asList(new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 0))  // Rotated 270
        ));

        // S piece
        PIECES.put('S', Arrays.asList(
                Arrays.asList(new Point(0, 1), new Point(0, 2), new Point(1, 0), new Point(1, 1)), // S shape
                Arrays.asList(new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1))  // Rotated 90
        ));

        // Z piece
        PIECES.put('Z', Arrays.asList(
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)), // Z shape
                Arrays.asList(new Point(0, 1), new Point(1, 0), new Point(1, 1), new Point(2, 0))  // Rotated 90
        ));

        // O piece (Square)
        PIECES.put('O', Arrays.asList(
                Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1))  // O shape (only one rotation)
        ));
    }

    /**
     * Calculates the width of a piece in its current rotation.
     * The width is determined by the maximum column offset + 1.
     * @param pieceRotation The list of points representing the piece's current rotation.
     * @return The width of the piece.
     */
    private int getPieceWidthForRotation(List<Point> pieceRotation) {
        if (pieceRotation == null || pieceRotation.isEmpty()) return 0;
        int maxC = -1; // Initialize to -1 in case all points are at c=0
        for (Point p : pieceRotation) {
            maxC = Math.max(maxC, p.c);
        }
        return maxC + 1;
    }

    /**
     * Calculates the height of a piece in its current rotation.
     * The height is determined by the maximum row offset + 1.
     * @param pieceRotation The list of points representing the piece's current rotation.
     * @return The height of the piece.
     */
    private int getPieceHeightForRotation(List<Point> pieceRotation) {
        if (pieceRotation == null || pieceRotation.isEmpty()) return 0;
        int maxR = -1; // Initialize to -1 in case all points are at r=0
        for (Point p : pieceRotation) {
            maxR = Math.max(maxR, p.r);
        }
        return maxR + 1;
    }

    /**
     * Checks if a piece can be placed at the given board coordinates without collision or going out of bounds.
     * @param board The current state of the game board.
     * @param pieceRotation The specific rotation of the piece to place.
     * @param pieceTopRow The board row where the top-most part (relative row 0) of the piece would be.
     * @param pieceLeftCol The board column where the left-most part (relative col 0) of the piece would be.
     * @return True if the piece can be placed, false otherwise.
     */
    private boolean canPlace(int[][] board, List<Point> pieceRotation, int pieceTopRow, int pieceLeftCol) {
        for (Point p : pieceRotation) {
            int targetBoardRow = pieceTopRow + p.r;
            int targetBoardCol = pieceLeftCol + p.c;

            // Check bounds
            if (targetBoardRow < 0 || targetBoardRow >= MAX_ROWS || targetBoardCol < 0 || targetBoardCol >= NUM_COLS) {
                return false; // Piece block is out of board boundaries
            }

            // Check collision with existing blocks on the board
            if (board[targetBoardRow][targetBoardCol] == 1) {
                return false; // Collision with an existing block
            }
        }
        return true; // All blocks of the piece can be placed without issues
    }

    /**
     * Calculates the greatest number of horizontal lines that can be completed 
     * when the piece arrives at the bottom.
     * @param strArr Input array: [PieceTypeString, Col0FillString, ..., Col11FillString]
     * @return The greatest number of horizontal lines that can be completed.
     */
    public int tetrisMove(String[] strArr) {
        // Basic input validation
        if (strArr == null || strArr.length != 1 + NUM_COLS) {
            // System.err.println("Invalid input array length.");
            return 0; 
        }

        char pieceType = strArr[0].charAt(0);
        int[] initialFillLevels = new int[NUM_COLS];
        try {
            for (int i = 0; i < NUM_COLS; i++) {
                initialFillLevels[i] = Integer.parseInt(strArr[i + 1]);
                // Validate fill levels to be within reasonable board height
                if (initialFillLevels[i] < 0 || initialFillLevels[i] > MAX_ROWS) {
                    // System.err.println("Invalid fill level for column " + i + ": " + initialFillLevels[i]);
                    return 0; 
                }
            }
        } catch (NumberFormatException e) {
            // System.err.println("Invalid number format for fill levels.");
            return 0;
        }

        // Initialize the game board based on fill levels
        // Board representation: board[row][col], (0,0) is top-left.
        // 1 indicates a filled block, 0 an empty cell.
        int[][] initialBoard = new int[MAX_ROWS][NUM_COLS];
        for (int c = 0; c < NUM_COLS; c++) {
            for (int r_fill = 0; r_fill < initialFillLevels[c]; r_fill++) {
                // Fill from the bottom up. If fillLevel is F, blocks are at
                // board rows MAX_ROWS-1, MAX_ROWS-2, ..., MAX_ROWS-F.
                // r_fill is 0-indexed from bottom.
                int boardRowToFill = MAX_ROWS - 1 - r_fill;
                if (boardRowToFill >= 0) { 
                     initialBoard[boardRowToFill][c] = 1;
                }
            }
        }

        int maxLinesCleared = 0;

        List<List<Point>> rotations = PIECES.get(pieceType);
        if (rotations == null) {
            // System.err.println("Unknown piece type: " + pieceType);
            return 0; // Unknown piece type
        }

        // Iterate through all rotations of the given piece
        for (List<Point> currentRotation : rotations) {
            int pieceWidth = getPieceWidthForRotation(currentRotation);
            int pieceHeight = getPieceHeightForRotation(currentRotation);

            if (pieceWidth == 0 || pieceHeight == 0) continue; // Should not happen with valid piece definitions

            // Iterate through all possible horizontal starting columns for the piece
            // startCol is the board column where the leftmost part (relative col 0) of the piece aligns.
            for (int startCol = 0; startCol <= NUM_COLS - pieceWidth; startCol++) {
                
                int landingRow = -1; // The board row where the top of the piece (relative row 0) lands

                // Find the lowest possible row the piece can be placed at (its landing spot)
                // testTopRow is the candidate board row for the piece's relative (0,0) point.
                for (int testTopRow = 0; testTopRow <= MAX_ROWS - pieceHeight; testTopRow++) {
                    if (canPlace(initialBoard, currentRotation, testTopRow, startCol)) {
                        landingRow = testTopRow; // This position is valid, potentially it can go lower
                    } else {
                        // Cannot place at testTopRow, so it must have landed at the previous valid 'landingRow'
                        break; 
                    }
                }

                if (landingRow != -1) { // A valid landing position was found
                    // Create a temporary board state to place the piece and count lines
                    int[][] boardAfterPlacement = new int[MAX_ROWS][NUM_COLS];
                    for (int r_board = 0; r_board < MAX_ROWS; r_board++) {
                        boardAfterPlacement[r_board] = Arrays.copyOf(initialBoard[r_board], NUM_COLS);
                    }

                    // Place the piece onto this temporary board
                    for (Point p : currentRotation) {
                        boardAfterPlacement[landingRow + p.r][startCol + p.c] = 1;
                    }

                    // Count how many lines are completed on this board state
                    int currentLinesCleared = 0;
                    for (int r_board = 0; r_board < MAX_ROWS; r_board++) {
                        boolean lineIsFull = true;
                        for (int c_board = 0; c_board < NUM_COLS; c_board++) {
                            if (boardAfterPlacement[r_board][c_board] == 0) {
                                lineIsFull = false;
                                break;
                            }
                        }
                        if (lineIsFull) {
                            currentLinesCleared++;
                        }
                    }
                    maxLinesCleared = Math.max(maxLinesCleared, currentLinesCleared);
                }
            }
        }
        return maxLinesCleared;
    }
    
    // Example main method for testing (optional, can be removed for submission)
    public static void main(String[] args) {
        MovimientoTetris game = new MovimientoTetris();

        // Test case 1: I piece on an empty board. Should clear 0 lines.
        String[] test1 = {"I", "0","0","0","0","0","0","0","0","0","0","0","0"};
        System.out.println("Test 1 (I on empty): Expected 0, Actual: " + game.tetrisMove(test1));

        // Test case 2: O piece, board set up to complete 2 lines with O piece
        // Fill rows MAX_ROWS-1 and MAX_ROWS-2 completely, except for a 2x2 hole at cols 5,6
        int[][] customBoard = new int[MAX_ROWS][NUM_COLS];
        for(int c=0; c<NUM_COLS; c++) {
            if (c != 5 && c != 6) {
                customBoard[MAX_ROWS-1][c] = 1;
                customBoard[MAX_ROWS-2][c] = 1;
            }
        }
        // To use this customBoard, we'd need to modify tetrisMove or pass initialBoard.
        // For now, using fill levels:
        String[] test2_fillLevels = new String[1 + NUM_COLS];
        test2_fillLevels[0] = "O";
        for (int i = 0; i < NUM_COLS; i++) {
            if (i == 5 || i == 6) {
                test2_fillLevels[i+1] = "0"; // Columns 5 and 6 are empty
            } else {
                test2_fillLevels[i+1] = "2"; // Other columns filled to height 2
            }
        }
        System.out.println("Test 2 (O completes 2 lines): Expected 2, Actual: " + game.tetrisMove(test2_fillLevels));
        
        // Test case 3: Vertical I piece to complete 4 lines
        String[] test3_fillLevels = new String[1 + NUM_COLS];
        test3_fillLevels[0] = "I";
        for (int i = 0; i < NUM_COLS; i++) {
            if (i == 0) { // Column 0 is empty where the I piece will drop
                test3_fillLevels[i+1] = "0";
            } else { // Other columns are filled up to height 4
                test3_fillLevels[i+1] = "4";
            }
        }
        // If vertical I lands in col 0, its 4 blocks will fill rows MAX_ROWS-1 to MAX_ROWS-4 in col 0.
        // Other columns are already filled in these rows. So, 4 lines.
        System.out.println("Test 3 (Vertical I completes 4 lines): Expected 4, Actual: " + game.tetrisMove(test3_fillLevels));

        // Example from a similar problem: Input: {"L", "0","0","0","0","0","0","0","0","0","1","1","0"}
        // Expected output for this type of problem is often 1 if one line is cleared.
        String[] exampleInput = {"L", "0","0","0","0","0","0","0","0","0","1","1","0"};
        System.out.println("Example (L with 0,0,..,0,1,1,0): Expected 0 or 1, Actual: " + game.tetrisMove(exampleInput));
        // With fill levels [...,0,1,1,0], only 2 blocks at the bottom. An L piece is unlikely to complete a full line
        // unless other columns (0-8, 11) were also filled to height 1.
        // If the board was: "0","0","0","0","0","0","0","0","1","1","1","0" (col 8,9,10 height 1)
        // And L piece rotation [[0,0],[1,0],[2,0],[2,1]] (3 high, 2 wide at bottom)
        // Lands in startCol=8. Piece blocks are (MR-3,8), (MR-2,8), (MR-1,8), (MR-1,9).
        // This example is tricky to get an "expected" without knowing the exact configuration that leads to 1.
        // The current code will correctly calculate based on the rules.
        
        
        String[] exampleInput2 = {"I", "2","4","3","4","5","2","0","6","5","3","6","4"};
        System.out.println("Example (I with 0,0,..,5, 6, 3, 4): Expected 2, Actual: " + game.tetrisMove(exampleInput2));
    }
}

