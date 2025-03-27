import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.*;

public class DungeonQueue {
    private final int numberOfInstances;
    private final int t1, t2;
    private final Queue<Party> partyQueue = new ConcurrentLinkedQueue<>();
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

        // Party making results
        System.out.println("Parties created: " + GREEN + parties.size() + RESET);
        System.out.println("Leftover Tank Players: " + GREEN + tankCount + RESET);
        System.out.println("Leftover Healer Players: " + GREEN + healerCount + RESET);
        System.out.println("Leftover DPS Players: " + GREEN + dpsCount + RESET);
        System.out.println();
    }

    public void executeDungeonRaids() {
        // Header
        System.out.println(RED + "Executing Dungeon Raids..." + RESET);
        System.out.println("+----------------------+------------+------------+------------+");
        System.out.printf("| %-20s | %-10s | %-10s | %-10s |\n", "Dungeon ID", "Status", "Party ID", "Time");
        System.out.println("+----------------------+------------+------------+------------+");

        // Name dungeon threads
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            private int threadCount = 0;

            @Override
            public Thread newThread(Runnable r) {
                threadCount++;
                return new Thread(r, "DungeonThread-" + threadCount);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(numberOfInstances, namedThreadFactory);
        try {
            for (int i = 0; i < numberOfInstances; i++) {
                executor.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    Party party = null; // Initialize party variable

                    try {
                        // Poll for a party before acquiring the semaphore
                        party = partyQueue.poll();
                        if (party != null) {
                            // Simulate a dungeon run
                            dungeonSlots.acquire(); // Acquire the semaphore only if we have a party
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
                        Thread.currentThread().interrupt(); // Preserve interrupt status
                    } finally {
                        // Only release if we successfully acquired the semaphore
                        if (party != null) {
                            dungeonSlots.release();
                        }
                        System.out.printf("| %-20s | %-10s | %-10s | %-10s |\n",
                                threadName,
                                RED + "Empty     " + RESET,
                                "-",
                                "-");
                    }
                });
            }
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (totalPartiesServed == 0) {
            System.out.println("You must have a party to raid a dungeon ... ");
        }

        System.out.println("+----------------------+------------+------------+------------+");
        System.out.println();
    }

    public void printRaidsSummary() {
        System.out.println(RED + "---- Dungeon Raids Summary ----" + RESET);
        System.out.println("Total parties served: " + GREEN + totalPartiesServed + RESET);
        System.out.println("Total time served: " + GREEN + totalTimeServed + RESET +" seconds.");
    }
}
