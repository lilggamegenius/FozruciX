using System.Windows.Forms;
using com.jcraft.jsch;

namespace FozruciCS.Console {
	public class CommandLine {
		public class MyUserInfo : UserInfo {
			public bool promptYesNo(string message) {
				return true;
			}

			public void showMessage(string str){ MessageBox.Show(str); }
			public string getPassphrase(){ throw new System.NotImplementedException(); }
			public string getPassword(){ throw new System.NotImplementedException(); }
			public bool promptPassword(string str){ throw new System.NotImplementedException(); }
			public bool promptPassphrase(string str){ throw new System.NotImplementedException(); }
		}
	}
}