import TileState.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Calculator {
    public final static int MONTE_CARLO_PREDICTION = 0;
    public final static int ALPHA_BETA_PRUNING = 1;
    public final static int RANDOM = 2;

    public final static int BEFORE_MOV_CACHE_INDEX = 0;
    public final static int RAND_GEN_CACHE_INDEX = 1;
    public final static int CACHE_ARRAY_LENGTH = 2;

    public final static int STRATEGY_THRESHOLD = 8;

    public static HashMap<Integer, String> algoNames = new HashMap();
    public static HashMap<Integer, ActionResult>[] cacheMapArray;
    public static Random rand = new Random();

    static {
        cacheMapArray = new HashMap[CACHE_ARRAY_LENGTH];
        for (int i = 0; i < CACHE_ARRAY_LENGTH; i++) {
            cacheMapArray[i] = new HashMap();
        }
        algoNames.put(MONTE_CARLO_PREDICTION, "Monte Carlo Prediction");
        algoNames.put(ALPHA_BETA_PRUNING, "Alpha Beta Pruning");
        algoNames.put(RANDOM, "Random");
    }


    public static int moveAlgorithm = MONTE_CARLO_PREDICTION;
    public static int genAlgorithm = RANDOM;


    public static void show() {
        System.out.println("Moving strategy: " + algoNames.get(moveAlgorithm));
        System.out.println("Generating strategy: " + algoNames.get(genAlgorithm));
    }

    public static ActionResult searchOpponent(TileState ts, int depth) {
        if (!(genAlgorithm == ALPHA_BETA_PRUNING) || ts.maxValue < 4*STRATEGY_THRESHOLD) {
            return randomSearch(ts);
        }
        clearCache();
        return alphaBetaMinValue(ts, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
    }

    public static ActionResult search(TileState ts, int depth) {
        if (moveAlgorithm == RANDOM || ts.maxValue < STRATEGY_THRESHOLD) {
            return randomSearch(ts);
        }

        clearCache();
        if (moveAlgorithm == MONTE_CARLO_PREDICTION) {
            return maxAvgValue(ts, depth);
        } else if (moveAlgorithm == ALPHA_BETA_PRUNING) {
            return alphaBetaMaxValue(ts, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
        }
        return null;
    }

    protected static ActionResult createActionResult(TileState ts, int depth) {
        ActionResult result = new ActionResult(ts);
        if (TileState.terminalTest(ts) || depth == 0) {
            result.isEnd = true;
            return result;
        }

        ActionResult cache = getResultFromCache(ts);
        if (cache != null) {
            return cache;
        }
        return result;
    }

    protected static ActionResult randomSearch(TileState ts) {
        ActionResult ar = new ActionResult(ts);
        ar.action = ts.validActions[rand.nextInt(1000) % ts.validActions.length];
        return ar;
    }

    protected static ActionResult maxAvgValue(TileState ts, int depth) {
        ActionResult result = createActionResult(ts, depth);
        if (result.isEnd) {
            return result;
        }

        result.value = Integer.MIN_VALUE;
        for (int i : ts.validActions) {
            TileState curState = ts.makeAction(i);
            ActionResult ar = avgValue(curState, depth - 1);
            if (ar.value > result.value) {
                result.value = ar.value;
                result.action = i;
            }
        }

        result.isEnd = true;
        putResult2Cache(ts, result);
        return result;
    }

    protected static ActionResult avgValue(TileState ts, int depth) {
        ActionResult result = createActionResult(ts, depth);
        if (result.isEnd) {
            return result;
        }

        result.value = 0;
        for (int i : ts.validActions) {
            TileState curState = ts.makeAction(i);
            ActionResult ar = maxAvgValue(curState, depth - 1);
            result.value += ar.value;
        }
        result.value /= ts.validActions.length;

        result.isEnd = true;
        putResult2Cache(ts, result);
        return result;
    }

    /*
      Min-Max search with alpha beta pruning strategy
    */
    protected static ActionResult alphaBetaMaxValue(TileState ts, int alpha, int beta, int depth) {
        ActionResult result = new ActionResult(ts);
        if (TileState.terminalTest(ts) || depth == 0) {
            return result;
        }
        result.value = Integer.MIN_VALUE;

        for (int i : ts.validActions) {

            TileState curState = ts.makeAction(i);
            ActionResult ar = alphaBetaMinValue(curState, alpha, beta, depth - 1);

            if (ar.value > result.value) {
                result.value = ar.value;
                result.action = i;
                if (result.value > beta) {
                    break;
                }
            }

            alpha = Integer.max(alpha, result.value);

        }
        return result;
    }

    protected static ActionResult alphaBetaMinValue(TileState ts, int alpha, int beta, int depth) {
        ActionResult result = new ActionResult(ts);

        if (TileState.terminalTest(ts) || depth == 0) {
            return result;
        }

        result.value = Integer.MAX_VALUE;

        for (int i : ts.validActions) {

            TileState curState = ts.makeAction(i);
            ActionResult ar = alphaBetaMaxValue(curState, alpha, beta, depth - 1);

            if (ar.value < result.value) {
                result.value = ar.value;
                result.action = i;
                if (result.value < alpha) {
                    break;
                }
            }

            beta = Integer.min(beta, result.value);

        }

        return result;
    }

    protected static void clearCache() {
        for (int i = 0; i < CACHE_ARRAY_LENGTH; i++) {
            cacheMapArray[i].clear();
        }
    }

    protected static HashMap<Integer, ActionResult> getCacheMap(TileState ts) {
        if (ts instanceof MovingState) {
            return cacheMapArray[BEFORE_MOV_CACHE_INDEX];
        }
        return cacheMapArray[RAND_GEN_CACHE_INDEX];
    }

    protected static ActionResult getResultFromCache(TileState ts) {

        HashMap<Integer, ActionResult> cache = getCacheMap(ts);
        int actHash = Arrays.hashCode(ts.tileValues);
        ActionResult res = cache.get(actHash);
        if (res != null && !Arrays.equals(res.target, ts.tileValues)) {   // collision case
            res = null;
        }
        return res;
    }

    protected static void putResult2Cache(TileState ts, ActionResult ar) {
        HashMap<Integer, ActionResult> cache = getCacheMap(ts);
        int actHash = Arrays.hashCode(ts.tileValues);
        if (!cache.containsKey(actHash)) {
            cache.put(actHash, ar);
        }
    }
}
