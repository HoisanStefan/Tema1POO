package query;

import actor.ActorsAwards;
import fileio.ActionInputData;
import fileio.ActorInputData;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;

public class Awards extends QueryManager {

    public final class ElPQ {
        private String name;
        private Integer numberOfAwards;

        @Override
        public String toString() {
            return "ElPQ{"
                    + "name='" + name + '\''
                    + ", numberOfAwards=" + numberOfAwards
                    + '}';
        }
    }

    static class DESComparator implements Comparator<ElPQ> {
        /**
         * @param p1
         * @param p2
         * @return
         */
        @Override
        public int compare(final ElPQ p1, final ElPQ p2) {
            if (p1.numberOfAwards < p2.numberOfAwards) {
                return 1;
            } else if (p1.numberOfAwards > p2.numberOfAwards) {
                return -1;
            } else if (p1.numberOfAwards == p2.numberOfAwards) {
                return p2.name.compareTo(p1.name);
            }
            return 0;
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
            if (p1.numberOfAwards > p2.numberOfAwards) {
                return 1;
            } else if (p1.numberOfAwards < p2.numberOfAwards) {
                return -1;
            } else if (p1.numberOfAwards == p2.numberOfAwards) {
                return p1.name.compareTo(p2.name);
            }
            return 0;
        }
    }

    /**
     * @param action
     * @return
     */
    public PriorityQueue<ElPQ> ascOrDec(final ActionInputData action) {
        if (action.getSortType().equals("asc")) {
            return new PriorityQueue<>(new ASCComparator());
        } else {
            return new PriorityQueue<>(new DESComparator());
        }
    }

    /**
     * @param actors
     * @param action
     * @return
     */
    public JSONObject awards(final List<ActorInputData> actors, final ActionInputData action) {
        JSONObject jo = new JSONObject();
        PriorityQueue<ElPQ> pq = ascOrDec(action);
        /**
         * The awards are specified at index 3 in filters
         * Doing this for checkstyle
         */
        int idx = 2;
        List<String> awards = action.getFilters().get(++idx);

        for (int i = 0; i < actors.size(); ++i) {
            int ok = 1;
            int sum = 0;
            for (String award : awards) {
                Map<ActorsAwards, Integer> extraction = actors.get(i).getAwards();
                List<String> actorAwardsAsStrings = new ArrayList<>();
                for (Map.Entry<ActorsAwards, Integer> entry : extraction.entrySet()) {
                    String newEntry = entry.getKey().toString();
                    actorAwardsAsStrings.add(newEntry);
                    /**
                     * all awards - secondary
                     */
                    sum += entry.getValue();
                }
                if (!actorAwardsAsStrings.contains(award)) {
                    ok = 0;
                    break;
                }
            }
            if (ok == 1) {
                ElPQ el = new ElPQ();
                el.numberOfAwards = sum;
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
