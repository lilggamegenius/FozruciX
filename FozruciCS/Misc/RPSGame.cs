using ikvm.extensions;

namespace FozruciCS.Misc{
	public struct RpsGame{
		private readonly string _player1;
		private readonly string _player2;

		private int _choice1;
		private int _choice2;

		public RpsGame(string player1, string player2) {
			_player1 = player1;
			_player2 = player2;
			_choice1 = 0;
			_choice2 = 0;
		}

		public bool isInGame(string playerToCheck) {
			return playerToCheck.equalsIgnoreCase(_player1) || playerToCheck.equalsIgnoreCase(_player2);
		}

		public bool isFirstPlayer(string playerToCheck) {
			return playerToCheck.equalsIgnoreCase(_player1);
		}

		public void setP1Choice(int choice) {
			_choice1 = choice;
		}

		public void setP2Choice(int choice) {
			_choice2 = choice;
		}

		public bool isGameSet() {
			return (_choice1 != 0) && (_choice2 != 0);
		}
	}
}