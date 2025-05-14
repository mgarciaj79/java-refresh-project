package org.manuel.test;

import java.util.*;

class OtroTetris {
    final int BOARD_WIDTH = 12;
    final int BOARD_HEIGHT = 20; // Sufficient height for typical Tetris scenarios

    // Map to store piece rotations. Key: piece type, Value: List of 2D int arrays for rotations
    Map<String, List<int[][]>> pieceRotations = new HashMap<>();

    public OtroTetris() {
        // Initialize pieceRotations map with shapes for each piece type
        initializePieceRotations();
    }

    private void initializePieceRotations() {
        // Define shapes for each piece and its rotations (1 represents a filled block)
        // I piece
        pieceRotations.put("I", Arrays.asList(
            new int[][]{{1, 1, 1, 1}}, // 0 deg (1x4)
            new int[][]{{1}, {1}, {1}, {1}} // 90 deg (4x1)
        ));

        // J piece
        pieceRotations.put("J", Arrays.asList(
            new int[][]{{1, 0, 0}, {1, 1, 1}}, // 0 deg (2x3)
            new int[][]{{0, 1}, {0, 1}, {1, 1}}, // 90 deg (3x2)
            new int[][]{{1, 1, 1}, {0, 0, 1}}, // 180 deg (2x3)
            new int[][]{{1, 1}, {1, 0}, {1, 0}} // 270 deg (3x2)
        ));

        // L piece
        pieceRotations.put("L", Arrays.asList(
            new int[][]{{0, 0, 1}, {1, 1, 1}}, // 0 deg (2x3)
            new int[][]{{1, 1}, {0, 1}, {0, 1}}, // 90 deg (3x2)
            new int[][]{{1, 1, 1}, {1, 0, 0}}, // 180 deg (2x3)
            new int[][]{{1, 0}, {1, 0}, {1, 1}} // 270 deg (3x2)
        ));

        // O piece
        
        int [][] oPieceShape = {{1, 1}, {1, 1}};
        List<int[][]> oPieceRotation = new ArrayList<int[][]>();
        oPieceRotation.add(oPieceShape);
        pieceRotations.put("O", oPieceRotation);

        // S piece
        pieceRotations.put("S", Arrays.asList(
            new int[][]{{0, 1, 1}, {1, 1, 0}}, // 0 deg (2x3)
            new int[][]{{1, 0}, {1, 1}, {0, 1}} // 90 deg (3x2)
        ));

        // T piece
        pieceRotations.put("T", Arrays.asList(
            new int[][]{{0, 1, 0}, {1, 1, 1}}, // 0 deg (2x3)
            new int[][]{{1, 0}, {1, 1}, {1, 0}}, // 90 deg (3x2)
            new int[][]{{1, 1, 1}, {0, 1, 0}}, // 180 deg (2x3)
            new int[][]{{0, 1}, {1, 1}, {0, 1}} // 270 deg (3x2)
        ));

        // Z piece
        pieceRotations.put("Z", Arrays.asList(
            new int[][]{{1, 1, 0}, {0, 1, 1}}, // 0 deg (2x3)
            new int[][]{{0, 1}, {1, 1}, {1, 0}} // 90 deg (3x2)
        ));
    }

