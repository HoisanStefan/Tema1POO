package query;

import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;

public class MostViewed extends QueryManager {

    /**
     * @param action
     * @return
     */
    public PriorityQueue<Favorite.ElPQ> ascOrDec(final ActionInputData action) {
        if (action.getSortType().equals("asc")) {
            return new PriorityQueue<>(new Favorite.ASCComparator());
        } else {
            return new PriorityQueue<>(new Favorite.DESComparator());
        }
    }

    /**
     * @param viewStorage
     * @param movie
     * @param value
     * @return
     */
    public Map<String, Integer> add(final Map<String, Integer> viewStorage,
                                    final MovieInputData movie,
                                    final Integer value) {
        if (viewStorage.containsKey(movie.getTitle())) {
            Integer oldValue = viewStorage.get(movie.getTitle());
            viewStorage.replace(movie.getTitle(), oldValue + value);
        } else {
            viewStorage.put(movie.getTitle(), value);
        }

        return viewStorage;
    }

    /**
     * @param viewStorage
     * @param show
     * @param value
     * @return
     */
    public Map<String, Integer> addSerial(final Map<String, Integer> viewStorage,
                                          final SerialInputData show,
                                          final Integer value) {
        if (viewStorage.containsKey(show.getTitle())) {
            Integer oldValue = viewStorage.get(show.getTitle());
            viewStorage.replace(show.getTitle(), oldValue + value);
        } else {
            viewStorage.put(show.getTitle(), value);
        }

        return viewStorage;
    }

    /**
     * @param users
     * @param action
     * @param movies
     * @return
     */
    public Map<String, Integer> getWatchedMovies(final List<UserManager> users,
                                                 final ActionInputData action,
                                                 final List<MovieInputData> movies) {
        Map<String, Integer> viewStorage = new HashMap<>();

        for (UserManager user : users) {
            Map<String, Integer> history = user.getHistory();
            for (Map.Entry<String, Integer> entry : history.entrySet()) {
                for (MovieInputData movie : movies) {
                    if (movie.getTitle().equals(entry.getKey())) {
                        if (action.getFilters().get(0).get(0) != null) {
                            int year = Integer.parseInt(action.getFilters().get(0).get(0));
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (movie.getGenres().contains(genre)
                                        && movie.getYear() == year) {
                                    viewStorage =
                                            add(viewStorage, movie, entry.getValue());
                                }
                            } else {
                                if (movie.getYear() == year) {
                                    viewStorage =
                                            add(viewStorage, movie, entry.getValue());
                                }
                            }
                        } else {
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (movie.getGenres().contains(genre)) {
                                    viewStorage =
                                            add(viewStorage, movie, entry.getValue());
                                }
                            } else {
                                viewStorage = add(viewStorage, movie, entry.getValue());
                            }
                        }
                    }
                }
            }
        }

        return viewStorage;
    }

    /**
     * @param users
     * @param action
     * @param shows
     * @return
     */
    public Map<String, Integer> getWatchedShows(final List<UserManager> users,
                                                final ActionInputData action,
                                                final List<SerialInputData> shows) {
        Map<String, Integer> viewStorage = new HashMap<>();

        for (UserManager user : users) {
            Map<String, Integer> history = user.getHistory();
            for (Map.Entry<String, Integer> entry : history.entrySet()) {
                for (SerialInputData show : shows) {
                    if (show.getTitle().equals(entry.getKey())) {
                        if (action.getFilters().get(0).get(0) != null) {
                            int year = Integer.parseInt(action.getFilters().get(0).get(0));
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (show.getGenres().contains(genre)
                                        && show.getYear() == year) {
                                    viewStorage =
                                            addSerial(viewStorage, show, entry.getValue());
                                }
                            } else {
                                if (show.getYear() == year) {
                                    viewStorage =
                                            addSerial(viewStorage, show, entry.getValue());
                                }
                            }
                        } else {
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (show.getGenres().contains(genre)) {
                                    viewStorage =
                                            addSerial(viewStorage, show, entry.getValue());
                                }
                            } else {
                                viewStorage = addSerial(viewStorage, show, entry.getValue());
                            }
                        }
                    }
                }
            }
        }

        return viewStorage;
    }

    /**
     * @param users
     * @param action
     * @param movies
     * @param shows
     * @return
     */
    public JSONObject mostViewed(final List<UserManager> users,
                                 final ActionInputData action,
                                 final List<MovieInputData> movies,
                                 final List<SerialInputData> shows) {
        JSONObject jo = new JSONObject();
        PriorityQueue<Favorite.ElPQ> pq = ascOrDec(action);
        Map<String, Integer> viewStorage;
        int n = action.getNumber();

        if (action.getObjectType().equals("movies")) {
            viewStorage = getWatchedMovies(users, action, movies);
        } else {
            viewStorage = getWatchedShows(users, action, shows);
        }

        for (Map.Entry<String, Integer> entry : viewStorage.entrySet()) {
            Favorite.ElPQ el = new Favorite.ElPQ();
            el.setTitle(entry.getKey());
            el.setNumberOfFavorites(entry.getValue());
            pq.add(el);
        }

        StringBuilder message = new StringBuilder("Query result: [");
        while (!pq.isEmpty() && n != 0) {
            message.append(pq.poll().getTitle());
            n--;
            if (!pq.isEmpty() && n != 0) {
                message.append(", ");
            }
        }

        message.append("]");
        jo.put("id", action.getActionId());
        jo.put("message", message.toString());

        return jo;
    }
}
