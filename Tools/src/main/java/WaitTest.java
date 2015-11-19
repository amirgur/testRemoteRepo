import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitTest {
    static AtomicInteger ai = new AtomicInteger(0);

    public static void main(String[] args) {
        List<WaitingThread> threads = new ArrayList<>();
        while (true) {
            System.out.println("number of threads: " + ai.get());
            String s = System.console().readLine();
            if (s == null)
                System.exit(0);
            s = s.trim();
            if (s.isEmpty())
                continue;
            char command = s.charAt(0);
            if (command != '+' && command != '-') {
                System.out.println("+ or - please");
                continue;
            }
            String num = s.substring(1);
            int iNum;
            try {
                iNum = Integer.parseInt(num.trim());
            } catch (Exception ex) {
                System.out.println("valid number please");
                continue;
            }
            if (command == '+') {
                for (int i = 0; i < iNum; i++) {
                    WaitingThread t = new WaitingThread();
                    t.start();
                    threads.add(t);
                }
            }
            if (command == '-') {
                Set<WaitingThread> threadsToJoin = new HashSet<>();
                for (Iterator<WaitingThread> it = threads.iterator(); it.hasNext(); ) {
                    if (iNum > 0) {
                        WaitingThread t = it.next();
                        threadsToJoin.add(t);

                        synchronized (t.lock) {
                            t.lock.notify();
                        }

                        it.remove();
                        iNum--;
                    } else
                        break;

                }
                for (WaitingThread t : threadsToJoin)
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            System.gc();
        }
    }

    static class WaitingThread extends Thread {
        public final Object lock = new Object();

        @Override
        public void run() {
            ai.incrementAndGet();
            try {
                deepStack(200);
            } catch (InterruptedException ex) {
            } catch (Exception ex) {
                System.out.println("exception in thread " + Thread.currentThread().getName() + ", " + ex.getMessage());
            } finally {
                ai.decrementAndGet();
            }
        }

        private void deepStack(int depth) throws InterruptedException {
            if (depth == 0) {
                synchronized (lock) {
                    lock.wait();
                }

            } else
                deepStack(depth - 1);
        }
    }

}


