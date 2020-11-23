package query;

import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;

public class Average extends QueryManager {

    public static final class ElMap {
        private double ratings;
        private Integer nrOfRatings;

        @Override
        public String toString() {
            return "ElMap{"
                    + "ratings=" + ratings
                    + ", nrOfRatings=" + nrOfRatings
                    + '}';
        }

        public double getRatings() {
            return ratings;
        }

        public void setRatings(final double ratings) {
            this.ratings = ratings;
        }

        public Integer getNrOfRatings() {
            return nrOfRatings;
        }

        public void setNrOfRatings(final Integer nrOfRatings) {
            this.nrOfRatings = nrOfRatings;
        }
    }

    public static final class ElPQ {
        private String name;
        private double ratingAVG;

        @Override
        public String toString() {
            return "ElPQ{"
                    + "name='" + name + '\''
                    + ", ratingAVG=" + ratingAVG
                    + '}';
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setRatingAVG(final double ratingAVG) {
            this.ratingAVG = ratingAVG;
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
            if (p1.ratingAVG < p2.ratingAVG) {
                return 1;
            } else if (p1.ratingAVG > p2.ratingAVG) {
                return -1;
            } else if (p1.ratingAVG == p2.ratingAVG) {
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
            if (p1.ratingAVG > p2.ratingAVG) {
                return 1;
            } else if (p1.ratingAVG < p2.ratingAVG) {
                return -1;
            } else if (p1.ratingAVG == p2.ratingAVG) {
                return p1.name.compareTo(p2.name);
            }
            return 0;
        }
    }

    /**
     * @param action
     * @return
     */
    public PriorityQueue<Average.ElPQ> ascOrDec(final ActionInputData action) {
        if (action.getSortType().equals("asc")) {
            return new PriorityQueue<>(new Average.ASCComparator());
        } else {
            return new PriorityQueue<>(new Average.DESComparator());
        }
    }

    /**
     *
     * @param actors
     * @param users
     * @param action
     * @param shows
     * @return
     */
    public JSONObject average(final List<ActorInputData> actors, final List<UserManager> users,
                              final ActionInputData action, final List<SerialInputData> shows) {
        JSONObject jo = new JSONObject();
        PriorityQueue<ElPQ> pq = ascOrDec(action);
        Integer n = action.getNumber();
        List<String> filmography = new ArrayList<>();
        Map<String, ElMap> ratingsStorage;

        for (int i = 0; i < actors.size(); ++i) {
            List<String> actorFilmography = actors.get(i).getFilmography();
            for (String video : actorFilmography) {
                if (!filmography.contains(video)) {
                    filmography.add(video);
                }
            }
        }

        ratingsStorage = ratingsStorage(users, filmography, shows);

        Map<String, ElMap> actorsAndTheirRatings = new HashMap<>();
        for (int i = 0; i < actors.size(); ++i) {
            ArrayList<String> actorFilmography = actors.get(i).getFilmography();
            for (String el : actorFilmography) {
                if (ratingsStorage.containsKey(el)) {
                    if (actorsAndTheirRatings.containsKey(actors.get(i).getName())) {
                        ElMap oldValue = actorsAndTheirRatings.get(actors.get(i).getName());
                        ElMap rating = ratingsStorage.get(el);
                        oldValue.ratings += rating.ratings / rating.nrOfRatings;
                        oldValue.nrOfRatings++;
                        actorsAndTheirRatings.replace(actors.get(i).getName(), oldValue);
                    } else {
                        ElMap newValue = new ElMap();
                        ElMap rating = ratingsStorage.get(el);
                        newValue.nrOfRatings = 1;
                        newValue.ratings = rating.ratings / rating.nrOfRatings;
                        actorsAndTheirRatings.put(actors.get(i).getName(), newValue);
                    }
                }
            }
        }

        for (Map.Entry<String, ElMap> entry : actorsAndTheirRatings.entrySet()) {
            ElPQ el = new ElPQ();
            el.ratingAVG = entry.getValue().ratings / entry.getValue().nrOfRatings;
            el.name = entry.getKey();
            pq.add(el);
        }

        String message = "Query result: [";
        while (!pq.isEmpty() && n != 0) {
            message += pq.poll().name;
            n--;
            if (!pq.isEmpty() && n != 0) {
                message += ", ";
            }
        }

        message += "]";
        jo.put("id", action.getActionId());
        jo.put("message", message);

        return jo;
    }
}
