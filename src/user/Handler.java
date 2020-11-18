package user;

import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.UserInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import video.VideoManager;

import java.util.ArrayList;
import java.util.List;

public class Handler {
    private List<ActorInputData> actors;
    private List<UserInputData> usersAsInput;
    private List<ActionInputData> actions;
    private List<MovieInputData> movies;
    private List<SerialInputData> serials;
    protected List<UserManager> users = new ArrayList<UserManager>();
    protected VideoManager video;

    public Handler() { }

    public Handler(final Input input) {
        this.usersAsInput = input.getUsers();
        this.actions = input.getCommands();
        this.serials = input.getSerials();
        this.movies = input.getMovies();
        this.video = new VideoManager();
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
     *
     * @param action
     * @return
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
                        jo = video.favorite(users, action);
                        break;
                    case "standard":
                        jo = video.standard(users, action, movies);
                        break;
                    case "best_unseen":
                        jo = video.bestUnseen(users, action, movies);
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
     *
     * @param result
     * @return
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