    /**
     * Calculates the greatest number of horizontal lines that can be completed
     * when a Tetris piece is dropped onto a board with given fill levels.
     *
     * @param strArr An array where the first element is the piece type (I, J, L, O, S, T, Z)
     * and the remaining 12 elements are the fill levels for the 12 columns.
     * @return The maximum number of horizontal lines that can be completed.
     */
    public int tetrisMove(String[] strArr) {
        String pieceType = strArr[0];
        int[] initialFill = new int[BOARD_WIDTH];
        for (int i = 0; i < BOARD_WIDTH; i++) {
            initialFill[i] = Integer.parseInt(strArr[i + 1]);
        }

        int maxLinesCleared = 0;

        List<int[][]> rotations = pieceRotations.get(pieceType);

        // Iterate through all possible rotations of the piece
        for (int[][] rotatedPiece : rotations) {
            int pieceHeight = rotatedPiece.length;
            int pieceWidth = rotatedPiece[0].length;

            // Iterate through all possible horizontal placements (column offsets)
            for (int colOffset = 0; colOffset <= BOARD_WIDTH - pieceWidth; colOffset++) {

                // Create a copy of the board for this placement simulation
                int[][] currentBoard = new int[BOARD_HEIGHT][BOARD_WIDTH];
                // Initialize board with initial fill
                for (int c = 0; c < BOARD_WIDTH; c++) {
                    for (int r = 0; r < initialFill[c]; r++) {
                        currentBoard[r][c] = 1;
                    }
                }

                // Calculate the landing row for the top of the piece
                int landingRow = BOARD_HEIGHT - pieceHeight; // Start checking from the highest possible position

                while (landingRow >= 0) {
                    boolean collision = false;
                    // Check for collision at the current landingRow
                    for (int i = 0; i < pieceHeight; i++) {
                        for (int j = 0; j < pieceWidth; j++) {
                            if (rotatedPiece[i][j] == 1) {
                                int boardRow = landingRow + i;
                                int boardCol = colOffset + j;

                                // Check for collision with board boundaries or existing blocks
                                if (boardRow < 0 || boardRow >= BOARD_HEIGHT || boardCol < 0 || boardCol >= BOARD_WIDTH || currentBoard[boardRow][boardCol] == 1) {
                                    collision = true;
                                    break;
                                }
                            }
                        }
                        if (collision) break;
                    }

                    if (collision) {
                        // If collision, the piece lands one row higher
                        landingRow++;
                        break;
                    }
                    // If no collision, the piece can fall further
                    landingRow--;
                }

                // After the loop, landingRow is the highest row the piece can occupy without collision.
                // If landingRow is -1, it means the piece goes off the top, which shouldn't happen with sufficient BOARD_HEIGHT,
                // but we adjust it to 0 if it goes below.
                 landingRow = Math.max(0, landingRow);


                // Place the piece on the currentBoard at the calculated landingRow
                boolean placed = true;
                for (int i = 0; i < pieceHeight; i++) {
                    for (int j = 0; j < pieceWidth; j++) {
                        if (rotatedPiece[i][j] == 1) {
                            int boardRow = landingRow + i;
                            int boardCol = colOffset + j;
                             if (boardRow >= BOARD_HEIGHT || boardCol >= BOARD_WIDTH || boardRow < 0 || boardCol < 0) {
                                // This case should ideally not be reached with correct landingRow calculation
                                placed = false;
                                break;
                            }
                            currentBoard[boardRow][boardCol] = 1;
                        }
                    }
                    if (!placed) break;
                }


                // If the piece was successfully placed
                if(placed) {
                    // Count completed lines after placing the piece
                    int linesCleared = 0;
                    List<Integer> fullRows = new ArrayList<>();
                    for (int r = 0; r < BOARD_HEIGHT; r++) {
                        boolean fullLine = true;
                        for (int c = 0; c < BOARD_WIDTH; c++) {
                            if (currentBoard[r][c] == 0) {
                                fullLine = false;
                                break;
                            }
                        }
                        if (fullLine) {
                            linesCleared++;
                            // No need to actually remove lines and shift down for just counting
                        }
                    }
                    maxLinesCleared = Math.max(maxLinesCleared, linesCleared);
                }
            }
        }

        return maxLinesCleared;
    }

    // Helper to print a board (for debugging)
    private void printBoard(int[][] board) {
        for (int r = BOARD_HEIGHT - 1; r >= 0; r--) {
            for (int c = 0; c < BOARD_WIDTH; c++) {
                System.out.print(board[r][c] == 1 ? "#" : ".");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
    	OtroTetris sol = new OtroTetris();

        // Example test cases
        String[] test1 = {"I", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        System.out.println("Test 1 (I on empty board): " + sol.tetrisMove(test1)); // Expected: 4

        String[] test2 = {"L", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        System.out.println("Test 2 (L on empty board): " + sol.tetrisMove(test2)); // Expected: 0

         String[] test3 = {"T", "0", "0", "0", "0", "0", "1", "1", "1", "1", "0", "0", "0"};
         System.out.println("Test 3 (T with some fill): " + sol.tetrisMove(test3)); // Expected: depends on T placement and fill. Need to manually verify.

        String[] test4 = {"I", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "0"};
        System.out.println("Test 4 (I with almost full bottom row): " + sol.tetrisMove(test4)); // Expected: 1 (horizontal I)

         String[] test5 = {"I", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"};
         System.out.println("Test 5 (I with full bottom row): " + sol.tetrisMove(test5)); // Expected: 0 (I cannot land) - Assuming landing on a full row is not possible. Let's assume it lands on top if possible.

        String[] test6 = {"I", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"}; // Same as test 1, double check
         System.out.println("Test 6 (I on empty board): " + sol.tetrisMove(test6)); // Expected: 4
    }
}
