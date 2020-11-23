package user;

import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.UserInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import query.QueryManager;
import video.VideoManager;

import java.util.ArrayList;
import java.util.List;

public class Handler {
    private List<ActorInputData> actors;
    private List<UserInputData> usersAsInput = new ArrayList<>();
    private List<ActionInputData> actions;
    private List<MovieInputData> movies;
    private List<SerialInputData> serials;
    private Input input = new Input();
    protected List<UserManager> users = new ArrayList<UserManager>();
    protected VideoManager video;
    protected QueryManager query;

    public Handler(final Input input, final Input input2) {
        this.input = input2;
        this.actors = input.getActors();
        this.usersAsInput = input.getUsers();
        this.actions = input.getCommands();
        this.serials = input.getSerials();
        this.movies = input.getMovies();
        this.video = new VideoManager();
        this.query = new QueryManager();
    }

    public Handler() {
    }

    /**
     * Creating users
     */
    public void createUserInstances() {
        for (UserInputData user : usersAsInput) {
            users.add(new UserManager(user));
        }
    }

    /**
     * We treat each case separately, every function that's called
     * returns a JSONObject with the answer and then it's returned
     * by this function as well
     * @param action - the input action
     * @return JSONObject with the answer
     */
    public JSONObject createJsonElement(final ActionInputData action) {
        JSONObject jo = new JSONObject();
        UserManager user = new UserManager();

        switch (action.getActionType()) {
            case "command":
                switch (action.getType()) {
                    case "favorite":
                        jo = user.favorite(users, action);
                        break;
                    case "view":
                        jo = user.view(users, action);
                        break;
                    case "rating":
                        jo = user.rating(users, action);
                        break;
                    default:
                        break;
                }
                break;
            case "recommendation":
                switch (action.getType()) {
                    case "favorite":
                        jo = video.favorite(input.getUsers(), action, movies, serials);
                        break;
                    case "standard":
                        jo = video.standard(users, action, movies);
                        break;
                    case "best_unseen":
                        jo = video.bestUnseen(users, action, movies);
                        break;
                    case "popular":
                        jo = video.popular(users, action, movies, serials);
                        break;
                    case "search":
                        jo = video.search(users, action, movies, serials);
                        break;
                    default:
                        break;
                }
                break;
            case "query":
                if ("actors".equals(action.getObjectType())) {
                    switch (action.getCriteria()) {
                        case "average":
                            jo = query.average(actors, users, action, serials);
                            break;
                        case "awards":
                            jo = query.awards(actors, action);
                            break;
                        case "filter_description":
                            jo = query.filterDescription(actors, action);
                            break;
                        default:
                            break;
                    }
                }
                /**
                 * Videos + Users:
                 */
                switch (action.getCriteria()) {
                    case "ratings":
                        jo = query.rating(users, action, movies, serials);
                        break;
                    case "favorite":
                        jo = query.favorite(users, action, movies, serials);
                        break;
                    case "most_viewed":
                        jo = query.mostViewed(users, action, movies, serials);
                        break;
                    case "num_ratings":
                        jo = query.nrOfRatings(users, action);
                        break;
                    case "longest":
                        jo = query.longest(action, movies, serials);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return jo;
    }

    /**
     * First, we create every instance of user, and then
     * we compute the answer for each action one by one
     * @param result - the initial array with the answers from actions
     *               (empty)
     * @return The final answer
     */
    public JSONArray handler(final JSONArray result) {
        createUserInstances();
        for (ActionInputData action : actions) {
            JSONObject jo;
            jo = createJsonElement(action);
            result.add(jo);
        }
        return result;
    }

    public final VideoManager getVideo() {
        return video;
    }

    public final void setVideo(final VideoManager video) {
        this.video = video;
    }
}
