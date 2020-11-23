package query;

import entertainment.Season;
import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Longest extends QueryManager {

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
     * @param pq
     * @param movie
     * @return
     */
    public PriorityQueue<Favorite.ElPQ> add(final PriorityQueue<Favorite.ElPQ> pq,
                                            final MovieInputData movie) {
        Favorite.ElPQ el = new Favorite.ElPQ();
        el.setNumberOfFavorites(movie.getDuration());
        el.setTitle(movie.getTitle());
        pq.add(el);
        return pq;
    }

    /**
     * @param pq
     * @param show
     * @return
     */
    public PriorityQueue<Favorite.ElPQ> addSerial(final PriorityQueue<Favorite.ElPQ> pq,
                                                  final SerialInputData show) {
        Favorite.ElPQ el = new Favorite.ElPQ();
        ArrayList<Season> seasons = show.getSeasons();
        int totalDuration = 0;

        for (Season season : seasons) {
            totalDuration += season.getDuration();
        }

        el.setTitle(show.getTitle());
        el.setNumberOfFavorites(totalDuration);
        pq.add(el);

        return pq;
    }

    /**
     * @param action
     * @param movies
     * @return
     */
    public PriorityQueue<Favorite.ElPQ> getPQMovies(final ActionInputData action,
                                                    final List<MovieInputData> movies) {
        PriorityQueue<Favorite.ElPQ> pq = ascOrDec(action);

        for (int i = 0; i < movies.size(); ++i) {
            if (action.getFilters().get(0).get(0) != null) {
                int year = Integer.parseInt(action.getFilters().get(0).get(0));
                if (action.getFilters().get(1).get(0) != null) {
                    String genre = action.getFilters().get(1).get(0);
                    if (movies.get(i).getGenres().contains(genre)
                            && movies.get(i).getYear() == year) {
                        pq = add(pq, movies.get(i));
                    }
                } else {
                    if (movies.get(i).getYear() == year) {
                        pq = add(pq, movies.get(i));
                    }
                }
            } else {
                if (action.getFilters().get(1).get(0) != null) {
                    String genre = action.getFilters().get(1).get(0);
                    if (movies.get(i).getGenres().contains(genre)) {
                        pq = add(pq, movies.get(i));
                    }
                } else {
                    pq = add(pq, movies.get(i));
                }
            }
        }

        return pq;
    }

    /**
     * @param action
     * @param shows
     * @return
     */
    public PriorityQueue<Favorite.ElPQ> getPQShows(final ActionInputData action,
                                                    final List<SerialInputData> shows) {
        PriorityQueue<Favorite.ElPQ> pq = ascOrDec(action);
        for (int i = 0; i < shows.size(); ++i) {
            if (action.getFilters().get(0).get(0) != null) {
                int year = Integer.parseInt(action.getFilters().get(0).get(0));
                if (action.getFilters().get(1).get(0) != null) {
                    String genre = action.getFilters().get(1).get(0);
                    if (shows.get(i).getGenres().contains(genre)
                            && shows.get(i).getYear() == year) {
                        pq = addSerial(pq, shows.get(i));
                    }
                } else {
                    if (shows.get(i).getYear() == year) {
                        pq = addSerial(pq, shows.get(i));
                    }
                }
            } else {
                if (action.getFilters().get(1).get(0) != null) {
                    String genre = action.getFilters().get(1).get(0);
                    if (shows.get(i).getGenres().contains(genre)) {
                        pq = addSerial(pq, shows.get(i));
                    }
                } else {
                    pq = addSerial(pq, shows.get(i));
                }
            }
        }

        return pq;
    }

    /**
     * @param action
     * @param movies
     * @param shows
     * @return
     */
    public JSONObject longest(final ActionInputData action,
                              final List<MovieInputData> movies,
                              final List<SerialInputData> shows) {
        JSONObject jo = new JSONObject();
        PriorityQueue<Favorite.ElPQ> pq;
        int n = action.getNumber();

        if (action.getObjectType().equals("movies")) {
            pq = getPQMovies(action, movies);
        } else {
            pq = getPQShows(action, shows);
        }

        String message = "Query result: [";
        while (!pq.isEmpty() && n != 0) {
            message += pq.poll().getTitle();
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
