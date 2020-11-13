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
    private List<UserInputData> users;
    private List<ActionInputData> actions;
    private List<MovieInputData> movies;
    private List<SerialInputData> serials;
    protected List<UserManager> Users = new ArrayList<UserManager>();

    public Handler() {}

    public Handler(final Input input) {
        this.users = input.getUsers();
        this.actions = input.getCommands();
        this.serials = input.getSerials();
    }

    /**
     * Creating users
     */
    public void createUserInstances() {
        for (UserInputData user : users) {
            Users.add(new UserManager(user));
        }
    }

    public JSONObject createJsonElement(ActionInputData action) {
        JSONObject jo = new JSONObject();
        UserManager user = new UserManager();

        switch(action.getActionType()) {
            case "command":
                switch(action.getType()) {
                    case "favorite":
                        jo = user.favorite(Users, action);
                        break;
                    case "view":
                        jo = user.view(Users, action);
                        /*for (int i = 0; i < Users.size(); ++i) {
                            if (Users.get(i).getUsername().equals("mildGelding9")) {
                                System.out.println(Users.get(i).getHistory());
                            }
                        }*/
                        break;
                }
                break;
        }

        return jo;
    }

    /**
     *
     * @param result
     * @return
     */
    public JSONArray handler(JSONArray result) {
        createUserInstances();
        for (ActionInputData action : actions) {
            JSONObject jo;
            jo = createJsonElement(action);
            result.add(jo);
        }
        return result;
    }
}
