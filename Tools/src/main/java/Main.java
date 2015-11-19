import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * looking at httpd's ssl_request_log, what were the active requests at a certain point at time?
 * run like that:
 * java Main 2015-11-16 23:58:40 < ssl_request_log.1447632000
 */
public class Main {

    public static void main (String[] args) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date requestedDate = sdf.parse(args[0] + " " + args[1]);
        String line = br.readLine();

        AtomicLong idGenerator = new AtomicLong();
        List<Event> events = new ArrayList<>();

        Comparator<Event> comparator = new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                int dateCompare = o1.date.compareTo(o2.date);
                if(dateCompare != 0)
                    return dateCompare;
                if(o1.start && !o2.start)
                    return -1;
                if(!o1.start && o2.start)
                    return 1;
                return 0;
            }
        };

        while (line != null) {
            try {
                long id = idGenerator.incrementAndGet();
                String[] fields = line.split(" ");
                Date start = sdf.parse(fields[0] + " " + fields[1]);
                events.add(new Event(start, true, line, id));
                int dur = (int) (Long.valueOf(fields [9])/1000);
                dur = dur / 1000;
                dur = dur * 1000;
                Date end = new Date(start.getTime() + dur);
                events.add(new Event(end, false, line, id));
            }
            finally {
                line = br.readLine();
            }
        }
        Collections.sort(events, comparator);

        Set<Event> concurrentLines = new HashSet<>();
        for(Event event : events) {
            if(event.date.after(requestedDate) || ((!event.start) && event.date.equals(requestedDate) )){
                List<Event> list = new ArrayList<>(concurrentLines);
                Collections.sort(list, comparator);
                for(Event s : list) {
                    System.out.println(s.line);
                }
                break;
            }
            if (event.start)
                concurrentLines.add(event);
            else
                concurrentLines.remove(event);
        }
    }
}


class Event {
    public long id;
    public Date date;
    public boolean start;
    public String line;
    public Event(Date date, boolean start, String line, long id) {
        this.id = id;
        this.date = date;
        this.start = start;
        this.line = line;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  Event))
            return false;
        return id == ((Event)obj).id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
