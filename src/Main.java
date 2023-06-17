package src;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue blockingQueue1 = new ArrayBlockingQueue(100);
    public static BlockingQueue blockingQueue2 = new ArrayBlockingQueue(100);
    public static BlockingQueue blockingQueue3 = new ArrayBlockingQueue(100);
    public static Thread textGenerator;

    public static void main(String[] args) throws InterruptedException {
        textGenerator = new Thread(() ->
        {
            for (int i = 0; i < 10000; i++) {
                String text = generateText("abc", 100000);
                try {
                    blockingQueue1.put(text);
                    blockingQueue2.put(text);
                    blockingQueue3.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGenerator.start();

        Thread thread1 = getThread(blockingQueue1, 'a');
        Thread thread2 = getThread(blockingQueue2, 'b');
        Thread thread3 = getThread(blockingQueue3, 'c');

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }

    public static String generateText(String letter, int lenght) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < lenght; i++) {
            text.append(letter.charAt(random.nextInt(letter.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = findMaxCharCount(queue, letter);
            System.out.println("Строк с максимальным количеством символа '" + letter + "': " + max);
        });
    }

    public static int findMaxCharCount(BlockingQueue<String> queue, char letter) {
        int max = 0;
        int count = 0;
        String text;
        try {
            while (textGenerator.isAlive()) {
                text = queue.take();
                for (char ch : text.toCharArray()) {
                    if (ch == letter) count++;
                }
                if (count > max) max = count;
                count = 0;
            }

        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " поток прерван!");
            return -1;
        }
        return max;
    }
}
