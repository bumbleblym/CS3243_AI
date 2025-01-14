package main;

import java.util.Random;

/**
 * Extensions of the feature functions
 */
public class FeatureFunctions2 {
    /**
     * Return weighted filled cells. cells at row-i costs i.
     */
    public static float weightedFilledCells(NextState nextState) {
        int filledCells = 0;
        int field[][] = nextState.field;
        for (int i = 0; i < State.ROWS; ++i) {
            for (int j = 0; j < State.COLS; ++j) {
                if (field[i][j] != 0) {
                    filledCells += (i + 1);
                }
            }
        }
        return filledCells;
    }
    
    /**
     *  The depth of the deepest hole (a width-1 hole with filled spots on both sides)
     */
    public static float deepestOneHole(NextState nextState) {
        int field[][] = nextState.field;
        for (int i = 0; i < State.ROWS; ++i) {
            for (int j = 0; j < State.COLS; ++j) {
                int left = (j - 1 < 0 ? 1 : field[i][j-1]);
                int right = (j + 1 >= State.COLS ? 1 : field[i][j+1]);
                if (field[i][j] == 0 && left == 1 && right == 1) {
                    return State.ROWS - i;
                }
            }
        }
        return 0;
    }
    
    /**
     *  The total depth of all the holes on the game board
     */
    public static float sumOfAllHoles(NextState nextState) {
        int holesSum = 0;
        int field[][] = nextState.field;
        int top[] = nextState.top;
        for (int j = 0; j < State.COLS; ++j) {
            for (int i = 0; i < top[j]; ++i) {
                if (field[i][j] == 0) {
                    holesSum += (State.ROWS - i);
                }
            }
        }
        return holesSum;
    }
    
    /**
     * Horizontal Roughness - The number of times a spot alternates between an empty and a filled status, going by rows
     */
    public static float horizontalRoughness(NextState nextState) {
        int horizontalRoughness = 0;
        int field[][] = nextState.field;
        for (int i = 0; i < State.ROWS; ++i) {
            for (int j = 1; j < State.COLS; ++j) {
                if (field[i][j] != field[i][j-1]) {
                    ++horizontalRoughness;
                }
            }
        }
        return horizontalRoughness;
    }
    
    /**
     * Vertical Roughness - The number of times a spot alternates between an empty and a filled status, going by columns
     */
    public static float verticalRoughness(NextState nextState) {
        int verticalRoughness = 0;
        int field[][] = nextState.field;
        for (int j = 0; j < State.COLS; ++j) {
            for (int i = 1; i < State.ROWS; ++i) {
                if (field[i][j] != field[i-1][j]) {
                    ++verticalRoughness;
                }
            }
        }
        return verticalRoughness;
    }
    
    /**
     * The number of holes that are 3 or more blocks deep
     */
    public static float wellCount(NextState nextState) {
        int numberOfWells = 0;
        int field[][] = nextState.field;
        int top[] = nextState.top;
        for (int j = 0; j < State.COLS; ++j) {
            for (int i = 0; i + 2 < top[j]; ++i) {
                if (field[i][j] == 0 && (i == 0 || field[i-1][j] == 1)) {
                    if (field[i+1][j] == 0 && field[i+2][j] == 0) {
                        ++numberOfWells;
                    }
                }
            }
        }
        return numberOfWells;
    }
    
    /**
     * Weighted empty cells
     */
    public static float weightedEmptyCells(NextState nextState) {
        int emptyCells = 0;
        int field[][] = nextState.field;
        int top[] = nextState.top;
        for (int j = 0; j < State.COLS; ++j) {
            for (int i = 0; i < top[j]; ++i) {
                if (field[i][j] == 0) {
                    emptyCells += State.ROWS - i;
                }
            }
        }
        return emptyCells;
    }
    
    /**
     * The height of the highest hole on the board
     */
    public static float highestHole(NextState nextState) {
        int field[][] = nextState.field;
        int top[] = nextState.top;
        for (int i = State.ROWS - 1; i >= 0; --i) {
            for (int j = 0; j < State.COLS; ++j) {
                if (field[i][j] == 0 && i < top[j]) {
                    return i;
                }
            }
        }
        return 0;
    }
    
    /**
     * Number of rows cleared by current move
     */
    public static float clearLines(NextState nextState) {
        return nextState.rowsCleared;
    }
    
