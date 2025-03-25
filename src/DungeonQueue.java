import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class DungeonQueue {
    // n instances, t1, t2, parties
    private final Semaphore dungeonSlots;
    private final int t1, t2;
    private final Queue<Party> partyQueue = new LinkedList<>();

    public DungeonQueue(int n, int t1, int t2) {
        this.dungeonSlots = new Semaphore(n);
        this.t1 = t1;
        this.t2 = t2;
    }

    public void addParty(Party party) {
        partyQueue.add(party);
    }

    // Testing
    public void printQueue() {
        System.out.println("Party Queue: " + partyQueue.size());
    }

}
