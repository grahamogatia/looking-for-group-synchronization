import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class DungeonQueue {
    // n instances, t1, t2, parties
    private final int numberOfInstances;
    private final int t1, t2;
    private final Queue<Party> partyQueue = new LinkedList<>();
    private final Semaphore dungeonSlots;
    private int totalPartiesServed = 0;
    private int totalTimeServed = 0;

    private final String RED = "\u001B[31m";
    private final String GREEN = "\u001B[32m";
     private final String RESET = "\u001B[0m";

    public DungeonQueue(int n, int t1, int t2) {
        this.numberOfInstances = n;
        this.t1 = t1;
        this.t2 = t2;
        this.dungeonSlots = new Semaphore(n);
    }

    public void  createParties(int tankCount, int healerCount, int dpsCount) {

        System.out.println(RED + "Creating Parties..." + RESET);

        ArrayList<Party> parties = new ArrayList<>();
        while (tankCount >= 1 && healerCount >= 1 && dpsCount >= 3) {
            parties.add(new Party());

            tankCount--;
            healerCount--;
            dpsCount-=3;
        }
        partyQueue.addAll(parties);

        /* Print party making summary */
        System.out.println("Parties created: " + GREEN + parties.size() + RESET);
        System.out.println("Leftover Tank Players: " + GREEN + tankCount + RESET);
        System.out.println("Leftover Healer Players: " + GREEN + healerCount + RESET);
        System.out.println("Leftover DPS Players: " + GREEN + dpsCount + RESET);
        System.out.println();
    }

    public void executeDungeonRaids() {

        System.out.println(RED + "Executing Dungeon Raids..." + RESET);

        // Print the table header with borders
        System.out.println("+----------------------+------------+------------+------------+");
        System.out.printf("| %-20s | %-10s | %-10s | %-10s \n", "Dungeon ID", "Status", "Party ID", "Time");
        System.out.println("+----------------------+------------+------------+------------+");

        // Create a custom ThreadFactory to name threads
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            private int threadCount = 0;

            @Override
            public Thread newThread(Runnable r) {
                threadCount++;
                return new Thread(r, "DungeonThread-" + threadCount);
            }
        };

        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfInstances, namedThreadFactory)) {
            while (!partyQueue.isEmpty()) {
                Party party;
                synchronized (partyQueue) {
                    if (partyQueue.isEmpty()) {
                        break; // Exit the loop if there are no more parties
                    }
                    party = partyQueue.poll(); // Poll the party inside the synchronized block
                }
                dungeonSlots.acquire();
                executor.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    try {
                        if (party != null) {
                            // Simulate a dungeon run
                            int runTime = (int) (Math.random() * (t2 - t1 + 1) + t1);

                            System.out.printf("| %-20s | %-10s | %-10d | %-10d |\n",
                                    threadName,
                                    GREEN + "Active    " + RESET, // Assuming the dungeon is active when the party enters
                                    party.getId(),
                                    runTime);
                            TimeUnit.SECONDS.sleep(runTime);

                            // Update statistics
                            totalPartiesServed++;
                            totalTimeServed += runTime;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        dungeonSlots.release();
                        System.out.printf("| %-20s | %-10s | %-10s | %-10s |\n",
                                threadName,
                                RED + "Empty     " + RESET, // Assuming the dungeon is active when the party enters
                                "",
                                "");

                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (totalPartiesServed == 0) {
            System.out.println("You must have a party to raid a dungeon ... ");
        }

        System.out.println("+----------------------+------------+------------+------------+");
        System.out.println();
    }

    // TODO: Print Summary
    public void printRaidsSummary() {
        System.out.println(RED + "---- Dungeon Raids Summary ----" + RESET);
        System.out.println("Total parties served: " + GREEN + totalPartiesServed + RESET);
        System.out.println("Total time served: " + GREEN + totalTimeServed + RESET +" seconds.");
    }
}
