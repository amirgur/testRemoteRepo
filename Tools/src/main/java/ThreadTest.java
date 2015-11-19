import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest {
    static AtomicInteger ai = new AtomicInteger(0);
    static  int stackDepth;
    static boolean useSleep;
    public static void main (String[] args) {
        stackDepth = Integer.parseInt(args[0]);
        useSleep = Boolean.parseBoolean(args[1]);
        List<SleepingThread> threads = new LinkedList<>();
        while(true) {
            System.out.println("number of threads: " + ai.get());
            String s = System.console().readLine();
            if (s == null)
                System.exit(0);
            s = s.trim();
            if(s.isEmpty())
                continue;
            char command = s.charAt(0);
            if(command != '+' && command != '-') {
                System.out.println("+ or - please");
                continue;
            }
            String num = s.substring(1);
            int iNum;
            try {
                iNum = Integer.parseInt(num.trim());
            }
            catch (Exception ex){
                System.out.println("valid number please");
                continue;
            }
            if(command == '+') {
                for (int i =0;i<iNum;i++) {
                    SleepingThread t = new SleepingThread();
                    t.start();
                    threads.add(t);
                }
            }
            if(command == '-') {
                Set<SleepingThread> threadsToJoin = new HashSet<>();
                for(Iterator<SleepingThread> it = threads.iterator();it.hasNext();) {
                    if(iNum > 0 ) {
                        SleepingThread t = it.next();
                        threadsToJoin.add(t);
                        if(useSleep) {
                            t.interrupt();
                        }
                        else {
                            synchronized (t.lock) {
                                t.lock.notify();
                            }
                        }
                        it.remove();
                        iNum--;
                    }
                    else
                        break;

                }
                for(SleepingThread t : threadsToJoin)
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            System.gc();
        }
    }

    static class SleepingThread extends Thread {
        public final Object lock = new Object();
        @Override
        public void run() {
            ai.incrementAndGet();
            try {
                deepStack(stackDepth);
            } catch (InterruptedException ex) {
            }catch (Exception ex) {
                System.out.println("exception in thread " + Thread.currentThread().getName() + ", " + ex.getMessage());
            }
            finally {
                ai.decrementAndGet();
            }
        }

        private void deepStack (int depth) throws InterruptedException {
            if (depth == 0) {
                if(useSleep) {
                    TimeUnit.DAYS.sleep(365);
                }
                else {
                    synchronized (lock) {
                        lock.wait();
                    }
                }
            }
            else
                deepStack(depth-1);
        }
    }

}


