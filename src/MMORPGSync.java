import java.io.IOException;
import java.util.ArrayList;

public class MMORPGSync {
    public static void main(String[] args) throws IOException {
        // Input Validation
        ConfigLoader configLoader = new ConfigLoader();
        int n = configLoader.getN(); // dungeon instances
        int t = configLoader.getT(); // tank players in queue
        int h = configLoader.getH(); // healers in queue
        int d = configLoader.getD(); // dps in queue
        int t1 = configLoader.getT1(); // min time before an instance is finished
        int t2 = configLoader.getT2(); // max time before an instance is finished


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

        // Logic to implement the Dungeon Queue Manager
        /* Rules:
            - n instances concurrently active
            - standard party: 1 tank, 1 healer, 3 dps
            - not deadlock, not starvation
            - t1 <= t <= t2 is selected as the completion time
                - t2 <= 15
        * */

        // Create DungeonQueue
        DungeonQueue dungeonQueue = new DungeonQueue(n, t1, t2);

        // Create parties: 1 tank, 1 healer, 3 dps
        // Discard players if not enough to form a party
        ArrayList<Party> parties = createParties(t, h, d);
        System.out.println(parties.size());
        for (Party p: parties) dungeonQueue.addParty(p);

        // Start MatchMaking
        dungeonQueue.allocateDungeonsToParties();

        // Print Summary
        dungeonQueue.printSummary();

    }

    public static ArrayList<Party> createParties(int tankCount, int healerCount, int dpsCount) {
        ArrayList<Party> parties = new ArrayList<>();

        while (tankCount >= 1 && healerCount >= 1 && dpsCount >= 3) {
            parties.add(new Party());

            tankCount--;
            healerCount--;
            dpsCount--;
        }

        return parties;
    }
}
