package user;

import fileio.ActionInputData;
import fileio.UserInputData;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

public final class UserManager extends Handler {
    public static final class Pair {
        private String title;
        private Integer season;

        @Override
        public String toString() {
            return "Pair{"
                    + "title='" + title + '\''
                    + ", season=" + season
                    + '}';
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Pair pair = (Pair) o;
            return Objects.equals(title, pair.title)
                    && Objects.equals(season, pair.season);
        }

        @Override
        public int hashCode() {
            return 0;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public Integer getSeason() {
            return season;
        }

        public void setSeason(final Integer season) {
            this.season = season;
        }
    }

    private ArrayList<String> favMovies;
    private String username;
    private Map<String, Integer> history;
    private String subscriptionType;
    private Map<Pair, Double> showsRatings;
    private Map<String, Double> moviesRatings;
    public UserManager() { }

    public UserManager(final UserInputData user) {
        this.favMovies = user.getFavoriteMovies();
        this.username = user.getUsername();
        this.history = user.getHistory();
        this.subscriptionType = user.getSubscriptionType();
        this.showsRatings = new HashMap<>();
        this.moviesRatings = new HashMap<>();
    }

    /**
     * The process of adding a new title to the favorite list
     * @param users - a list with all the users
     * @param action - the command we need to implement
     * @return jo - a JSONObject which stores the message
     */
    public JSONObject favorite(final List<UserManager> users, final ActionInputData action) {
        JSONObject jo = new JSONObject();
        int occurrence = 0;

        for (int i = 0; i < users.size(); ++i) {
            UserManager user = users.get(i);
            /*
              We search the user for whom the action is applied to
             */
            if (action.getUsername().equals(user.getUsername())) {
                Map<String, Integer> newHistory = user.getHistory();
                /*
                  We search through the user's history in order to
                  find the occurrence of the movie title
                 */
                if (newHistory.containsKey(action.getTitle())
                        && !user.getFavMovies().contains(action.getTitle())) {
                    ArrayList<String> newFavMovies = user.getFavMovies();
                    newFavMovies.add(action.getTitle());
                    user.setFavMovies(newFavMovies);
                    super.users = users;
                    super.users.set(i, user);
                    occurrence = 1;
                }
                /*
                  If we try to add a favorite video that's already in the
                  favorite list -> duplicate
                 */
                if (newHistory.containsKey(action.getTitle())
                        && user.getFavMovies().contains(action.getTitle())
                            && occurrence == 0) {
                    occurrence = 2;
                }
            }
        }

        if (occurrence == 0) {
            String message = "error -> ";
            message += action.getTitle() + " is not seen";
            jo.put("id", action.getActionId());
            jo.put("message", message);
        } else if (occurrence == 1) {
            String message = "success -> ";
            message += action.getTitle() + " was added as favourite";
            jo.put("id", action.getActionId());
            jo.put("message", message);
        } else {
            String message = "error -> ";
            message += action.getTitle() + " is already in favourite list";
            jo.put("id", action.getActionId());
            jo.put("message", message);
        }

        return jo;
    }

    /**
     * @param users
     * @param action
     * @return
     */
    public JSONObject view(final List<UserManager> users, final ActionInputData action) {
        JSONObject jo = new JSONObject();
        UserManager user;
        int value = 0;

        for (int i = 0; i < users.size(); ++i) {
            user = users.get(i);
            if (action.getUsername().equals(user.getUsername())) {
                Map<String, Integer> newHistory = user.getHistory();
                if (newHistory.containsKey(action.getTitle())) {
                    int oldValue = newHistory.get(action.getTitle());
                    newHistory.replace(action.getTitle(), oldValue + 1);
                    value = oldValue + 1;
                } else {
                    newHistory.put(action.getTitle(), 1);
                    value = 1;
                }

                user.setHistory(newHistory);
                super.users = users;
                super.users.set(i, user);
            }
        }

        String message = "success -> ";
        message += action.getTitle() + " was viewed with total views of ";
        message += value;
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
    public JSONObject rating(final List<UserManager> users, final ActionInputData action) {
        JSONObject jo = new JSONObject();
        UserManager user;

        for (int i = 0; i < users.size(); ++i) {
            user = users.get(i);
            if (action.getUsername().equals(user.getUsername())) {
                Map<String, Integer> historyNew = user.getHistory();
                /*
                  If the user has seen the title already
                 */
                if (historyNew.containsKey(action.getTitle())) {
                    /*
                      If we're dealing with a movie
                     */
                    if (action.getSeasonNumber() == 0) {
                        Map<String, Double> moviesRatingsNew = user.getMoviesRatings();
                        /*
                          If we haven't rated this movie before
                         */
                        if (!moviesRatingsNew.containsKey(action.getTitle())) {
                            moviesRatingsNew.put(action.getTitle(), action.getGrade());
                            user.setMoviesRatings(moviesRatingsNew);
                            super.users = users;
                            super.users.set(i, user);
                            String message = "success -> ";
                            message += action.getTitle() + " was rated with "
                                    + action.getGrade() + " by " + action.getUsername();
                            jo.put("id", action.getActionId());
                            jo.put("message", message);
                        } else {
                            String message = "error -> ";
                            message += action.getTitle() + " has been already rated";
                            jo.put("id", action.getActionId());
                            jo.put("message", message);
                        }
                        /*
                          Or else, we're dealing with a show
                         */
                    } else {
                        Map<Pair, Double> showsRatingsNew = user.getShowsRatings();
                        Pair key = new Pair();
                        key.title = action.getTitle();
                        key.season = action.getSeasonNumber();
                        int ok = 1;
                        for (Map.Entry<Pair, Double> entry : showsRatingsNew.entrySet()) {
                            if (entry.getKey().equals(key)) {
                                ok = 0;
                                break;
                            }
                        }
                        /*
                          If we haven't rated this season of the show already
                         */
                        if (ok == 1) {
                            showsRatingsNew.put(key, action.getGrade());
                            user.setShowsRatings(showsRatingsNew);
                            super.users = users;
                            super.users.set(i, user);
                            String message = "success -> ";
                            message += action.getTitle() + " was rated with "
                                    + action.getGrade() + " by " + action.getUsername();
                            jo.put("id", action.getActionId());
                            jo.put("message", message);
                        } else {
                            String message = "error -> ";
                            message += action.getTitle() + " has been already rated";
                            jo.put("id", action.getActionId());
                            jo.put("message", message);
                        }
                    }
                } else {
                    /*
                      If the user hasn't seen the movie yet
                     */
                    String message = "error -> " + action.getTitle() + " is not seen";
                    jo.put("id", action.getActionId());
                    jo.put("message", message);
                }
            }
        }

        return jo;
    }

    public ArrayList<String> getFavMovies() {
        return favMovies;
    }

    public void setFavMovies(final ArrayList<String> favMovies) {
        this.favMovies = favMovies;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(final Map<String, Integer> history) {
        this.history = history;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<Pair, Double> getShowsRatings() {
        return showsRatings;
    }

    public void setShowsRatings(final Map<Pair, Double> showsRatings) {
        this.showsRatings = showsRatings;
    }

    public Map<String, Double> getMoviesRatings() {
        return moviesRatings;
    }

    public void setMoviesRatings(final Map<String, Double> moviesRatings) {
        this.moviesRatings = moviesRatings;
    }
}

