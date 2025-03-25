public class Party {
    private static int counter = 1;
    public final int id;

    public Party() {
        this.id = counter++;
    }

    public int getId() {
        return id;
    }
}
