
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ggonz on 11/25/2015.
 */
public class SaveDataStore {
    List<Note> noteList = new ArrayList<>();

    List<String> authedUser = new ArrayList<>();
    List<Integer> authedUserLevel = new ArrayList<>();

    List<String> DNDJoined = new ArrayList<>();
    List<DNDPlayer> DNDList = new ArrayList<>();

    public SaveDataStore() {
    }

    public SaveDataStore(List<Note> noteList, List<String> authedUser, List<Integer> authedUserLevel, List<String> DNDJoined, List<DNDPlayer> DNDList) {
        this.noteList = noteList;
        this.authedUser = authedUser;
        this.authedUserLevel = authedUserLevel;
        this.DNDJoined = DNDJoined;
        this.DNDList = DNDList;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public List<String> getAuthedUser() {
        return authedUser;
    }

    public List<Integer> getAuthedUserLevel() {
        return authedUserLevel;
    }

    public List<String> getDNDJoined() {
        return DNDJoined;
    }

    public List<DNDPlayer> getDNDList() {
        return DNDList;
    }
}
