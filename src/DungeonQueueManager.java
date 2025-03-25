import java.io.IOException;

public class DungeonQueueManager {
    public static void main(String[] args) {
        try {
            ConfigLoader configLoader = new ConfigLoader();
            int n = configLoader.getN();
            int t = configLoader.getT();
            int h = configLoader.getH();
            int d = configLoader.getD();
            int t1 = configLoader.getT1();
            int t2 = configLoader.getT2();


            /* For Testing */
            final String RED = "\u001B[31m";
            final String GREEN = "\u001B[32m";
            final String RESET = "\u001B[0m";  // Reset color after output

            System.out.println(RED + "=== Dungeon Queue Configuration ===\n" + RESET +
                    "Max Concurrent Instances (n): " + GREEN + n + RESET + "\n" +
                    "Number of Tank Players (t): " + GREEN + t + RESET + "\n" +
                    "Number of Healer Players (h): " + GREEN + h + RESET + "\n" +
                    "Number of DPS Players (d): " + GREEN + d + RESET + "\n" +
                    "Min Dungeon Time (t1): " + GREEN + t1 + " seconds" + RESET + "\n" +
                    "Max Dungeon Time (t2): " + GREEN + t2 + " seconds" + RESET + "\n" +
                    RED + "=================================" + RESET);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
