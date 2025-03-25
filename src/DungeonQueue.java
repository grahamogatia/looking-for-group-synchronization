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

    public void addParty(Party party) {
        partyQueue.add(party);
    }

    // Testing
    public void printQueue() {
        System.out.println("Party Queue: " + partyQueue.size());
    }

    // TODO: Matchmaking
    public void allocateDungeonsToParties() {

        // Print the table header with borders
        System.out.println("+----------------------+------------+------------+------------+------------+");
        System.out.printf("| %-20s | %-10s | %-10s | %-10s | %-10s |\n", "Dungeon ID", "Status", "Party ID", "Time", "Permits");
        System.out.println("+----------------------+------------+------------+------------+------------+");

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
                dungeonSlots.acquire();
                executor.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    Party party = partyQueue.poll();
                    try {
                        if (party != null) {
                            // Simulate a dungeon run
                            int runTime = (int) (Math.random() * (t2 - t1 + 1) + t1);

                            System.out.printf("| %-20s | %-10s | %-10d | %-10d | %-10d |\n",
                                    threadName,
                                    GREEN + "Active    " + RESET, // Assuming the dungeon is active when the party enters
                                    party.getId(),
                                    runTime,
                                    dungeonSlots.availablePermits());
                            TimeUnit.SECONDS.sleep(runTime);

                            // Update statistics
                            totalPartiesServed++;
                            totalTimeServed += runTime;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        dungeonSlots.release();
                        System.out.printf("| %-20s | %-10s | %-10s | %-10s | %-10d |\n",
                                threadName,
                                RED + "Empty     " + RESET, // Assuming the dungeon is active when the party enters
                                "",
                                "",
                                dungeonSlots.availablePermits());

                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("+----------------------+------------+------------+------------+------------+");
    }

    // TODO: Print Summary
    public void printSummary() {
        System.out.println("Total parties served: " + totalPartiesServed);
        System.out.println("Total time served: " + totalTimeServed + " seconds.");
    }
}
