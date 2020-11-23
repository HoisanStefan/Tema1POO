package query;

import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.List;
import java.util.Map;

public class QueryManager extends user.Handler {

    /**
     * @param actors
     * @param users
     * @param action
     * @param shows
     * @return
     */
    public JSONObject average(final List<ActorInputData> actors,
                              final List<UserManager> users,
                              final ActionInputData action,
                              final List<SerialInputData> shows) {
        Average helpAverage = new Average();
        return helpAverage.average(actors, users, action, shows);
    }

    /**
     * @param actors
     * @param action
     * @return
     */
    public JSONObject awards(final List<ActorInputData> actors,
                             final ActionInputData action) {
        Awards helpAwards = new Awards();
        return helpAwards.awards(actors, action);
    }

    /**
     * @param actors
     * @param action
     * @return
     */
    public JSONObject filterDescription(final List<ActorInputData> actors,
                                        final ActionInputData action) {
        FilterDescription helpFilterDescription = new FilterDescription();
        return helpFilterDescription.filterDescription(actors, action);
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
        Rating helpRating = new Rating();
        return helpRating.rating(users, action, movies, shows);
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
        Favorite helpFavorite = new Favorite();
        return helpFavorite.favorite(users, action, movies, shows);
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
        MostViewed helpMostViewed = new MostViewed();
        return helpMostViewed.mostViewed(users, action, movies, shows);
    }

    /**
     * @param users
     * @param action
     * @return
     */
    public JSONObject nrOfRatings(final List<UserManager> users,
                                  final ActionInputData action) {
        NrOfRatings helpNrOfRatings = new NrOfRatings();
        return helpNrOfRatings.nrOfRatings(users, action);
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
        Longest helpLongest = new Longest();
        return helpLongest.longest(action, movies, shows);
    }

    /**
     * @param users
     * @param filmography
     * @param shows
     * @return
     */
    public Map<String, Average.ElMap> ratingsStorage(final List<UserManager> users,
                                                     final List<String> filmography,
                                                     final List<SerialInputData> shows) {
        RatingsStorage rs = new RatingsStorage();
        return rs.ratingsStorage(users, filmography, shows);
    }
}
