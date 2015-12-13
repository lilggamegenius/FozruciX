
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ggonz on 11/25/2015.
 * Simple data class
 */
class SaveDataStore{
	private List<Note> noteList = new ArrayList<>();

	private List<String> authedUser = new ArrayList<>();
	private List<Integer> authedUserLevel = new ArrayList<>();

	private List<String> DNDJoined = new ArrayList<>();
	private List<DNDPlayer> DNDList = new ArrayList<>();


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
