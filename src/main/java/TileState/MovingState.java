package TileState;

import java.util.ArrayList;

public class MovingState extends TileState {

    public final static int POSSIBLE_MOVE_DIRECTION = 4;
    public final static int MOVE_UP = 0;
    public final static int MOVE_LEFT = 1;
    public final static int MOVE_RIGHT = 2;
    public final static int MOVE_DOWN = 3;
    public static int[][] move; //0:Up, 1:Left, 2:Right, 3:Down

    static {
        move = new int[DIMENSION][LENGTH];
        int cur = 0;
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                move[MOVE_LEFT][i * DIMENSION + j] = cur;
                move[MOVE_RIGHT][i * DIMENSION + DIMENSION - (j + 1)] = cur;
                move[MOVE_UP][i + j * DIMENSION] = cur;
                move[MOVE_DOWN][DIMENSION - (i + 1) + (DIMENSION - (j + 1)) * DIMENSION] = cur;
                cur++;
            }
        }
    }

    public MovingState(int[] tileValues) {
        super(tileValues);
    }

    public int[] calValidAct() {
        ArrayList<Integer> acts = new ArrayList<>();
        for (int j = 0; j < POSSIBLE_MOVE_DIRECTION; j++) {
            boolean flag = false;
            for (int i = 0; i < DIMENSION; i++) {
                int tar = 0 + i * DIMENSION;
                int cur = 1 + i * DIMENSION;

                while (cur < (i + 1) * DIMENSION) {
                    if (tileValues[move[j][tar]] == 0) {
                        if (tileValues[move[j][cur]] > 0) {
                            flag = true;
                            break;
                        }
                    } else {
                        if (tileValues[move[j][tar]] == tileValues[move[j][cur]]) {
                            flag = true;
                            break;
                        }
                        tar++;
                    }
                    cur++;
                }

                if (flag) {
                    acts.add(j);
                    break;
                }
            }
        }

        return acts.stream().mapToInt(Integer::intValue).toArray();
    }

    public int[] moveTile(int direction) {
        int[] childState = this.tileValues.clone();

        for (int i = 0; i < DIMENSION; i++) {
            int tar = 0 + i * DIMENSION;
            int cur = 1 + i * DIMENSION;
            while (cur < (i + 1) * DIMENSION) {

                if (childState[move[direction][cur]] > 0) {
                    if (childState[move[direction][tar]] == 0) {
                        childState[move[direction][tar]] = childState[move[direction][cur]];
                        childState[move[direction][cur]] = 0;
                    } else {
                        if (childState[move[direction][tar]] == childState[move[direction][cur]]) {
                            childState[move[direction][tar]] = 2 * childState[move[direction][tar]];
                            childState[move[direction][cur]] = 0;
                        } else if (tar + 1 != cur) {
                            childState[move[direction][tar + 1]] = childState[move[direction][cur]];
                            childState[move[direction][cur]] = 0;
                        }
                        tar++;
                    }

                }
                cur++;
            }
        }
        return childState;
    }

    public static void showMoveArrays() {
        System.out.println("Up");
        showArray(move[MOVE_UP]);
        System.out.println("Left");
        showArray(move[MOVE_LEFT]);
        System.out.println("Right");
        showArray(move[MOVE_RIGHT]);
        System.out.println("Down");
        showArray(move[MOVE_DOWN]);
    }

    @Override
    public TileState makeAction(int act) {
        if (!isValidAction(act)) {
            System.out.println("not valid action");
            return null;
        }
        int[] childValues = moveTile(act);
        return new GeneratingState(childValues);
    }

}
