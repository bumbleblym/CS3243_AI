package weightadjuster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import main.ResultDump;
import players.WeightedHeuristicPlayer;

/**
 * Main Genetic Algorithm that we used 
 */
public class GeneticAlgorithmAdjuster {
    protected static Random rand = new Random();
    protected int dim;
    protected int realDim;
    protected float[][] states;
    protected float[] scores;
    protected WeightedHeuristicPlayer w;
    protected PartialGamePlayer p;
    protected int stateNumber;
    protected final int INITIAL_GOOD_STATES = 1;
    protected int PRINT_INTERVAL = 10;
    
    protected float total = 0;
    
    protected final int MUTATION_BITS = 2;
    protected double mutationProbability = 0.2;
    //protected double mutationProbability = 0.4;
    protected HashMap<Integer,Float> fixedValue = new HashMap<>(); 
    
    //protected float[] highScoreWeights;
    //protected float highScore;
    protected int STALE_HEAP_THRESHOLD = 400; // terminate after 9 iterations of no improvement
    protected WeightHeap bestHeap = new WeightHeap(10);
    
    //protected static float[] conversionTable = new float[]{0.01f, 0.02f, 0.05f, 0.1f, 0.5f, 1f, 2f, 3f, 5f, 10f, 20f, 40f, 70f, 100f, 500f, 8000};
    //protected static float[] conversionTable = new float[]{0.01f, 0.05f, 0.3f, 1.5f, 4f, 10f, 30f, 60f, 90f, 140, 200, 300, 500, 1000, 1500, 6000};
    //protected static float[] conversionTable = new float[]{0.01f, 0.05f, 0.3f, 1.5f, 4f, 10f, 30f, 60f, 90f, 140, 200, 250,300,350,400,500,700,850, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800, 3000, 4000, 5000, 6000};
    protected static final int WORD_SIZE = 12;
    protected static final int HALF_TABLE_SIZE = pow2(WORD_SIZE-1);
    private static final int MUTATE_LENGTH_MAX = 6;
    
    private static int pow2(int n) {
        int v = 1;
        for (; n > 0; --n) v *= 2;
        return v;
    }
    
    public GeneticAlgorithmAdjuster(WeightedHeuristicPlayer w, int _dim, int N) {
        dim = _dim;
        realDim = dim;
        stateNumber = N;
        this.w = w;
        states = new float[stateNumber][];
        for (int i = 0; i < stateNumber; ++i) {
            states[i] = new float[dim];
        }
        scores = new float[stateNumber];
    }
    
    public void fixValue(int position, float value) {
        fixedValue.put(position, value);
        --dim;
        for (int i = 0; i < stateNumber; ++i) {
            states[i] = new float[dim];
        }
    }
    
    float[] generateRealWeights(float[] state) {
        float[] realWeights = new float[realDim];
        int index = 0;
        for (int i = 0; i < realDim; ++i) {
            if (fixedValue.containsKey(i)) {
                realWeights[i] = fixedValue.get(i);
            } else {
                realWeights[i] = state[index++];
            }
        }
        return realWeights;
    }
    
    protected float[] generateState(float[] realWeights) {
        float[] state = new float[dim];
        int index = 0;
        for (int i = 0; i < realDim; ++i) {
            if (!fixedValue.containsKey(i)) {
                state[index++] = realWeights[i];
            }
        }
        return state;
    }
    
    protected float[] randomStateFromHeap() {
        return generateState(bestHeap.getRandomWeights());
    }
    
    protected float[] generateRandomState(int length) {
        boolean[] encoded = new boolean[length*WORD_SIZE];
        for (int i=0; i<encoded.length; ++i) {
            encoded[i] = rand.nextBoolean();
        }
        return decode(encoded);
    }
    
    protected void generateRandomStates() {
        generateMostlyRandomStates();
    }
    
    private void generateTotallyRandomStates() {
        for (int i = 0; i < stateNumber; ++i) {
            states[i] = generateRandomState(dim);
        }
    }
    
