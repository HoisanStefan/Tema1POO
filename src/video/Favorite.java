package video;

import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;

public class Favorite extends VideoManager {

    public final class ElPQ {
        private String title;
        private Integer favoriteNum;
        private Integer index;

        @Override
        public String toString() {
            return "ElPQ{"
                    + "title='" + title + '\''
                    + ", favoriteNum=" + favoriteNum
                    + ", index=" + index
                    + '}';
        }
    }

    static class AVGComparator implements Comparator<ElPQ> {
        /**
         * Sorting in descending order of views per genre,
         * @param p1
         * @param p2
         * @return
         */
        @Override
        public int compare(final ElPQ p1, final ElPQ p2) {
            if (p1.favoriteNum < p2.favoriteNum) {
                return 1;
            } else if (p1.favoriteNum > p2.favoriteNum) {
                return -1;
            } else if (p1.favoriteNum == p2.favoriteNum) {
                if (p1.index > p2.index) {
                    return 1;
                } else {
                    return -1;
                }
            }
            return 0;
        }
    }

    /**
     * In order to find out the video with the most appearances in
     * users' favorite lists, we need the list of users given as input
     * @param usersInput - from database
     * @param action - input action
     * @param movies - from database
     * @param shows - from database
     * @return
     */
    public JSONObject favorite(final List<UserInputData> usersInput,
                               final ActionInputData action,
                               final List<MovieInputData> movies,
                               final List<SerialInputData> shows) {
        JSONObject jo = new JSONObject();
        Map<String, Integer> favAppearances = new HashMap<>();
        PriorityQueue<ElPQ> pq = new PriorityQueue<>(new AVGComparator());

        for (int i = 0; i < usersInput.size(); ++i) {
            if (action.getUsername().equals(usersInput.get(i).getUsername())) {
                if (usersInput.get(i).getSubscriptionType().equals("BASIC")) {
                    jo.put("message", "FavoriteRecommendation cannot be applied!");
                    jo.put("id", action.getActionId());
                    return jo;
                }
            }
        }

        for (int i = 0; i < usersInput.size(); ++i) {
            ArrayList<String> favVideos = usersInput.get(i).getFavoriteMovies();
            for (String title : favVideos) {
                if (favAppearances.containsKey(title)) {
                    Integer oldValue = favAppearances.get(title);
                    favAppearances.replace(title, oldValue + 1);
                } else {
                    favAppearances.put(title, 1);
                }
            }
        }

        for (int i = 0; i < movies.size(); ++i) {
            if (favAppearances.containsKey(movies.get(i).getTitle())) {
                ElPQ el = new ElPQ();
                el.index = i;
                el.favoriteNum = favAppearances.get(movies.get(i).getTitle());
                el.title = movies.get(i).getTitle();
                pq.add(el);
            }
        }

        for (int i = 0; i < shows.size(); ++i) {
            if (favAppearances.containsKey(shows.get(i).getTitle())) {
                ElPQ el = new ElPQ();
                el.index = movies.size() + i;
                el.favoriteNum = favAppearances.get(shows.get(i).getTitle());
                el.title = shows.get(i).getTitle();
                pq.add(el);
            }
        }

        /*
        We update the PQ storing the favorite videos
         */
        setPqFavorite(pq);

        while (!pq.isEmpty()) {
            ElPQ el = pq.poll();
            for (int j = 0; j < usersInput.size(); ++j) {
                if (usersInput.get(j).getUsername().equals(action.getUsername())) {
                    Map<String, Integer> userHistory = usersInput.get(j).getHistory();
                    if (!userHistory.containsKey(el.title)) {
                        String message = "FavoriteRecommendation result: ";
                        message += el.title;
                        jo.put("id", action.getActionId());
                        jo.put("message", message);
                        return jo;
                    }
                }
            }
        }

        jo.put("id", action.getActionId());
        jo.put("message", "FavoriteRecommendation cannot be applied!");

        return jo;
    }
}
