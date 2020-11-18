package video;

import fileio.ActionInputData;
import fileio.MovieInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class VideoManager extends user.Handler {
    protected Map<String, Pair> moviesRated = new HashMap<>();

    public VideoManager() { }

    public final class Pair {
        private double ratingsAcc = 0;
        private Integer ratingsNumber = 0;

        @Override
        public String toString() {
            return "Pair{"
                    + "ratingsAcc=" + ratingsAcc
                    + ", ratingsNumber=" + ratingsNumber
                    + '}';
        }
    }

    public final class ElPQ {
        private String title;
        private Integer index;
        private double ratingAVG;

        @Override
        public String toString() {
            return "ElPQ{"
                    + "title='" + title + '\''
                    + ", index=" + index
                    + ", ratingAVG=" + ratingAVG
                    + '}';
        }
    }

    class AVGComparator implements Comparator<ElPQ> {
        /**
         * Sorting in descending order of ratings average,
         * if 2 averages are equal, we store the first one
         * encountered in the database
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
                if (p1.index > p2.index) {
                    return 1;
                } else if (p1.index < p2.index) {
                    return -1;
                }
            }
            return 0;
        }
    }

    /**
     * Storing the data from a movie that's been seen
     * @param key
     * @param value
     * @return
     */
    public Map<String, Pair> addElement(final String key, final double value) {
        Pair data = new Pair();
        if (this.moviesRated.containsKey(key)) {
            data = this.moviesRated.get(key);
            data.ratingsAcc += value;
            data.ratingsNumber++;
            this.moviesRated.replace(key, data);
        } else {
            data.ratingsAcc = value;
            data.ratingsNumber = 1;
            this.moviesRated.put(key, data);
        }

        return this.moviesRated;
    }

    /**
     * Parsing all the users and adding their ratings
     * @param users
     */
    public void parsingUsers(final List<UserManager> users) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getMoviesRatings().size() != 0) {
                Map<String, Double> mapMovie = users.get(i).getMoviesRatings();
                for (Map.Entry<String, Double> entry : mapMovie.entrySet()) {
                    this.addElement(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     *
     * @param users - list of users
     * @param action
     * @param movies - list of movies from database
     * @return
     */
    public JSONObject bestUnseen(final List<UserManager> users, final ActionInputData action,
                                 final List<MovieInputData> movies) {
        JSONObject jo = new JSONObject();
        parsingUsers(users);
        Pair data;
        PriorityQueue<ElPQ> pq = new PriorityQueue<>(new AVGComparator());
        Map<String, Integer> moviesIndices = new HashMap<>();
        /**
         * We store in a map the indices for each movie (we will need them for
         * priority)
         */
        for (int i = 0; i < movies.size(); ++i) {
            if (!moviesIndices.containsKey(movies.get(i).getTitle())) {
                moviesIndices.put(movies.get(i).getTitle(), i);
            }
        }

        for (Map.Entry<String, Pair> entry : moviesRated.entrySet()) {
            if (moviesIndices.containsKey(entry.getKey())) {
                data = entry.getValue();
                ElPQ el = new ElPQ();
                el.title = entry.getKey();
                el.index = moviesIndices.get(entry.getKey());
                el.ratingAVG = data.ratingsAcc / data.ratingsNumber;
                pq.add(el);
            }
        }

        /**
         * pq now stores all the movies in descending order of ratings,
         * if we have 2 or more equal average grades, the first element
         * will be the first one in the database
         */
        for (int i = 0; i < users.size(); ++i) {
            if (action.getUsername().equals(users.get(i).getUsername())) {
                Map<String, Integer> history = users.get(i).getHistory();
                while (!pq.isEmpty()) {
                    ElPQ element = pq.poll();
                    if (!history.containsKey(element.title)) {
                        String message = "BestRatedUnseenRecommendation result: ";
                        message += element.title;
                        jo.put("id", action.getActionId());
                        jo.put("message", message);
                        return jo;
                    }
                }
                /**
                 * If we get here, that means the user has to see an unrated
                 * video, because all rated videos have been seen already
                 */
                for (int j = 0; j < movies.size(); ++j) {
                    if (!users.get(i).getHistory().containsKey(movies.get(j).getTitle())) {
                        String message = "BestRatedUnseenRecommendation result: ";
                        message += movies.get(j).getTitle();
                        jo.put("id", action.getActionId());
                        jo.put("message", message);
                        return jo;
                    }
                }
            }
        }
        /**
         * If we're still here, it means that the user had seen all the videos
         */
        String message = "BestRatedUnseenRecommendation cannot be applied!";
        jo.put("id", action.getActionId());
        jo.put("message", message);
        return jo;
    }

    /**
     *
     * @param users
     * @param action
     * @return
     */
    public JSONObject favorite(final List<UserManager> users, final ActionInputData action) {
        JSONObject jo = new JSONObject();

        return jo;
    }

    /**
     *
     * @param users - list of users
     * @param action
     * @param movies - list of movies from database
     * @return
     */
    public JSONObject standard(final List<UserManager> users, final ActionInputData action,
                               final List<MovieInputData> movies) {
        JSONObject jo = new JSONObject();
        UserManager user;
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getUsername().equals(action.getUsername())) {
                user = users.get(i);
                for (MovieInputData movie : movies) {
                    if (!user.getHistory().containsKey(movie.getTitle())) {
                        String message = "StandardRecommendation result: ";
                        message += movie.getTitle();
                        jo.put("id", action.getActionId());
                        jo.put("message", message);
                        return jo;
                    }
                }
            }
        }
        /**
         * If we get here, it means that the user had seen all the videos already
         */
        String message = "StandardRecommendation cannot be applied";
        jo.put("id", action.getActionId());
        jo.put("message", message);
        return jo;
    }

    public Map<String, Pair> getMoviesRated() {
        return moviesRated;
    }

    public void setMoviesRated(final Map<String, Pair> moviesRated) {
        this.moviesRated = moviesRated;
    }

    @Override
    public String toString() {
        return "VideoManager{"
                + "moviesRated=" + moviesRated
                + '}';
    }
}
