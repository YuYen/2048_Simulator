package TileState;

import java.util.ArrayList;

public class GeneratingState extends TileState {

    public GeneratingState(){ super(new int[LENGTH]);}
    public GeneratingState(int[] tileValues) {
        super(tileValues);
    }

    @Override
    public TileState makeAction(int act) {
        int[] childValues = tileValues.clone();
        childValues[act] = 2;
        return new MovingState(childValues);
    }

    @Override
    public int[] calValidAct() {
        ArrayList<Integer> acts = new ArrayList<>();
        for (int i = 0; i < LENGTH; i++) {
            if (this.tileValues[i] == 0) {
                acts.add(i);
            }
        }
        return acts.stream().mapToInt(Integer::intValue).toArray();
    }
}
