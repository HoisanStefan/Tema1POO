package query;

import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;

public class Favorite extends QueryManager {
    public static final class ElPQ {
        private String title;
        private Integer numberOfFavorites;

        @Override
        public String toString() {
            return "ElPQ{"
                    + "title='" + title + '\''
                    + ", numberOfFavorites=" + numberOfFavorites
                    + '}';
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public Integer getNumberOfFavorites() {
            return numberOfFavorites;
        }

        public void setNumberOfFavorites(final Integer numberOfFavorites) {
            this.numberOfFavorites = numberOfFavorites;
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
            if (p1.numberOfFavorites < p2.numberOfFavorites) {
                return 1;
            } else if (p1.numberOfFavorites > p2.numberOfFavorites) {
                return -1;
            } else if (p1.numberOfFavorites == p2.numberOfFavorites) {
                return p2.title.compareTo(p1.title);
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
            if (p1.numberOfFavorites > p2.numberOfFavorites) {
                return 1;
            } else if (p1.numberOfFavorites < p2.numberOfFavorites) {
                return -1;
            } else if (p1.numberOfFavorites == p2.numberOfFavorites) {
                return p1.title.compareTo(p2.title);
            }
            return 0;
        }
    }

    /**
     * @param favoriteStorage
     * @param movie
     * @return
     */
    public static Map<String, Integer> add(final Map<String, Integer> favoriteStorage,
                                           final MovieInputData movie) {
        if (favoriteStorage.containsKey(movie.getTitle())) {
            int oldValue = favoriteStorage.get(movie.getTitle());
            favoriteStorage.replace(movie.getTitle(), oldValue + 1);
        } else {
            favoriteStorage.put(movie.getTitle(), 1);
        }

        return favoriteStorage;
    }

    /**
     * @param favoriteStorage
     * @param show
     * @return
     */
    public static Map<String, Integer> addSerial(final Map<String, Integer> favoriteStorage,
                                                 final SerialInputData show) {
        if (favoriteStorage.containsKey(show.getTitle())) {
            int oldValue = favoriteStorage.get(show.getTitle());
            favoriteStorage.replace(show.getTitle(), oldValue + 1);
        } else {
            favoriteStorage.put(show.getTitle(), 1);
        }

        return favoriteStorage;
    }

    /**
     * @param users
     * @param action
     * @param movies
     * @return
     */
    public Map<String, Integer> getFavMovies(final List<UserManager> users,
                                             final ActionInputData action,
                                             final List<MovieInputData> movies) {
        Map<String, Integer> favoriteStorage = new HashMap<>();

        for (int i = 0; i < users.size(); ++i) {
            ArrayList<String> favorites = users.get(i).getFavMovies();
            for (String title : favorites) {
                for (int j = 0; j < movies.size(); ++j) {
                    if (movies.get(j).getTitle().equals(title)) {
                        if (action.getFilters().get(0).get(0) != null) {
                            int year = Integer.parseInt(action.getFilters().get(0).get(0));
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (movies.get(j).getGenres().contains(genre)
                                && movies.get(j).getYear() == year) {
                                    favoriteStorage = add(favoriteStorage, movies.get(j));
                                }
                            } else {
                                if (movies.get(j).getYear() == year) {
                                    favoriteStorage = add(favoriteStorage, movies.get(j));
                                }
                            }
                        } else {
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (movies.get(j).getGenres().contains(genre)) {
                                    favoriteStorage = add(favoriteStorage, movies.get(j));
                                }
                            } else {
                                favoriteStorage = add(favoriteStorage, movies.get(j));
                            }
                        }
                    }
                }
            }
        }

        return favoriteStorage;
    }

    /**
     * @param users
     * @param action
     * @param shows
     * @return
     */
    public Map<String, Integer> getFavShows(final List<UserManager> users,
                                            final ActionInputData action,
                                            final List<SerialInputData> shows) {
        Map<String, Integer> favoriteStorage = new HashMap<>();

        for (int i = 0; i < users.size(); ++i) {
            ArrayList<String> favorites = users.get(i).getFavMovies();
            for (String title : favorites) {
                for (int j = 0; j < shows.size(); ++j) {
                    if (shows.get(j).getTitle().equals(title)) {
                        if (action.getFilters().get(0).get(0) != null) {
                            int year = Integer.parseInt(action.getFilters().get(0).get(0));
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (shows.get(j).getGenres().contains(genre)
                                        && shows.get(j).getYear() == year) {
                                    favoriteStorage = addSerial(favoriteStorage, shows.get(j));
                                }
                            } else {
                                if (shows.get(j).getYear() == year) {
                                    favoriteStorage = addSerial(favoriteStorage, shows.get(j));
                                }
                            }
                        } else {
                            if (action.getFilters().get(1).get(0) != null) {
                                String genre = action.getFilters().get(1).get(0);
                                if (shows.get(j).getGenres().contains(genre)) {
                                    favoriteStorage = addSerial(favoriteStorage, shows.get(j));
                                }
                            } else {
                                favoriteStorage = addSerial(favoriteStorage, shows.get(j));
                            }
                        }
                    }
                }
            }
        }

        return favoriteStorage;
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
     * @param users
     * @param action
     * @param movies
     * @param shows
     * @return
     */
    public JSONObject favorite(final List<UserManager> users,
                               final ActionInputData action,
                               final List<MovieInputData> movies,
                               final List<SerialInputData> shows) {
        JSONObject jo = new JSONObject();
        PriorityQueue<ElPQ> pq = ascOrDec(action);
        Map<String, Integer> favoriteStorage;
        int n = action.getNumber();

        if (action.getObjectType().equals("movies")) {
            favoriteStorage = getFavMovies(users, action, movies);
        } else {
            favoriteStorage = getFavShows(users, action, shows);
        }

        for (Map.Entry<String, Integer> entry : favoriteStorage.entrySet()) {
            ElPQ el = new ElPQ();
            el.title = entry.getKey();
            el.numberOfFavorites = entry.getValue();
            pq.add(el);
        }

        String message = "Query result: [";
        while (!pq.isEmpty() && n != 0) {
            message += pq.poll().title;
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
