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

public class Popular extends VideoManager {

    public static final class ElPQ {
        private String genre;
        private Integer views;
    }

    static class AVGComparator implements Comparator<ElPQ> {
        /**
         * Sorting in descending order of views per genre,
         */
        @Override
        public int compare(final ElPQ p1, final ElPQ p2) {
            if (p1.views < p2.views) {
                return 1;
            } else if (p1.views > p2.views) {
                return -1;
            }
            return 0;
        }
    }

    /**
     * @param users
     * @param action
     * @param movies
     * @param shows
     * @return
     */
    public JSONObject popular(final List<UserManager> users,
                              final ActionInputData action,
                              final List<MovieInputData> movies,
                              final List<SerialInputData> shows) {
        JSONObject jo = new JSONObject();
        Map<String, Integer> genreViews = new HashMap<>();

        for (int i = 0; i < users.size(); ++i) {
            if (action.getUsername().equals(users.get(i).getUsername())) {
                if (users.get(i).getSubscriptionType().equals("BASIC")) {
                    jo.put("message", "PopularRecommendation cannot be applied!");
                    jo.put("id", action.getActionId());
                    return jo;
                }
            }
        }

        for (int i = 0; i < movies.size(); ++i) {
            MovieInputData movie = movies.get(i);
            for (int j = 0; j < users.size(); ++j) {
                Map<String, Integer> history = users.get(j).getHistory();
                if (history.containsKey(movie.getTitle())) {
                    for (int k = 0; k < movie.getGenres().size(); ++k) {
                        String genre = movie.getGenres().get(k);
                        Integer userViews = history.get(movie.getTitle());
                        if (genreViews.containsKey(genre)) {
                            Integer oldValue = genreViews.get(genre);
                            genreViews.replace(genre, oldValue + userViews);
                        } else {
                            genreViews.put(genre, userViews);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < shows.size(); ++i) {
            SerialInputData show = shows.get(i);
            for (int j = 0; j < users.size(); ++j) {
                Map<String, Integer> history = users.get(j).getHistory();
                if (history.containsKey(show.getTitle())) {
                    for (int k = 0; k < show.getGenres().size(); ++k) {
                        String genre = show.getGenres().get(k);
                        Integer userViews = history.get(show.getTitle());
                        if (genreViews.containsKey(genre)) {
                            Integer oldValue = genreViews.get(genre);
                            genreViews.replace(genre, oldValue + userViews);
                        } else {
                            genreViews.put(genre, userViews);
                        }
                    }
                }
            }
        }
        /*
          views will be the total number of views for that genre
          title will be the genre
          we don't care about the indices, so index = 0
         */
        PriorityQueue<ElPQ> pq = new PriorityQueue<>(new AVGComparator());
        for (Map.Entry<String, Integer> entry : genreViews.entrySet()) {
            ElPQ el = new ElPQ();
            el.genre = entry.getKey();
            el.views  = entry.getValue();
            pq.add(el);
        }

        setPqVideosViews(pq);

        while (!pq.isEmpty()) {
            for (int i = 0; i < users.size(); ++i) {
                if (action.getUsername().equals(users.get(i).getUsername())) {
                    Map<String, Integer> history = users.get(i).getHistory();
                    for (int j = 0; j < movies.size(); ++j) {
                        String genre = pq.peek().genre;
                        if (movies.get(j).getGenres().contains(genre)
                                && !history.containsKey(movies.get(j).getTitle())) {
                            String message = "PopularRecommendation result: ";
                            message += movies.get(j).getTitle();
                            jo.put("id", action.getActionId());
                            jo.put("message", message);
                            return jo;
                        }
                    }
                    for (int j = 0; j < shows.size(); ++j) {
                        String genre = pq.peek().genre;
                        if (shows.get(j).getGenres().contains(genre)
                                && !history.containsKey(shows.get(j).getTitle())) {
                            String message = "PopularRecommendation result: ";
                            message += shows.get(j).getTitle();
                            jo.put("id", action.getActionId());
                            jo.put("message", message);
                            return jo;
                        }
                    }
                }
            }
            pq.poll();
        }

        /*
          If we get here, it means that there are no unwatched videos
         */
        jo.put("message", "PopularRecommendation cannot be applied!");
        jo.put("id", action.getActionId());

        return jo;
    }
}
