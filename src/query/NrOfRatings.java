package query;

import fileio.ActionInputData;
import org.json.simple.JSONObject;
import user.UserManager;

import java.util.List;
import java.util.PriorityQueue;

public class NrOfRatings extends QueryManager {

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
     * @param users
     * @param action
     * @return
     */
    public JSONObject nrOfRatings(final List<UserManager> users,
                               final ActionInputData action) {
        JSONObject jo = new JSONObject();
        PriorityQueue<Favorite.ElPQ> pq = ascOrDec(action);
        int n = action.getNumber();

        for (UserManager user : users) {
            int moviesRatings = user.getMoviesRatings().size();
            int showsRatings = user.getShowsRatings().size();
            if (moviesRatings + showsRatings != 0) {
                Favorite.ElPQ el = new Favorite.ElPQ();
                el.setNumberOfFavorites(moviesRatings + showsRatings);
                el.setTitle(user.getUsername());
                pq.add(el);
            }
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
