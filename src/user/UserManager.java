package user;

import fileio.ActionInputData;
import fileio.UserInputData;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class UserManager extends Handler {

    private ArrayList<String> favMovies;
    private String username;
    private Map<String, Integer> history;
    private String subscriptionType;

    public UserManager() { }

    public UserManager(final UserInputData user) {
        this.favMovies = user.getFavoriteMovies();
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
     * @param users - a list with all the users
     * @param action - the command we need to implement
     * @return jo - a JSONObject which stores the message
     */
    public JSONObject favorite(final List<UserManager> users, final ActionInputData action) {
        JSONObject jo = new JSONObject();
        int occurrence = 0;

        for (int i = 0; i < users.size(); ++i) {
            UserManager user = users.get(i);
            /**
             * We search the user for whom the action is applied to
             */
            if (action.getUsername().equals(user.getUsername())) {
                Map<String, Integer> newHistory = user.getHistory();
                /**
                 * We search through the user's history in order to
                 * find the occurrence of the movie title
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
                /**
                 * If we try to add a favorite video that's already in the
                 * favorite list -> duplicate
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
        } else if (occurrence == 2) {
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
}

