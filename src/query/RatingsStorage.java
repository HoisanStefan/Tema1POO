package query;

import fileio.SerialInputData;
import user.UserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingsStorage extends QueryManager {
    /**
     *
     * @param users - the current state of users list
     * @param filmography - all the titles we need to compute the ratingsAVG for
     * @param shows - the shows database in order to know how many seasons each entry has
     * @return a map storing at each element the title of a video and the data to compute
     * its average
     */
    public Map<String, Average.ElMap> ratingsStorage(final List<UserManager> users,
                                                     final List<String> filmography,
                                                     final List<SerialInputData> shows,
                                                     final boolean flag) {
        Map<String, Average.ElMap> ratingsStorage = new HashMap<>();
        UserManager lastUser = users.get(0);
        for (int i = 0; i < users.size(); ++i) {
            Map<String, Double> movieRatings = users.get(i).getMoviesRatings();
            Map<UserManager.Pair, Double> showRatings = users.get(i).getShowsRatings();
            for (Map.Entry<String, Double> entry : movieRatings.entrySet()) {
                if (filmography.contains(entry.getKey())) {
                    if (ratingsStorage.containsKey(entry.getKey())) {
                        Average.ElMap oldValue = ratingsStorage.get(entry.getKey());
                        oldValue.setNrOfRatings(oldValue.getNrOfRatings() + 1);
                        oldValue.setRatings(oldValue.getRatings() + entry.getValue());
                        ratingsStorage.replace(entry.getKey(), oldValue);
                    } else {
                        Average.ElMap newValue = new Average.ElMap();
                        newValue.setRatings(entry.getValue());
                        newValue.setNrOfRatings(1);
                        ratingsStorage.put(entry.getKey(), newValue);
                    }
                }
            }

            for (Map.Entry<UserManager.Pair, Double> entry : showRatings.entrySet()) {
                UserManager.Pair key = entry.getKey();
                if (filmography.contains(key.getTitle())) {
                    int numberOfSeasons = 1;
                    double value = entry.getValue();
                    for (int j = 0; j < shows.size(); ++j) {
                        if (shows.get(j).getTitle().equals(key.getTitle())) {
                            numberOfSeasons = shows.get(j).getNumberSeason();
                        }
                    }

                    for (int j = 0; j <= numberOfSeasons && j != key.getSeason(); ++j) {
                        UserManager temp = new UserManager();
                        UserManager.Pair key2 = new UserManager.Pair();
                        key2.setSeason(j);
                        key2.setTitle(entry.getKey().getTitle());
                        if (showRatings.containsKey(key2)) {
                            value += showRatings.get(key2);
                            showRatings.remove(key2);
                        }
                    }

                    if (ratingsStorage.containsKey(key.getTitle())) {
                        Average.ElMap oldValue = ratingsStorage.get(key.getTitle());
                        oldValue.setRatings(oldValue.getRatings() + value);
                        /**
                         * We avoid treating the same user as a different one
                         * if he rated more seasons of the same show
                         */
                        if (lastUser != users.get(i)) {
                            oldValue.setNrOfRatings(oldValue.getNrOfRatings() + numberOfSeasons);
                            lastUser = users.get(i);
                        }
                        ratingsStorage.replace(key.getTitle(), oldValue);
                    } else {
                        Average.ElMap newValue = new Average.ElMap();
                        newValue.setNrOfRatings(numberOfSeasons);
                        newValue.setRatings(value);
                        ratingsStorage.put(key.getTitle(), newValue);
                    }
                }
            }
        }

        if (flag) {
            for (String title : filmography) {
                if (!ratingsStorage.containsKey(title)) {
                    Average.ElMap value = new Average.ElMap();
                    value.setNrOfRatings(1);
                    value.setRatings(0);
                    ratingsStorage.put(title, value);
                }
            }
        }

        return ratingsStorage;
    }
}
