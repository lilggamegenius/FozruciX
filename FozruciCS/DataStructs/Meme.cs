namespace FozruciCS.DataStructs{
	public struct Meme{
		private readonly string creator;
		private string meme;

		public Meme(string creator, string Meme) {
			this.creator = creator;
			meme = Meme;
		}

		public string getCreator() {
			return creator;
		}

		public string getMeme() {
			return meme;
		}

		public void setMeme(string meme) {
			this.meme = meme;
		}

		public string toString() {
			return "Creator: " + creator + " - " + meme;
		}
	}
}