    private void generateMostlyRandomStates() {
        for (int i=0; i<INITIAL_GOOD_STATES; ++i) {
            states[i] = generateInitialGoodState();
        }
        for (int i = INITIAL_GOOD_STATES; i < stateNumber; ++i) {
            states[i] = generateRandomState(dim);
        }
    }
    
    private float[] generateInitialGoodState() {
        float[][] goodWeights = new float[][] {
                new float[]{-1224f, -133f, -424f, -2026f, -832f, -663f, 0}
        };
        int choice = rand.nextInt(goodWeights.length);
        float[] weights = goodWeights[choice];
        if (weights.length != dim) {
            //generateRandomState(dim);
            throw new UnsupportedOperationException("ERROR!!! WRONG DIM!");
        }
        return weights;
    }
    
    protected void selection() {
        float totalScore = 0;
        while (totalScore == 0) {
            for (int i = 0; i < states.length; ++i) {
                float[] realWeights = generateRealWeights(states[i]);
                float result = w.playPartialWithWeights(realWeights,50);
                //float result = w.playWithWeights(realWeights, 15);
                scores[i] = result;
                //System.out.println(i + " " + scores[i]);
                totalScore += scores[i];
            }
            if (totalScore == 0) {
                generateRandomStates();
            }
        }
        
        Integer[] ranked = new Integer[scores.length];
        for (int i=0; i<ranked.length; ++i) ranked[i] = i;
        Arrays.sort(ranked, (a, b) -> {
            float cmp = scores[b] - scores[a];
            if (cmp < 0) return -1;
            else if (cmp == 0) return 0;
            else return 1;
        });
        float[] probability = new float[scores.length];
        int totalProb = 0;
        for (int i=0; i<probability.length; ++i) {
            probability[i] = scores.length-i;
            totalProb += scores.length-i;
        }
        for (int i=0; i<probability.length; ++i) {
            probability[i] /= totalProb;
        }
        for (int i=0; i<ranked.length; ++i) {
            scores[ranked[i]] = probability[i];
            //System.out.println(i + " | " + ranked[i] + " | " + scores[ranked[i]] + " | " + probability[i]);
        }
        //states[ranked[ranked.length-1]] = generateRandomState(states[0].length);
        states[ranked[ranked.length-1]] = randomStateFromHeap();
        
        
        
        /*for (int i = 0; i < states.length; ++i) {
            scores[i] /= totalScore;
        }*/
        float[][] newStates = new float[stateNumber][];
        for (int i = 0; i < states.length; ++i) {
            double random = rand.nextDouble();
            for (int j = 0; j < states.length; ++j) {
                random -= scores[j];
                if (random < 0) {
                    newStates[i] = states[j];
                    break;
                }
            }
        }
        states = newStates;
    }
    
    protected void crossover() {
        for (int i = 0; i + 1 < states.length; i += 2) {
            boolean[] firstBitString = encode(states[i]);
            boolean[] secondBitString = encode(states[i+1]);
            int position = rand.nextInt(firstBitString.length);
            int position2 = rand.nextInt(firstBitString.length);
            if (position2 < position) {
                int temp = position2;
                position2 = position;
                position = temp;
            }
            
            for (int j = position; j < position2; ++j) {
                boolean temp = firstBitString[j];
                firstBitString[j] = secondBitString[j];
                secondBitString[j] = temp;
               
            }
            states[i] = decode(firstBitString);
            states[i+1] = decode(secondBitString);
        }
    }
    
    protected boolean maybeReset(int iteration) {
        if (bestHeap.consecutiveRejects <= STALE_HEAP_THRESHOLD) return false;
        
        String message = "Best scores heap is stale. " + bestHeap.consecutiveRejects +
                " consecutive rejects. RESETTING STATES AND SAVING...";
        System.out.print(message);
        message += "\nTerminated at iteration " + iteration + ".\nScores:\n" + bestHeap.toString() + "\n";
        ResultDump.saveResults(message);
        generateTotallyRandomStates();
        bestHeap = new WeightHeap(bestHeap.size());
        return true;
    }
    
    
    protected int mutationBits() {
        return MUTATION_BITS;
    }
    
