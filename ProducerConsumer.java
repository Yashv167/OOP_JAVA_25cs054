import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

class BoundedBuffer {
    private final Queue<Integer> queue = new ArrayDeque<>();
    private final int capacity;

    BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    synchronized void put(int value) throws InterruptedException {
        while (queue.size() == capacity) {            
            System.out.println("  buffer full, producer waits");
            wait();                                   
        }
        queue.add(value);
        System.out.println("put  " + value + " -> " + queue);
        notifyAll();                                  
    }

    synchronized int take() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("  buffer empty, consumer waits");
            wait();
        }
        int value = queue.remove();
        System.out.println("take " + value + " <- " + queue);
        notifyAll();                                  
        return value;
    }
}

public class ProducerConsumer {
    static final int ITEMS = 10;
    static final int CAPACITY = 3;

    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer buffer = new BoundedBuffer(CAPACITY);
        List<Integer> received = new ArrayList<>();   

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    buffer.put(i);                    
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < ITEMS; i++) {
                    received.add(buffer.take());
                    Thread.sleep(50);                 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();                              

        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= ITEMS; i++) expected.add(i);

        System.out.println();
        System.out.println("Produced: " + ITEMS + ", consumed: " + received.size());
        System.out.println("Received: " + received);
        System.out.println("Nothing lost and in order: " + received.equals(expected));
    }
}