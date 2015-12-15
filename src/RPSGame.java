/**
 * Created by ggonz on 12/13/2015.
 */
public class RPSGame{
	String player1;
	String player2;

	int choice1 = 0;
	int choice2 = 0;

	public RPSGame(String player1, String player2){
		this.player1 = player1;
		this.player2 = player2;
	}

	public boolean isInGame(String playerToCheck){
		return playerToCheck.equalsIgnoreCase(player1) || playerToCheck.equalsIgnoreCase(player2);
	}

	public boolean isFirstPlayer(String playerToCheck){
		return playerToCheck.equalsIgnoreCase(player1);
	}

	public void setP1Choice(int choice){
		this.choice1 = choice;
	}

	public void setP2Choice(int choice){
		this.choice2 = choice;
	}

	public boolean isGameSet(){
		return choice1 != 0 && choice2 != 0;
	}

}
