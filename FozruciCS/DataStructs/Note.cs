using System;
using java.util;

namespace FozruciCS.DataStructs{
	public struct Note{
		public string sender{get;}
		public string receiver{get;}
		public string message{get;}
		public string date{get;}
		public string channel{get;}
		public Guid id{get;}

		public Note(string sender, string receiver, string message, string channel) {
			this.sender = sender;
			this.receiver = receiver;
			this.message = message;
			this.channel = channel;
			
			date = new Date().toString();
			id = Guid.NewGuid();
		}

		
		public string displayMessage() {
			return "\"" + message + "\". Message left by " + sender + " in " + channel + " at " + date + ". ";

		}

		public string getMessageForList() {
			return "to: " + receiver + "| Message: " + message;
		}

		
		public string getGuidForList() {
			return "to: " + receiver + "| Guid: " + id;
		}


		
		public static implicit operator string(Note n) {
			return "Sender: " + n.sender + ". Receiver: " + n.receiver + ". Date: " + n.date + ". Channel: " + n.channel + ". Message: " + n.message + ".";
		}
	}
}