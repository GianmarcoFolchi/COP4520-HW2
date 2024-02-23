/* 
* I opted for strategy three after considering the limitations of the first two options. Strategies one and two lacked the assurance that
* every interested guest would have the opportunity to view the vase. In contrast, strategy three guarantees this by introducing a queue 
* system. While the first two strategies might offer quicker access, they fail to ensure that all interested parties can view the vase. 
* I prefer a slightly slower, yet assured, system where every guest who wishes to see the vase can do so, rather than a system that offers 
* no such assurance.
* This approach is demonstrably effective as it systematically records every guest that has viewed the vase, adhering to the requirement.
* To implement this multithreaded queue, I utilized Java's BlockingQueue and the Semaphore class to ensure that only one person can view
* the vase at any given time. This method may not be the most efficient in terms of speed; however, it prioritizes safety and reliability. 
* Remarkably, this strategy proved to be highly efficient in practice, running in less than a second on my computer with a total of ten 
* guests, making it not only the safest but also an impressively swift solution.
* */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        ShowroomQueue showroomQueue = new ShowroomQueue(10);
        Showroom showroomSemaphore = new Showroom();
        for (int i = 0; i < 10; i++) {
            final int guestThreadNumber = i;
            Thread guestThread = new Thread(() -> {
                showroomSemaphore.viewVase();
                System.out.println("guest " + (guestThreadNumber + 1) + " viewed the vase");
            });
            showroomQueue.addToQueue(guestThread);
        }
    }
}

class ShowroomQueue {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final int totalGuests;
    private int guestsServed = 0;

    public ShowroomQueue(int totalGuests) {
        this.totalGuests = totalGuests;
        Thread showroomThread = new Thread(() -> {
            try {
                while (true) {
                    queue.take().run();
                    synchronized (this) {
                        if (++guestsServed == totalGuests) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        showroomThread.start();
    }

    public void addToQueue(Runnable guest) {
        queue.add(guest);
    }
}

class Showroom {
    private final Semaphore semaphore = new Semaphore(1, false);

    public void viewVase() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release(); 
        }
    }
}
