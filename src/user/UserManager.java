package user;

import common.Constants;
import fileio.ActionInputData;
import fileio.UserInputData;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserManager extends Handler {

    private ArrayList<String> fav_movies;
    private String username;
    private Map<String, Integer> history;
    private String subscriptionType;

    public UserManager() {}

    public UserManager(UserInputData user) {
        this.fav_movies = user.getFavoriteMovies();
        this.username = user.getUsername();
        this.history = user.getHistory();
        this.subscriptionType = user.getSubscriptionType();
    }

    /*public UserManager whichUser(List<UserManager> Users, ActionInputData action) {
        UserManager user = null;
        for (int i = 0; i < Users.size(); ++i) {
            if (action.getUsername().equals(Users.get(i).getUsername())) {
                user = Users.get(i);
                return user;
            }
        }
        return user;
    }*/

    /**
     * The process of adding a new title to the favorite list
     * @param Users - a list with all the users
     * @param action - the command we need to implement
     * @return jo - a JSONObject which stores the message
     */
    public JSONObject favorite(List<UserManager> Users, ActionInputData action) {
        JSONObject jo = new JSONObject();
        int occurrence = 0;

        for (int i = 0; i < Users.size(); ++i) {
            UserManager user = Users.get(i);
            /**
             * We search the user for whom the action is applied to
             */
            if (action.getUsername().equals(user.getUsername())) {
                Map<String, Integer> history = user.getHistory();
                /**
                 * We search through the user's history in order to
                 * find the occurrence of the movie title
                 */
                if (history.containsKey(action.getTitle()) &&
                        !user.getFav_movies().contains(action.getTitle())) {
                    ArrayList<String> newFavMovies = user.getFav_movies();
                    newFavMovies.add(action.getTitle());
                    user.setFav_movies(newFavMovies);
                    super.Users = Users;
                    super.Users.set(i, user);
                    occurrence = 1;
                }
                /**
                 * If we try to add a favorite video that's already in the
                 * favorite list -> duplicate
                 */
                if (history.containsKey(action.getTitle()) &&
                        user.getFav_movies().contains(action.getTitle())
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
        } else if (occurrence == 2) {
            String message = "error -> ";
            message += action.getTitle() + " is already in favourite list";
            jo.put("id", action.getActionId());
            jo.put("message", message);
        }

        return jo;
    }

    public JSONObject view(List<UserManager> Users, ActionInputData action) {
        JSONObject jo = new JSONObject();
        UserManager user = new UserManager();

        for (int i = 0; i < Users.size(); ++i) {
            user = Users.get(i);
            if (action.getUsername().equals(user.getUsername())) {
                Map<String, Integer> history = user.getHistory();
                if (history.containsKey(action.getTitle())) {
                    history.put(action.getTitle(), history.get(action.getTitle() + 1));
                } else {
                    history.put(action.getTitle(), 1);
                }

                user.setHistory(history);
                super.Users = Users;
                super.Users.set(i, user);
            }
        }

        String message = "success -> ";
        message += action.getTitle() + " was viewed with total views of ";
        message += user.history.get(action.getTitle());
        jo.put("id", action.getActionId());
        jo.put("message", message);

        return jo;
    }

    public ArrayList<String> getFav_movies() {
        return fav_movies;
    }

    public void setFav_movies(ArrayList<String> fav_movies) {
        this.fav_movies = fav_movies;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Integer> history) {
        this.history = history;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }
}

