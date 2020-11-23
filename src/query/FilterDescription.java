package query;

import fileio.ActionInputData;
import fileio.ActorInputData;
import org.json.simple.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterDescription extends QueryManager {

    public final class ElPQ {
        private String name;

        @Override
        public String toString() {
            return "ElPQ{"
                    + "name='" + name
                    + '}';
        }
    }

    private static boolean isContain(final String source, final String subItem) {
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    static class DESComparator implements Comparator<ElPQ> {
        /**
         * @param p1
         * @param p2
         * @return
         */
        @Override
        public int compare(final ElPQ p1, final ElPQ p2) {
            return p2.name.compareTo(p1.name);
        }
    }

    static class ASCComparator implements Comparator<ElPQ> {
        /**
         * @param p1
         * @param p2
         * @return
         */
        @Override
        public int compare(final ElPQ p1, final ElPQ p2) {
            return p1.name.compareTo(p2.name);
        }
    }

    /**
     * @param action
     * @return
     */
    public final PriorityQueue<ElPQ> ascOrDec(final ActionInputData action) {
        if (action.getSortType().equals("asc")) {
            return new PriorityQueue<>(new ASCComparator());
        } else {
            return new PriorityQueue<>(new DESComparator());
        }
    }

    /**
     *
     * @param actors
     * @param action
     * @return
     */
    public JSONObject filterDescription(final List<ActorInputData> actors,
                                        final ActionInputData action) {
        JSONObject jo = new JSONObject();
        PriorityQueue<ElPQ> pq = ascOrDec(action);
        List<String> words = action.getFilters().get(2);
        for (int i = 0; i < actors.size(); ++i) {
            int ok = 1;
            String description = actors.get(i).getCareerDescription();
            for (String word : words) {
                if (!isContain(description.toLowerCase(), word.toLowerCase())) {
                    ok = 0;
                    break;
                }
            }
            if (ok == 1) {
                ElPQ el = new ElPQ();
                el.name = actors.get(i).getName();
                pq.add(el);
            }
        }

        String message = "Query result: [";
        while (!pq.isEmpty()) {
            message += pq.poll().name;
            if (!pq.isEmpty()) {
                message += ", ";
            }
        }

        message += "]";
        jo.put("id", action.getActionId());
        jo.put("message", message);

        return jo;
    }
}
