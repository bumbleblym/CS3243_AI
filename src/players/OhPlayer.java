package players;

import main.FeatureFunctions;
import main.FeatureFunctions2;
import main.NextState;
import main.State;
import weightadjuster.GeneticAlgorithmAdjuster;
import weightadjuster.GeneticAlgorithmSD;

public class OhPlayer extends WeightedHeuristicPlayer {


    protected void configure() {
        features = new Feature[]{
                (n)->FeatureFunctions.lost(n),
                (n)->FeatureFunctions.bumpiness(n),
                (n)->FeatureFunctions.sumHeight(n), //
                // (n)->FeatureFunctions.numRowsCleared(n), //
                //(n)->FeatureFunctions.maxHeightDifference(n), //
                (n)->FeatureFunctions.numHoles(n),
                (n)->FeatureFunctions.sumHoleDistanceFromTop(n),
                (n)->FeatureFunctions.holeAndPitColumns(n),
                (n)->FeatureFunctions.holeAndPitColumnsMin(n),
                (n)->FeatureFunctions.topPerimeter(n),
                //(n)->FeatureFunctions.maxHeight(n),     //
                //(n)->FeatureFunctions.maxHeightPow(n, 3), //
                (n)->FeatureFunctions.sumEmptyCellDistanceFromTop(n),
                //(n)->FeatureFunctions.numFilledCells(n), //
                //(n)->FeatureFunctions.numRowsWithMoreThanOneEmptyCell(n), ////
                //(n)->FeatureFunctions.holeCoverEmptyCells(n), ////
                (n)->FeatureFunctions2.weightedFilledCells(n), //////
                (n)->FeatureFunctions2.deepestOneHole(n),
                (n)->FeatureFunctions2.sumOfAllHoles(n),
                (n)->FeatureFunctions2.horizontalRoughness(n),
                (n)->FeatureFunctions2.verticalRoughness(n),
                (n)->FeatureFunctions2.wellCount(n),
                (n)->FeatureFunctions2.weightedEmptyCells(n),
                (n)->FeatureFunctions2.highestHole(n),
                (n)->FeatureFunctions2.surfaceSmoothness(n),
                //(n)->FeatureFunctions2.totalHeightNewPiece(n), // messes up sequence database
                (n)->FeatureFunctions2.sumSquareWells(n),
                (n)->FeatureFunctions2.landingHeight(n)
                

                /*(n)->FeatureFunctions.lost(n),
                (n)->FeatureFunctions.numRowsCleared(n),
                (n)->FeatureFunctions2.totalHeightNewPiece(n),
                (n)->FeatureFunctions.sumHeight(n),
                (n)->FeatureFunctions.sumHoleDistanceFromTop(n),
                (n)->FeatureFunctions.holeAndPitColumns(n),
                (n)->FeatureFunctions.holeAndPitColumnsMin(n),
                (n)->FeatureFunctions2.deepestOneHole(n),
                (n)->FeatureFunctions2.highestHole(n),
                (n)->FeatureFunctions2.wellCount(n),
                (n)->FeatureFunctions2.sumSquareWells(n),
                (n)->FeatureFunctions.numHoles(n),
                (n)->FeatureFunctions2.weightedEmptyCells(n),
                (n)->FeatureFunctions2.horizontalRoughness(n),
                (n)->FeatureFunctions2.verticalRoughness(n),
                (n)->FeatureFunctions.bumpiness(n),
                (n)->FeatureFunctions.topPerimeter(n)*/
                
        };
    }
    
