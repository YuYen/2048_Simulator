package TileState;

public class ActionResult {
    public int action;
    public int value;
    public int[] target;
    public boolean isEnd = false;

    public ActionResult(TileState ts) {
        this.value = ts.stateScore;
        this.target = ts.tileValues;
    }

    public void show() {
        System.out.println("selected action:" + action + " value:" + value);
    }
}
