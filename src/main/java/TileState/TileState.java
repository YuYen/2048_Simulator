package TileState;

import java.util.*;


public abstract class TileState {
    public final static int DIMENSION = 4;
    public final static int LENGTH = DIMENSION * DIMENSION;
    public final static int CONNECTED_LENGTH = 11;
    public static int[] valueDomain;
    public static HashMap<Integer, Integer> scoreMap;
    public static Set<Integer> cornerIndexes = new HashSet<>();

    static {
        cornerIndexes.add(0);
        cornerIndexes.add(DIMENSION - 1);
        cornerIndexes.add(LENGTH - DIMENSION);
        cornerIndexes.add(LENGTH - 1);

        valueDomain = new int[LENGTH];
        valueDomain[0] = 0;
        int cur = 2;
        for (int i = 1; i < LENGTH; i++) {
            valueDomain[i] = cur;
            cur *= 2;
        }

        scoreMap = new HashMap();
        scoreMap.put(0, 0);
        scoreMap.put(1, 0);
        for (int i = 1; i < LENGTH; i++) {
            scoreMap.put(valueDomain[i], valueDomain[i] + 2 * scoreMap.get(valueDomain[i - 1]));
        }
    }

    public int[] tileValues;  // value array of current tiles
    public int stateScore;
    public int valueCount;
    public int maxValue;
    public boolean isTerminal;
    public HashMap<Integer, ArrayList<Integer>> valPosMap = new HashMap<>();
    public ArrayList<Integer> valueKeys = new ArrayList<>();
    public ArrayList<Integer> maxPosition = new ArrayList<>();

    public int[] validActions;

    public TileState(int[] tileValues) {
        this.tileValues = tileValues;
        if (this.tileValues.length != LENGTH) {
            System.err.println("length error");
        }
        this.validActions = calValidAct();

        // sort array values
        for (int i = 0; i < LENGTH; i++) {
            int key = tileValues[i];
            if (key > 0) {
                valueKeys.add(key);
                if (!valPosMap.containsKey(key)) {
                    valPosMap.put(key, new ArrayList<>());
                }
                valPosMap.get(key).add(i);
                valueCount++;
            }
        }
        if (valueCount > 0) {
            Collections.sort(valueKeys, Collections.reverseOrder());
            maxValue = valueKeys.get(0);
            maxPosition = valPosMap.get(maxValue);
            this.calScore();
        }
    }

    public abstract int[] calValidAct();

    public abstract TileState makeAction(int act);

    public static boolean terminalTest(TileState ts) {
        return ts.isTerminal;
    }

    public void calScore() {

        if (this.validActions.length == 0) {
            this.isTerminal = true;
            this.stateScore = 0;
            return;
        }

        for (int key : valueKeys) {
            this.stateScore += scoreMap.get(key);
        }

        if (this.valueCount == 1) {
            this.stateScore *= 16;
            return;
        }

        // check max in corner
        int preIndex = findCornerValueIndex(valPosMap.get(maxValue));
        if (preIndex < 0) {
            this.stateScore = this.stateScore / 2;
            return;
        }

        int dirHori, dirVert;
        dirHori = (preIndex % DIMENSION - 1 > 0) ? -1 : 1;
        dirVert = (preIndex / DIMENSION - 1 > 0) ? -DIMENSION : DIMENSION;

        // find first direction
        int curUsedCount = 1;
        int nextKey = valueKeys.get(curUsedCount);

        int limit = Integer.min(valueKeys.size() - 1, CONNECTED_LENGTH);

        int longest;
        if (tileValues[preIndex + dirHori] == nextKey) {
            longest = findLongestConnected(preIndex, curUsedCount, limit, dirHori, dirVert);

        } else if (tileValues[preIndex + dirVert] == nextKey) {
            longest = findLongestConnected(preIndex, curUsedCount, limit, dirVert, dirHori);
        } else {
            return;
        }
        this.stateScore *= longest * 2;
    }

    public int findLongestConnected(int strIndex, int used, int limit, int dir1, int dir2) {
        int result = 0;
        int nextIndex = strIndex + dir1;

        if (used > 1) {
            int tmpDir = dir1;
            if (used % DIMENSION == DIMENSION - 1) {
                dir1 = dir2;
                dir2 = -tmpDir;
            } else if (used % DIMENSION == 0) {
                dir1 = dir2;
                dir2 = tmpDir;
            }
        }

        if (limit > 0 && valueKeys.get(used) == tileValues[nextIndex]) {
            result += findLongestConnected(nextIndex, used + 1, limit - 1, dir1, dir2);
            result++;
        }
        return result;
    }

    public int findCornerValueIndex(List<Integer> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (cornerIndexes.contains(list.get(i))) {
                index = list.get(i);
                return index;
            }
        }
        return index;
    }

    public boolean isValidAction(int act) {
        return Arrays.binarySearch(validActions, act) >= 0;
    }

    public void showValidActions() {
        System.out.print("Valid actions: ");
        for (int i = 0; i < validActions.length; i++) {
            System.out.print(validActions[i] + "\t");
        }
        System.out.println();
    }

    public static void showArray(int[] array) {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                System.out.print(array[i * DIMENSION + j] + "\t\t");
            }
            System.out.println();
        }
    }

    public void showState() {
        showArray(this.tileValues);
        System.out.print("================" + this.stateScore + "," + this.maxValue + " (");
        for (int i = 0; i < validActions.length; i++) {
            System.out.print(validActions[i] + ",");
        }
        System.out.print(")\n");
    }

    public static void showScoreMap() {
        for (int i = 0; i < LENGTH; i++) {
            System.out.println(valueDomain[i] + ":" + scoreMap.get(valueDomain[i]));
        }
    }

    public void showStateInfo() {
        System.out.print("value Keys:");
        for (int i : valueKeys) {
            System.out.print(i + "\t");
        }
        System.out.println("\n score : " + stateScore);
    }
}