    protected void mutation() {
        int mutationBits = mutationBits();
        for (int i = 0; i < states.length; ++i) {
            boolean[] bitString = encode(states[i]);
            for (int j=0; j<mutationBits; ++j) {
                int mutateLength = rand.nextInt(MUTATE_LENGTH_MAX);
                int position = rand.nextInt(bitString.length-mutateLength);
                double prob = rand.nextDouble();
                if (prob < mutationProbability) {
                    //System.out.println("MUTATION!");
                    for (int k=0; k<mutateLength; ++k)
                        bitString[position+k] ^= true;
                }
            }
            states[i] = decode(bitString);
        }
    }

    public static boolean[] binToGray(boolean[] bin) {
        boolean[] newBin = Arrays.copyOf(bin, bin.length);
        int words = newBin.length/WORD_SIZE;
        for (int i=words-1; i>=0; --i) {
            int offset = i*WORD_SIZE;
            for (int j=0; j<WORD_SIZE-1; ++j) {
                newBin[offset+j] = newBin[offset+j] ^ newBin[offset+j+1];
            }
        }
        return newBin;
    }
    
    public static boolean[] grayToBin(boolean[] bin) {
        boolean[] newBin = Arrays.copyOf(bin, bin.length);
        int words = newBin.length/WORD_SIZE;
        for (int i=words-1; i>=0; --i) {
            int offset = i*WORD_SIZE;
            for (int j=WORD_SIZE-2; j>=0; --j) {
                newBin[offset+j] = newBin[offset+j] ^ newBin[offset+j+1];
            }
        }
        return newBin;
    }
    
    public static String bitString(boolean[] encoded) {
        StringBuilder sb = new StringBuilder(encoded.length);
        for (boolean b : encoded) {
            sb.append(b ? "1" : "0");
        }
        return sb.toString();
    }

    public static int encodeFloat(float f) {
        return (int)(f + HALF_TABLE_SIZE);
        
        /*boolean neg = f < 0;
        if (neg) f = -f;
        
        int index = 0;
        while (index+1 < conversionTable.length && f > conversionTable[index]) {
            index++;
        }
        if (neg) return conversionTable.length-index-1;
        else return conversionTable.length+index;*/
    }

    public static float decodeFloat(int v) {
        return v - HALF_TABLE_SIZE;
        /*boolean neg = v < HALF_TABLE_SIZE;
        if (neg) v = HALF_TABLE_SIZE - v - 1;
        else v -= HALF_TABLE_SIZE;
        if (neg) return -conversionTable[v];
        else return conversionTable[v];*/
    }

    public static boolean[] encode(float[] array) {
        boolean[] encoded = new boolean[array.length*WORD_SIZE];
        for (int i=0; i<array.length; ++i) {
            int offset = i*WORD_SIZE;
            int v = encodeFloat(array[i]);
            int index = 0;
            while (v > 0) {
                encoded[offset+index] = v%2 == 1;
                v /= 2;
                index++;
            }
        }
        //return encoded;
        return binToGray(encoded);
    }

    public static float[] decode(boolean[] encoded) {
        encoded = grayToBin(encoded);
        float[] decoded = new float[encoded.length/WORD_SIZE];
        for (int i=0; i<decoded.length; ++i) {
            int offset = i*WORD_SIZE;
            int v = 0;
            for (int j = WORD_SIZE-1; j>=0; --j) {
                v *= 2;
                if (encoded[offset+j]) v++;
            }
            decoded[i] = decodeFloat(v);
        }
        return decoded;
    }
    
    protected float playWithWeights(float[] realWeights, int times) {
        return w.playWithWeights(realWeights, 2);
    }
    
