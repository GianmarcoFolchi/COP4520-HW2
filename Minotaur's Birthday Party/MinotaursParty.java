/* 
 * 
 * The core of the solution involves maintaining a boolean array to track which guests have already visited the labyrinth and a 
 * special role for a 'leader' guest to monitor the overall progress. The program randomly selects guests to enter the labyrinth, ensuring 
 * each guest's visit is represented by a separate thread, symbolizing their independent journey through the maze.
 * 
 * Upon entering the labyrinth, a guest encounters one of two scenarios: if the cupcake is present, they may choose to eat it if they 
 * haven't already done so, marking their visit. If the cupcake is absent, and the guest is designated as the leader (a predetermined role), 
 * they request a new cupcake, indicating another round of visits. This leader also keeps count of the number of cupcakes consumed, 
 * which indirectly reflects the number of guests who have had their turn, considering some guests might visit the labyrinth multiple times.
 * 
 * The synchronization block ensures that only one guest interacts with the cupcake at any given moment, 
 * preserving the integrity of the visiting order and the cupcake's availability status. This mechanism, alongside the leader's role in 
 * requesting new cupcakes and tracking visits, forms a robust strategy to guarantee that all guests will have visited the labyrinth at 
 * least once.
 * The program concludes once the leader confirms that the count of new cupcakes requested matches or exceeds the number of guests, ensuring 
 * that all guests have had the opportunity to visit the labyrinth. This solution not only meets the Minotaur's criteria for the party game 
 * but also demonstrates a practical application of concurrency control, resource management, and strategic planning in a simulated environment.
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinotaursParty {
    private static final int NUM_GUESTS = 10;
    private static boolean[] visited = new boolean[NUM_GUESTS];
    private static int leaderGuestCount = 0;
    private static boolean isCupcakePresent = true;
    private static final Object lock = new Object();
    private static boolean leaderConfirmation;

    public static void main(String[] args) {
        List<Thread> guests = new ArrayList<Thread>();
        Random rand = new Random();

        for (int i = 0; i < NUM_GUESTS; i++) {
            visited[i] = false;
        }

        while (!leaderConfirmation) {
            final int guestId = rand.nextInt(NUM_GUESTS);
            guests.add(new Thread(() -> visitLabyrinth(guestId)));
            guests.get(guests.size() - 1).start();
        }

        for (Thread guest : guests) {
            try {
                guest.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        System.out.println("The leader has confirmed that all guests have visited the labyrinth at least once.");
    }

    private static void visitLabyrinth(int guestId) {
        synchronized (lock) {
            if (isCupcakePresent) {
                if (visited[guestId] == false) {
                    isCupcakePresent = false;
                    visited[guestId] = true;
                    System.out.println("Guest " + guestId + " ate the cupcake.");
                } else {
                    System.out.println("Guest " + guestId + " found a cupcake, but left as he had already eaten one.");
                }
            } else {
                if (guestId == 1) {
                    System.out.println("Leader requested a new cupcake and updated leaderGuestCount.");
                    isCupcakePresent = true;
                    leaderGuestCount++;
                    if (leaderGuestCount >= NUM_GUESTS)
                        leaderConfirmation = true;
                } else {
                    System.out.println("Guest " + guestId + " did not find a cupcake and left.");
                }
            }
        }
    }
}
