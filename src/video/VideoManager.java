package video;

import fileio.ActionInputData;
import fileio.Input;
import fileio.MovieInputData;
import fileio.UserInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class VideoManager extends user.Handler {
    protected Map<String, Pair> moviesRated = new HashMap<>();
    protected PriorityQueue<Popular.ElPQ> pqVideosViews =
            new PriorityQueue<>(new Popular.AVGComparator());
    protected PriorityQueue<Favorite.ElPQ> pqFavorite =
            new PriorityQueue<>(new Favorite.AVGComparator());
    protected Input input;

    public VideoManager() { }

    public static final class Pair {
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

    public static final class ElPQ {
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

    static class AVGComparator implements Comparator<ElPQ> {
        /**
         * Sorting in descending order of ratings average,
         * if 2 averages are equal, we store the first one
         * encountered in the database
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
     * @param key key to be mapped
     * @param value value stored for the key
     */
    public void addElement(final String key, final double value) {
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
    }

    /**
     * Parsing all the users and adding their ratings
     * @param users - List of current users
     */
    public void parsingUsers(final List<UserManager> users) {
        for (UserManager user : users) {
            if (user.getMoviesRatings().size() != 0) {
                Map<String, Double> mapMovie = user.getMoviesRatings();
                for (Map.Entry<String, Double> entry : mapMovie.entrySet()) {
                    this.addElement(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     *
     * @param users - list of users
     * @param action - the action given
     * @param movies - list of movies from database
     * @return A JSONObject with the answer
     */
    public JSONObject bestUnseen(final List<UserManager> users,
                                 final ActionInputData action,
                                 final List<MovieInputData> movies) {
        JSONObject jo = new JSONObject();
        parsingUsers(users);
        Pair data;
        PriorityQueue<ElPQ> pq = new PriorityQueue<>(new AVGComparator());
        Map<String, Integer> moviesIndices = new HashMap<>();

        /*
          We store in a map the indices for each movie (we will need them for
          priority)
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

        /*
          pq now stores all the movies in descending order of ratings,
          if we have 2 or more equal average grades, the first element
          will be the first one in the database

          If we get at the second for, that means the user has to see an unrated
          video, because all rated videos have been seen already
         */
        for (UserManager user : users) {
            if (action.getUsername().equals(user.getUsername())) {
                Map<String, Integer> history = user.getHistory();
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
                for (MovieInputData movie : movies) {
                    if (!user.getHistory().containsKey(movie.getTitle())) {
                        String message = "BestRatedUnseenRecommendation result: ";
                        message += movie.getTitle();
                        jo.put("id", action.getActionId());
                        jo.put("message", message);
                        return jo;
                    }
                }
            }
        }
        /*
          If we're still here, it means that the user had seen all the videos
         */
        String message = "BestRatedUnseenRecommendation cannot be applied!";
        jo.put("id", action.getActionId());
        jo.put("message", message);
        return jo;
    }

    /**
     *
     * @param users - list of users
     * @param action - the action given
     * @return A JSONObject with the answer
     */
    public JSONObject popular(final List<UserManager> users,
                              final ActionInputData action,
                              final List<MovieInputData> movies,
                              final List<SerialInputData> shows) {
        Popular helpPopular = new Popular();
        return helpPopular.popular(users, action, movies, shows);
    }

    /**
     *
     * @param users - list of users
     * @param action - the action given
     * @return A JSONObject with the answer
     */
    public JSONObject favorite(final List<UserInputData> users,
                               final ActionInputData action,
                               final List<MovieInputData> movies,
                               final List<SerialInputData> shows) {
        Favorite helpFavorite = new Favorite();
        return helpFavorite.favorite(users, action, movies, shows);
    }

    /**
     *
     * @param users - list of users
     * @param action - the action given
     * @param movies - list of movies from database
     * @return A JSONObject with the answer
     */
    public JSONObject standard(final List<UserManager> users,
                               final ActionInputData action,
                               final List<MovieInputData> movies) {
        JSONObject jo = new JSONObject();
        UserManager user;
        for (UserManager userManager : users) {
            if (userManager.getUsername().equals(action.getUsername())) {
                user = userManager;
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
        /*
          If we get here, it means that the user had seen all the videos already
         */
        String message = "StandardRecommendation cannot be applied!";
        jo.put("id", action.getActionId());
        jo.put("message", message);
        return jo;
    }

    /**
     *
     * @param users - list of users
     * @param action - the action given
     * @param movies - the list with the movies from database
     * @param shows - the list with the shows from database
     * @return A JSONObject with the answer
     */
    public JSONObject search(final List<UserManager> users,
                             final ActionInputData action,
                             final List<MovieInputData> movies,
                             final List<SerialInputData> shows) {
        Search helpSearch = new Search();
        return helpSearch.search(users, action, movies, shows);
    }


    public final Map<String, Pair> getMoviesRated() {
        return moviesRated;
    }

    public final void setMoviesRated(final Map<String, Pair> moviesRated) {
        this.moviesRated = moviesRated;
    }

    public final void setPqVideosViews(final PriorityQueue<Popular.ElPQ> pqVideos) {
        this.pqVideosViews = pqVideos;
    }

    public final PriorityQueue<Favorite.ElPQ> getPqFavorite() {
        return pqFavorite;
    }

    public final void setPqFavorite(final PriorityQueue<Favorite.ElPQ> pqFavorite) {
        this.pqFavorite = pqFavorite;
    }

    public final Input getInput() {
        return input;
    }

    public final void setInput(final Input input) {
        this.input = input;
    }

    @Override
    public final String toString() {
        return "VideoManager{"
                + "moviesRated=" + moviesRated
                + '}';
    }
}
