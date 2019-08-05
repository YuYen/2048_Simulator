import TileState.*;

public class Driver {

    public static int round = 0;

    public static void printRound() {
        System.out.println("======================================================================\n");
        System.out.println("The " + round + "th round:");
    }

    public static void printHelper() {
        System.out.println("[Usage]: [depth move_algo [gen_algo]] ");
        System.out.println("depth: search depth, should be integer");
        System.out.println("move_algo (moving algorithm): \"a\"->Alpha Beta Pruning, \"m\"->Monte Carlo Prediction \"r\"->Random");
        System.out.println("gen_algo (generating algorithm): \"a\"->Alpha Beta Pruning, \"r\"->Random");
        System.exit(0);
    }

    public static int getAlgorithm(String s){
        int algo = -1;
        switch (s.toLowerCase()){
            case "a":
                algo = Calculator.ALPHA_BETA_PRUNING;
                break;
            case "m":
                algo = Calculator.MONTE_CARLO_PREDICTION;
                break;
            case "r":
                algo = Calculator.RANDOM;
                break;
            default:
                printHelper();
        }
        return algo;
    }

    public static void main(String[] args) {

        int depth=1;
        if (args.length == 0) {
            depth = 9;
        } else if (args.length > 1) {
            try{
                depth = Integer.parseInt(args[0]);
            }catch (Exception e){
                printHelper();
            }
            Calculator.moveAlgorithm = getAlgorithm(args[1]);
            if (args.length > 2) {
                Calculator.genAlgorithm = getAlgorithm(args[2]);
                if (Calculator.genAlgorithm == Calculator.MONTE_CARLO_PREDICTION)
                    printHelper();
            }
        } else {
            printHelper();
        }

        Calculator.show();
        System.out.println("search depth:" + depth);

        TileState rgTs = new GeneratingState();
        TileState bmTs = rgTs.makeAction(Calculator.searchOpponent(rgTs, depth).action);

        long start = System.currentTimeMillis();
        while (!TileState.terminalTest(bmTs)) {
            round++;
            printRound();
            bmTs.showState();
            ActionResult ar = Calculator.search(bmTs, depth);

            rgTs = bmTs.makeAction(ar.action);
            ar.show();
            rgTs.showState();

            ActionResult ar2 = Calculator.searchOpponent(rgTs, depth);
            ar2.show();
            bmTs = rgTs.makeAction(ar2.action);
        }
        long timeCost = System.currentTimeMillis() - start;

        System.out.println("End of Simulation");
        System.out.println("max arrive value: " + bmTs.maxValue);
        System.out.println("total round: " + round);
        System.out.println("decision time pre step: " + timeCost / round);
        System.out.println("Final State: ");
        bmTs.showState();
    }
}
