import java.io.IOException;

public class MMORPGSync {
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";  // Reset color after output

    public static void main(String[] args) {
        try {
            // Load configuration
            ConfigLoader configLoader = new ConfigLoader();
            DungeonConfig config = loadDungeonConfig(configLoader);
            printConfigSummary(config);

            // Execute dungeon raids
            DungeonQueue dungeonQueue = new DungeonQueue(config.n, config.t1, config.t2);
            dungeonQueue.createParties(config.t, config.h, config.d);
            dungeonQueue.executeDungeonRaids();
            dungeonQueue.printRaidsSummary();
        } catch (IOException e) {
            System.err.println(RED + "Error loading configuration: " + e.getMessage() + RESET);
        }
    }

    private static DungeonConfig loadDungeonConfig(ConfigLoader configLoader) throws IOException {
        int n = configLoader.getN(); // dungeon instances
        int t = configLoader.getT(); // tank players in queue
        int h = configLoader.getH(); // healers in queue
        int d = configLoader.getD(); // dps in queue
        int t1 = configLoader.getT1(); // min time before an instance is finished
        int t2 = configLoader.getT2(); // max time before an instance is finished

        return new DungeonConfig(n, t, h, d, t1, t2);
    }

    private static void printConfigSummary(DungeonConfig config) {
        System.out.println(RED + "--- Dungeon Queue Configuration ---\n" + RESET +
                "Max Concurrent Instances (n): " + GREEN + config.n + RESET + "\n" +
                "Number of Tank Players (t): " + GREEN + config.t + RESET + "\n" +
                "Number of Healer Players (h): " + GREEN + config.h + RESET + "\n" +
                "Number of DPS Players (d): " + GREEN + config.d + RESET + "\n" +
                "Min Dungeon Time (t1): " + GREEN + config.t1 + " seconds" + RESET + "\n" +
                "Max Dungeon Time (t2): " + GREEN + config.t2 + " seconds" + RESET + "\n");
    }

    private static class DungeonConfig {
        int n; // dungeon instances
        int t; // tank players in queue
        int h; // healer players in queue
        int d; // dps in queue
        int t1; // min time before an instance is finished
        int t2; // max time before an instance is finished

        DungeonConfig(int n, int t, int h, int d, int t1, int t2) {
            this.n = n;
            this.t = t;
            this.h = h;
            this.d = d;
            this.t1 = t1;
            this.t2 = t2;
        }
    }
}