    /**
     * Genetic Algorithm Results
     * Features: lost, bumpiness, sumHeight, numRowsCleared, maxHeightDifference,
     *           numHoles, sumHoleDistanceFromTop, holeAndPitColumns, topPerimeter
                (n) -> FeatureFunctions.lost(n),
                (n) -> FeatureFunctions.bumpiness(n),
                (n) -> FeatureFunctions.sumHeight(n),
                (n) -> FeatureFunctions.numRowsCleared(n),
                (n) -> FeatureFunctions.maxHeightDifference(n),
                (n) -> FeatureFunctions.numHoles(n),
                (n) -> FeatureFunctions.sumHoleDistanceFromTop(n),
                (n) -> FeatureFunctions.holeAndPitColumns(n),
                (n) -> FeatureFunctions.topPerimeter(n)
     *  Hi-Score: 1561.0 | [2.0, -5.0, -150.0, 0.1, 0.5, -2.0, -80.0, -40.0, -80.0]
     *  State #17. Score = 1236.5   [-0.02, -20.0, -150.0, -2.0, 2.0, -2.0, -10.0, -5.0, -20.0]
     *  Hi-Score: 2340.5 | [-80.0, -5.0, 0.01, 0.02, 0.05, -20.0, -5.0, -2.0, -5.0]
     *  Hi-Score: 2259.0 | [-2.0, -100.0, -2.0, -0.5, 5.0, -500.0, -40.0, -70.0, -3.0]
     *  Hi-Score: 2525.0 | [-2.0, -100.0, -2.0, -0.01, 5.0, -500.0, -40.0, -70.0, -3.0]
     *  Hi-Score: 4496.5 | [-0.01, -100.0, -2.0, -0.5, 5.0, -500.0, -40.0, -70.0, -3.0]
     *  
     *  
     *  
                (n) -> FeatureFunctions.lost(n),
                (n) -> FeatureFunctions.bumpiness(n),
                (n) -> FeatureFunctions.sumHeight(n),
                (n) -> FeatureFunctions.numRowsCleared(n),
                (n) -> FeatureFunctions.maxHeightDifference(n),
                (n) -> FeatureFunctions.numHoles(n),
                (n) -> FeatureFunctions.sumHoleDistanceFromTop(n),
                (n) -> FeatureFunctions.holeAndPitColumns(n),
                (n) -> FeatureFunctions.holeAndPitColumnsMin(n),
                (n) -> FeatureFunctions.topPerimeter(n)
     *  Hi-Score: 8299.5 | [-99999.0, -100.0, -2.0, -0.5, 5.0, -500.0, -40.0, -70.0, 0.01, 0.5]
     *  Hi-Score: 7358.25 | [-99999.0, -100.0, -2.0, -0.5, 5.0, -500.0, -40.0, -70.0, -0.1, 2.0]
     *  Score =   6870.500 [  -100.00,    -2.00,    -0.50,     5.00,  -500.00,   -40.00,   -70.00,     0.01,    -3.00]
     *  Hi-Score: 12067.75 | [-99999.0, -100.0, -2.0, -0.5, 5.0, -500.0, -40.0, -70.0, -0.1, -3.0]
     *  Hi-Score: 13924.0 | [-99999.0, -100.0, -2.0, -0.01, 5.0, -500.0, -40.0, -70.0, -40.0, -3.0]
     *  Hi-Score: 18838.75 | [-99999.0, -100.0, -2.0, -0.01, 5.0, -500.0, -40.0, -70.0, -40.0, -1.0]
     *  Hi-Score: 24683.75 | [-99999.0, -100.0, -2.0, -0.5, 5.0, -500.0, -40.0, -70.0, -40.0, -3.0]
     *
     *  new float[]{-99999.0f, -100.0f, -2.0f, -0.5f, 5.0f, -500.0f, -40.0f, -70.0f, -40.0f, -3.0f};
     */
    protected void initialiseWeights() {
        weights = new float[features.length];
        weights = new float[]{-9999999f, -2002f, 1883f, -1111f, -1386f, -1061f, -1922f, -702f, 1397f, -78f, -1651f, -1576f, -1963f, -844f, 57f, 910f, -1569f, 653f, -11f, -7f};
        //weights = new float[]{-9999999f, -1835f, 1886f, 1881f, -1353f, -1668f, -1782f, -693f, 1304f, -77f, 873f, -1623f, -1990f, -805f, 752f, 901f, -1421f, 632f, -7f, -10f};
    };
    
    /**
     * return the min max value of feature
     */
    public static Feature minMax(Feature feature) {
        return (NextState nextState) -> {
            float worstPiece = Float.POSITIVE_INFINITY;
            for (int i = 0; i < State.N_PIECES; ++i) {
                float bestMove = Float.NEGATIVE_INFINITY;
                int[][] legalMoves = NextState.legalMoves[i];
                for (int j=0; j<legalMoves.length; ++j) {
                    NextState ns = NextState.generate(nextState,i,legalMoves[j]);
                    float score = feature.compute(ns);
                    if (score > bestMove) {
                        bestMove = score;
                        if (bestMove >= worstPiece) {
                            j = legalMoves.length; // break;
                            //break;
                        }
                    }
                }
                if (bestMove < worstPiece) {
                    worstPiece = bestMove;
                }
            }
            return worstPiece;
        };
    }


    
    public static void main(String[] args) {
        int choice = -2; // 0 to watch, 1 to learn.

        WeightedHeuristicPlayer p = new OhPlayer();
        //WeightAdjuster adjuster = new SmoothingAdjuster(p.dim());
        //adjuster.fixValue(1, -0f);
        //adjuster.fixValue(7, -0f);
        //adjuster.fixValue(2, -5f);
        //adjuster.fixValue(3, -1f);
        //adjuster.fixValue(4, -1f);
        //adjuster.fixValue(5, 1000f);
        //adjuster.fixValue(6, 0f);
       
        //p.switchToMinimax(1);
        switch(choice) {
            case -2:
                checkAverageScore(p, 20);break;
            case -1:
                checkScore(p);break;
            case 0:
                watch(p);break;
            case 1:
                GeneticAlgorithmAdjuster adjuster = new GeneticAlgorithmSD(p, p.dim(), 20);
                adjuster.fixValue(0, -9999999f);
                learn(adjuster);break;
                //learn(p, adjuster);break;
            case 2:
                record(p);break;
            case 3:
                int[] pState = new int[0];
                System.out.println(pState.length);
                watchWithPredeterminedState(p, pState);break;
        }
    }
}
