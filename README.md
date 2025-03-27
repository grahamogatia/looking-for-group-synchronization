# Looking for Group Synchronization

## Description
This program simulates the **Looking for Group (LFG) dungeon queuing system** in an MMORPG, managing the synchronization of players entering dungeon instances.

It ensures that parties are formed correctly and that a maximum of `n` dungeon instances run concurrently. Each party consists of:

- **1 Tank**
- **1 Healer**
- **3 DPS Players**

Dungeon completion times are randomized between a minimum (`t1`) and maximum (`t2`) duration.

The program uses **process synchronization techniques** to prevent deadlocks and starvation while efficiently managing the queue.

At the end of execution, it provides a summary of how many parties were served and the total time spent in dungeons.

## Configuration
Modify `config.properties` to set up the parameters:

```ini
n=3    # Maximum number of concurrent instances
t=5    # Number of tank players in the queue
h=5    # Number of healer players in the queue
d=15   # Number of DPS players in the queue
t1=5   # Minimum time before an instance is finished (seconds)
t2=10  # Maximum time before an instance is finished (seconds)
```

## Build and Run Instructions

### Method 1: Using IntelliJ IDEA
1. Clone the repository:
    ```sh
    git clone https://github.com/YOUR_GITHUB_USERNAME/looking-for-group-synchronization.git
    cd looking-for-group-synchronization
    ```
2. Open IntelliJ IDEA.
3. Click **File → Open** and select the project folder.
4. Navigate to `src/MMORPGSync.java`.
5. Right-click and select **Run 'MMORPGSync.main()'**.

### Method 2: Using Terminal
1. Navigate to the project directory:
    ```sh
    cd looking-for-group-synchronization
    ```
2. Compile the Java program:
    ```sh
    javac -d out -cp src src/MMORPGSync.java
    ```
3. Run the program:
    ```sh
    java -cp out MMORPGSync
    ```

## Expected Output
The program will display:
- The status of each instance (active or empty).
- A summary of the total parties served and total time taken.
