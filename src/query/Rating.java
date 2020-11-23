package query;

import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;

public class Rating extends QueryManager {

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
     * @param movies
     * @param action
     * @return
     */
    public List<String> getMoviesFilmography(final List<MovieInputData> movies,
                                             final ActionInputData action) {
        List<String> filmography = new ArrayList<>();
        String genre = action.getFilters().get(1).get(0);

        for (int i = 0; i < movies.size(); ++i) {
            if (!filmography.contains(movies.get(i).getTitle())) {
                if (action.getFilters().get(0).get(0) != null) {
                    int year = Integer.parseInt(action.getFilters().get(0).get(0));
                    if (movies.get(i).getYear() == year
                            && movies.get(i).getGenres().contains(genre)) {
                        filmography.add(movies.get(i).getTitle());
                    }
                } else {
                    if (movies.get(i).getGenres().contains(genre)) {
                        filmography.add(movies.get(i).getTitle());
                    }
                }
            }
        }

        return filmography;
    }

    /**
     * @param shows
     * @param action
     * @return
     */
    public List<String> getShowsFilmography(final List<SerialInputData> shows,
                                            final ActionInputData action) {
        List<String> filmography = new ArrayList<>();
        String genre = action.getFilters().get(1).get(0);

        for (int i = 0; i < shows.size(); ++i) {
            if (!filmography.contains(shows.get(i).getTitle())) {
                if (action.getFilters().get(0).get(0) != null) {
                    int year = Integer.parseInt(action.getFilters().get(0).get(0));
                    if (shows.get(i).getYear() == year
                            && shows.get(i).getGenres().contains(genre)) {
                        filmography.add(shows.get(i).getTitle());
                    }
                } else {
                    if (shows.get(i).getGenres().contains(genre)) {
                        filmography.add(shows.get(i).getTitle());
                    }
                }
            }
        }

        return filmography;
    }

    /**
     * @param users
     * @param action
     * @param movies
     * @param shows
     * @return
     */
    public JSONObject rating(final List<UserManager> users,
                             final ActionInputData action,
                             final List<MovieInputData> movies,
                             final List<SerialInputData> shows) {

        JSONObject jo = new JSONObject();
        List<String> filmography;
        PriorityQueue<Average.ElPQ> pq = ascOrDec(action);
        Map<String, Average.ElMap> ratingsStorage;
        int n = action.getNumber();

        if (action.getObjectType().equals("movies")) {
            filmography = getMoviesFilmography(movies, action);
        } else {
            filmography = getShowsFilmography(shows, action);
        }

        ratingsStorage = ratingsStorage(users, filmography, shows);
        for (Map.Entry<String, Average.ElMap> entry : ratingsStorage.entrySet()) {
            Average.ElPQ el = new Average.ElPQ();
            Average.ElMap value = entry.getValue();
            el.setName(entry.getKey());
            el.setRatingAVG(value.getRatings() / value.getNrOfRatings());
            pq.add(el);
        }

        String message = "Query result: [";
        while (!pq.isEmpty() && n != 0) {
            message += pq.poll().getName();
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
