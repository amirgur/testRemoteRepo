import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryTest {
    public static void main (String[] args) {
        List<long[]> blocks = new LinkedList<>();
        while(true) {
            System.out.println("number of blocks: " + blocks.size());
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
                    Random random = new Random();
                    long[] longArray = new long[(int) Math.pow(2,17)];
                    for(int j = 0 ; j < longArray.length;j++)
                        longArray[j] = random.nextLong();
                    blocks.add(longArray);
                }
            }
            if(command == '-') {
                for(Iterator<long[]> it = blocks.iterator();it.hasNext();) {
                    if(iNum > 0 ) {
                        it.next();
                        it.remove();
                        iNum--;
                    }
                    else
                        break;

                }
            }
            System.gc();
        }
    }

}
