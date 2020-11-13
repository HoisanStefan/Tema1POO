package user;

import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.UserInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Handler {
    private List<ActorInputData> actors;
    private List<UserInputData> usersAsInput;
    private List<ActionInputData> actions;
    private List<MovieInputData> movies;
    private List<SerialInputData> serials;
    protected List<UserManager> users = new ArrayList<UserManager>();

    public Handler() { }

    public Handler(final Input input) {
        this.usersAsInput = input.getUsers();
        this.actions = input.getCommands();
        this.serials = input.getSerials();
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
                        /*for (int i = 0; i < users.size(); ++i) {
                            if (users.get(i).getUsername().equals("mildGelding9")) {
                                System.out.println(users.get(i).getHistory());
                            }
                        }*/
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
}