    public void adjust() {
        generateRandomStates();
        int iteration = Integer.MAX_VALUE;
        for (int i = 0; i < iteration; ++i) {
            System.out.println("Iteration " + i);
            selection();
            crossover();
            mutation();
            
            if ((i+1)%PRINT_INTERVAL == 0) {
                //System.out.println("Iteration " + i);
                float total = 0;
                for (int j = 0; j < stateNumber; ++j) {
                    float[] realWeights = generateRealWeights(states[j]);
                    
                    float result = printAndReturnResult(j, realWeights);
                    
                    total += result;
                    
                    bestHeap.tryInsert(result, realWeights);
                    /*if (result > highScore) {
                        highScoreWeights = Arrays.copyOf(realWeights, realWeights.length);
                        highScore = result;
                    }*/
                    
                }
                printTotalAndHighScore(total);
            }
        }
    }

    protected void printTotalAndHighScore(float total) {
        System.out.println("Average Score: " + (total/stateNumber));
        System.out.println(bestHeap);
        //System.out.println("Hi-Score: " + highScore + " | " + Arrays.toString(highScoreWeights));
    }

    protected float printAndReturnResult(int j, float[] realWeights) {
        float result = playWithWeights(realWeights, 2);
        float resultPartial = w.playPartialWithWeights(realWeights, 10);

        printResults(j, result, resultPartial);
        return result;
    }

    protected void printResults(int stateNo, float result, float resultPartial) {
        System.out.printf("State #%2d. Score = %10.3f - %8.3f [",stateNo,result,resultPartial);
        for (int k = 0; k < states[stateNo].length; ++k) {
            System.out.printf("%9.2f", states[stateNo][k]);
            if (k + 1 < states[stateNo].length) {
                System.out.printf(",");
            } else {
                System.out.println("]");
            }
        }
    }
    
}

/**
 * A heap to contain the k best weights (+ scores) found so far.
 */
class WeightHeap {
    private float[] scores;
    private float[][] weights;
    private int size;
    private static Random rand = new Random();
    public int consecutiveRejects;

    public WeightHeap(int size) {
        this.size = size;
        weights = new float[size][];
        scores = new float[size];
        consecutiveRejects = 0;
    }
    
    public void tryInsert(float score, float[] weights) {
        if (score < scores[0]) {
            //System.out.println(consecutiveRejects);
            consecutiveRejects++;
            return;
        }
        
        consecutiveRejects = 0;
        this.scores[0] = score;
        this.weights[0] = Arrays.copyOf(weights, weights.length);
        bubbleDown();
    }
    
    private void bubbleDown() {
        int current = 0;
        while(true) {
            int left = 2*current+1;
            if (left >= size)
                break;
            int target = left;
            int right = 2*current+2;
            if (right < size)
                target = minValue(left, right);
            if (minValue(current, target) == current) {
                break; // stop bubbling down.
            } else {
                // swap weights
                float[] tempF = weights[current];
                weights[current] = weights[target];
                weights[target] = tempF;
                // swap scores
                float tempI = scores[current];
                scores[current] = scores[target];
                scores[target] = tempI;
                
                current = target;
            }
        }
    }
    
    private int minValue(int i1, int i2) {
        if (scores[i1] <= scores[i2])
            return i1;
        return i2;
    }
    
    public int size() {
        return size;
    }
    
    @Override
    public String toString() {
        String sep = "";
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<size; ++i) {
            String scoreString = scores[i] + " - ";
            sb.append(sep);
            sb.append(scoreString);
            for (int j=scoreString.length(); j<12; ++j) sb.append(" ");
            sb.append(Arrays.toString(weights[i]));

            sep = "\n";
        }
        
        return sb.toString();
    }
    
    
    public float[] getRandomWeights() {
        int nWeights = 0;
        for (int i=0; i<weights.length; ++i) {
            if (weights[i] != null) nWeights++;
        }
        if (nWeights == 0) throw new UnsupportedOperationException("Heap empty???");
        
        int count = rand.nextInt(nWeights);
        for (int i=0; i<weights.length; ++i) {
            if (weights[i] != null) {
                if (count > 0) {
                    count--;
                } else{
                    return Arrays.copyOf(weights[i], weights[i].length);
                }
            }
        }
        throw new UnsupportedOperationException("Not supposed to reach here.");
    }
}
