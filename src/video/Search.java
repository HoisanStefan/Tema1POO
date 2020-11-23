package video;

import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;

public class Search extends VideoManager {

    public final class ElMap {
        private double ratings;
        private Integer nrOfRatings;

        @Override
        public String toString() {
            return "ElMap{"
                    + "ratings=" + ratings
                    + ", nrOfRatings=" + nrOfRatings
                    + '}';
        }
    }

    public final class ElPQ {
        private String title;
        private double ratingAVG;

        @Override
        public String toString() {
            return "ElPQ{"
                    + "title='" + title + '\''
                    + ", ratingAVG=" + ratingAVG
                    + '}';
        }
    }

    static class AVGComparator implements Comparator<ElPQ> {
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
                return p1.title.compareTo(p2.title);
            }
            return 0;
        }
    }

    /**
     * First, we search for all the rated movies and shows and we store each
     * grade in a map. When the storing action is complete, we add
     * the elements from the map, as well as the rest of the videos, in a PQ
     * @param users
     * @param action
     * @param movies
     * @param shows
     * @return
     */
    public JSONObject search(final List<UserManager> users,
                             final ActionInputData action,
                             final List<MovieInputData> movies,
                             final List<SerialInputData> shows) {
        JSONObject jo = new JSONObject();
        String genre = action.getGenre();
        PriorityQueue<ElPQ> pq = new PriorityQueue<>(new AVGComparator());
        Map<String, Integer> history = new HashMap<>();
        Map<String, ElMap> ratingsStorage = new HashMap<>();

        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getUsername().equals(action.getUsername())) {
                history = users.get(i).getHistory();
                if (users.get(i).getSubscriptionType().equals("BASIC")) {
                    jo.put("message", "SearchRecommendation cannot be applied!");
                    jo.put("id", action.getActionId());
                    return jo;
                }
            }
        }

        for (int i = 0; i < users.size(); ++i) {
            Map<String, Double> movieRatings = users.get(i).getMoviesRatings();
            Map<UserManager.Pair, Double> showRatings = users.get(i).getShowsRatings();
            for (Map.Entry<String, Double> entry : movieRatings.entrySet()) {
                if (!history.containsKey(entry.getKey())) {
                    for (int j = 0; j < movies.size(); ++j) {
                        if (movies.get(j).getGenres().contains(genre)
                        && movies.get(j).getTitle().equals(entry.getKey())) {
                            if (ratingsStorage.containsKey(entry.getKey())) {
                                ElMap oldValue = ratingsStorage.get(entry.getKey());
                                oldValue.nrOfRatings++;
                                oldValue.ratings += entry.getValue();
                                ratingsStorage.replace(entry.getKey(), oldValue);
                            } else {
                                ElMap newValue = new ElMap();
                                newValue.ratings = entry.getValue();
                                newValue.nrOfRatings = 1;
                                ratingsStorage.put(entry.getKey(), newValue);
                            }
                        }
                    }
                }
            }
            for (Map.Entry<UserManager.Pair, Double> entry : showRatings.entrySet()) {
                UserManager.Pair key = entry.getKey();
                if (!history.containsKey(key.getTitle())) {
                    for (int j = 0; j < shows.size(); ++j) {
                        if (shows.get(j).getGenres().contains(genre)
                        && shows.get(j).getTitle().equals(key.getTitle())) {
                            Double value = 0.0;
                            for (int k = 1; k <= shows.get(j).getNumberSeason()
                                    && k != key.getSeason(); ++k) {
                                UserManager temp = new UserManager();
                                UserManager.Pair key2 = new UserManager.Pair();
                                key2.setTitle(key.getTitle());
                                key2.setSeason(k);
                                if (showRatings.containsKey(key2)) {
                                    value += showRatings.get(key2);
                                    showRatings.remove(key2);
                                }
                            }
                            if (ratingsStorage.containsKey(key.getTitle())) {
                                ElMap oldValue = ratingsStorage.get(key.getTitle());
                                oldValue.ratings += value;
                                oldValue.nrOfRatings += shows.get(j).getNumberSeason();
                                ratingsStorage.replace(key.getTitle(), oldValue);
                            } else {
                                ElMap newValue = new ElMap();
                                newValue.ratings = value;
                                newValue.nrOfRatings = shows.get(j).getNumberSeason();
                                ratingsStorage.put(key.getTitle(), newValue);
                            }
                        }
                    }
                }
            }
        }

        /*
          ratingStorage has all rated videos
          (unrated videos are missing)
         */
        for (Map.Entry<String, ElMap> entry : ratingsStorage.entrySet()) {
            ElPQ el = new ElPQ();
            ElMap values = entry.getValue();
            el.ratingAVG = (values.ratings / values.nrOfRatings);
            el.title = entry.getKey();
            pq.add(el);
        }

        /*
          Adding unrated and unseen movies that belong to the genre
         */
        for (int i = 0; i < movies.size(); ++i) {
            if (!history.containsKey(movies.get(i).getTitle())) {
                if (movies.get(i).getGenres().contains(genre)
                        && !ratingsStorage.containsKey(movies.get(i).getTitle())) {
                    ElPQ el = new ElPQ();
                    el.title = movies.get(i).getTitle();
                    el.ratingAVG = 0;
                    pq.add(el);
                }
            }
        }

        /*
          Adding unrated and unseen shows that belong to the genre
         */
        for (int i = 0; i < shows.size(); ++i) {
            if (!history.containsKey(shows.get(i).getTitle())) {
                if (shows.get(i).getGenres().contains(genre)
                        && !ratingsStorage.containsKey(shows.get(i).getTitle())) {
                    ElPQ el = new ElPQ();
                    el.title = shows.get(i).getTitle();
                    el.ratingAVG = 0;
                    pq.add(el);
                }
            }
        }

        if (pq.isEmpty()) {
            String message = "SearchRecommendation cannot be applied!";
            jo.put("id", action.getActionId());
            jo.put("message", message);
            return jo;
        }

        String message = "SearchRecommendation result: [";
        while (!pq.isEmpty()) {
            ElPQ el = pq.poll();
            message += el.title;
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
