package players;

import weightadjuster.SmoothingAdjuster;
import weightadjuster.WeightAdjuster;
import main.FeatureFunctions;
import main.NextState;
import main.State;

public class OhPlayer extends WeightedHeuristicPlayer {


    protected void configure() {
        features = new Feature[]{
                (s,n)->FeatureFunctions.lost(s,n),
                minMax((s,n)-> {
                    float total = 0;
                    total += FeatureFunctions.totalHoles(s,n);
                    total += 1f*FeatureFunctions.bumpiness(s,n);
                    total += 8f*FeatureFunctions.totalColumnsHeight(s,n);
                    total -= 10f*FeatureFunctions.completedLines(s,n);
                    total += 2f*FeatureFunctions.differenceHigh(s,n);
                    total += 20f*FeatureFunctions.totalHolePieces(s,n);
                    
                    //System.out.println(FeatureFunctions.totalHoles(s,n) - FeatureFunctions.totalHolePieces(s,n));
                    
                    return total;
                })
                //(s,n)->FeatureFunctions.maximumColumnHeight(s,n),
                //(s,n)->FeatureFunctions.totalHoles(s,n),
                //(s,n)->FeatureFunctions.totalColumnsHeight(s,n),
                //(s,n)->FeatureFunctions.bumpiness(s, n),
                //(s,n)->FeatureFunctions.completedLines(s, n),
                //(s,n)->FeatureFunctions.totalFilledCells(s, n),
                //(s,n)->FeatureFunctions.minMaximumColumnHeight(s, n),
                //(s,n)->FeatureFunctions.minMaxTotalHoles(s, n),
                //(s,n)->FeatureFunctions.differenceHigh(s, n)
        };
    }
    
    protected void initialiseWeights() {
        weights = new float[features.length];
        weights = new float[]{-99999.0f, -5f};
        //weights = new float[]{-99999.0f, -0.0f, -72.27131f, -0.39263827f, -18.150364f, 1.9908575f, -4.523054f, 2.6717715f}; // <-- good weights.
        //weights = new float[]{-99999.0f, -0.0f, -80.05821f, 0.2864133f, -16.635815f, -0.0488357f, -2.9707198f, -1f, -1f, -1f}; // <-- good weights.
        //weights = new float[]{-99999.0f, -1, -4, -95};
    }

    /**
     * return the min max value of highest column height
     */
    public static Feature minMax(Feature feature) {
        return (State state, NextState nextState) -> {
            float worstPiece = Float.POSITIVE_INFINITY;
            for (int i = 0; i < State.N_PIECES; ++i) {
                float bestMove = Float.NEGATIVE_INFINITY;
                int[][] legalMoves = NextState.legalMoves[i];
                for (int j=0; j<legalMoves.length; ++j) {
                    NextState ns = NextState.generate(nextState,i,legalMoves[j]);
                    float score = feature.compute(state,ns);
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
    /**
     * return the min max value of highest column height
     */
    public static Feature minMaxTwo(Feature feature) {
        return (State state, NextState nextState) -> {
            float worstPiece = Float.POSITIVE_INFINITY;
            for (int i = 0; i < State.N_PIECES; ++i) {
                float bestMove = Float.NEGATIVE_INFINITY;
                int[][] legalMoves = NextState.legalMoves[i];
                for (int j=0; j<legalMoves.length; ++j) {
                    NextState ns = NextState.generate(nextState,i,legalMoves[j]);
                    float score = feature.compute(state,ns);
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
        int choice = 0; // 0 to watch, 1 to learn.

        WeightedHeuristicPlayer p = new OhPlayer();
        WeightAdjuster adjuster = new SmoothingAdjuster(p.dim());
        adjuster.fixValue(0, -99999f);
        //adjuster.fixValue(1, -0f);
        //adjuster.fixValue(7, -0f);
        //adjuster.fixValue(2, -5f);
        //adjuster.fixValue(3, -1f);
        //adjuster.fixValue(4, -1f);
        //adjuster.fixValue(5, 1000f);
        //adjuster.fixValue(6, 0f);
        
        switch(choice) {
            case 0:
                watch(p);break;
            case 1:
                learn(p, adjuster);break;
        }
    }
}
