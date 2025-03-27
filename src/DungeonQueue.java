import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonQueue {
    private final int numberOfInstances;
    private final int t1, t2;
    private final Queue<Party> partyQueue = new ConcurrentLinkedQueue<>();
    private final Semaphore dungeonSlots;
    private final AtomicInteger totalPartiesServed = new AtomicInteger(0);
    private final AtomicInteger totalTimeServed = new AtomicInteger(0);

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


        // Allocate dungeon slots to parties
        ExecutorService executor = Executors.newFixedThreadPool(numberOfInstances, namedThreadFactory);
        CountDownLatch latch = new CountDownLatch(partyQueue.size()); // Assuming you have 500 parties
        printAllInitialThreadStatus();
        try {
            while (!partyQueue.isEmpty()) {
                Party party = partyQueue.poll();
                if (party != null) {
                    executor.submit(() -> {
                        String threadName = Thread.currentThread().getName();
                        try {
                            // Simulate a dungeon run
                            dungeonSlots.acquire();
                            int runTime = (int) (Math.random() * (t2 - t1 + 1) + t1);

                            System.out.printf("| %-20s | %-10s | %-10d | %-10d |\n",
                                    threadName,
                                    GREEN + "Active    " + RESET,
                                    party.getId(),
                                    runTime);
                            TimeUnit.SECONDS.sleep(runTime);

                            // Update statistics
                            serveParty();
                            addTimeServed(runTime);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            dungeonSlots.release();
                            latch.countDown(); // Decrement the latch count
                            System.out.printf("| %-20s | %-10s | %-10s | %-10s |\n",
                                    threadName,
                                    RED + "Empty     " + RESET,
                                    "-",
                                    "-");
                        }
                    });
                }
            }
        } finally {
            executor.shutdown();
            try {
                latch.await(); // Wait for all tasks to complete
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (totalPartiesServed.get() == 0) {
            System.out.println("You must have a party to raid a dungeon ... ");
        }

        System.out.println("+----------------------+------------+------------+------------+");
        System.out.println();
    }

    public synchronized void serveParty() {
        totalPartiesServed.incrementAndGet();
    }

    public synchronized void addTimeServed(int time) {
        totalTimeServed.addAndGet(time);
    }

    public void printAllInitialThreadStatus() {
        for (int i = 0; i < numberOfInstances; i++) {
            System.out.printf("| %-20s | %-10s | %-10s | %-10s |\n",
                    "DungeonThread-" + (i + 1),
                    RED + "Empty     " + RESET,
                    "-",
                    "-");
        }
    }

    public void printRaidsSummary() {
        System.out.println(RED + "---- Dungeon Raids Summary ----" + RESET);
        System.out.println("Total parties served: " + GREEN + totalPartiesServed.get() + RESET);
        System.out.println("Total time served: " + GREEN + totalTimeServed.get() + RESET +" seconds.");
    }
}