    /**
     * (h2 – h1) + |h2 – h3| + |h3 – h4| + |h4 – h5| + |h5 – h6| + |h6 – h7| + |h7 – h8| + |h8 – h9| + (h9 – h10)
     */
    public static float surfaceSmoothness(NextState nextState) {
        int surfaceSmoothness = 0;
        int top[] = nextState.top;
        surfaceSmoothness += top[1] - top[0];
        surfaceSmoothness += top[State.COLS-1] - top[State.COLS-2];
        for (int i = 1; i < State.COLS - 2; ++i) {
            surfaceSmoothness += Math.abs(top[i] - top[i+1]);
        }
        return surfaceSmoothness;
    }
    
    /**
     * for every well of depth w, sum 1 + 2 + 3 + ... + w
     */
    public static float sumSquareWells(NextState nextState) {
        int sum = 0;
        int top[] = nextState.top;
        int field[][] = nextState.field;
        for (int j = 0; j < State.COLS; ++j) {
            int wellDepth = 0;
            for (int i = 0; i < top[j]; ++i) {
                if (field[i][j] == 0) {
                    ++wellDepth;
                    sum += wellDepth;
                } else {
                    wellDepth = 0;
                }
            }
        }
        return sum;
    }
    
    public static float sumWellDepths(NextState nextState) {
    	int sum = 0;
        int top[] = nextState.top;
        int field[][] = nextState.field;
        for (int j = 0; j < State.COLS; ++j) {
            int wellDepth = 0;
            for (int i = 0; i < State.ROWS; ++i) {
                if (field[i][j] == 0) {
                    ++wellDepth;
                    boolean wallLeft = (j == 0 || field[i][j-1] != 0);
                    boolean wallRight = (j == State.COLS-1 || field[i][j+1] != 0);
                    if (wallLeft && wallRight) {
                    	sum += wellDepth;
                    }
                } else {
                    wellDepth = 0;
                }
            }
        }
        return sum;
    }
    
    /**
     * compute the lowest and highest position of new piece. compute the total
     */
    public static float tetrominoHeight(NextState nextState) {
        int fieldBeforeCleared[][] = nextState.fieldBeforeCleared;
        if (nextState.lost) {
        	return State.ROWS;
        }
        int minHeightPiece = Integer.MAX_VALUE;
        int maxHeightPiece = Integer.MIN_VALUE;
        for (int i = 0; i < State.ROWS; ++i) {
            for (int j = 0; j < State.COLS; ++j) {
                if (fieldBeforeCleared[i][j] == nextState.turn) {
                    minHeightPiece = Math.min(minHeightPiece,i);
                    maxHeightPiece = Math.max(maxHeightPiece,i);
                }
            }
        }
        return (float)(maxHeightPiece + minHeightPiece) / 2f;
    }
    
    /**
     * return the landing height of the new piece
     */
    public static float landingHeight(NextState nextState) {
    	int fieldBeforeCleared[][] = nextState.fieldBeforeCleared;
    	if (nextState.lost) {
        	return State.ROWS;
        }
    	for (int i = 0; i < State.ROWS; ++i) {
    		for (int j = 0; j < State.COLS; ++j) {
    			if (fieldBeforeCleared[i][j] == nextState.turn) {
    				return i;
    			}
    		}
    	}
    	return 0;
    }
    
    public static float rowTransitions(NextState nextState) {
    	int sum = 0;
    	int field[][] = nextState.field;
    	for (int i = 0; i < State.ROWS; ++i) {
    		for (int j = 0; j < State.COLS; ++j) {
    			boolean wallLeft = (j == 0 || field[i][j-1] != 0);
    			boolean wallNow = (field[i][j] != 0);
    			if (wallLeft ^ wallNow) {
    				++sum;
    			}
    		}
    		if (field[i][State.COLS - 1] == 0) {
    			++sum;
    		}
    	}
    	return sum;
    }
    
    public static float columnTransitions(NextState nextState) {
    	int sum = 0;
    	int field[][] = nextState.field;
    	for (int j = 0; j < State.COLS; ++j) {
    		for (int i = 0; i < State.ROWS; ++i) {
    			boolean wallDown = (i == 0 || field[i-1][j] != 0);
    			boolean wallNow = (field[i][j] != 0);
    			if (wallDown ^ wallNow) {
    				++sum;
    			}
    		}
    	}
    	return sum;
    }
}
