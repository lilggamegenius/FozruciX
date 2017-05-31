using System.Diagnostics;
using FozruciCS.Utils;
using org.pircbotx.hooks.events;

namespace FozruciCS.Console {
	public class CMD {
		public Process Process{ get; }
		public MessageEvent Event;
		//public StringStream
		
		public CMD(MessageEvent @event, string[] strings){
			Event = @event;
			Process = new Process{
				StartInfo = {
					RedirectStandardOutput = true,
					RedirectStandardError = true,
					CreateNoWindow = true,
					Arguments = string.Join(" ", strings)
				}
				
			};
			Process.OutputDataReceived += (s, e) => 
			{ 
				//Console.WriteLine(e.Data); 
			};
			Process.StartInfo.FileName = !LilGUtil.IsLinux ? "/bin/bash" : "cmd.exe";
		}

		public void Start(){ Process.Start(); }

		public void Interrupt(){
			if(!Process.HasExited){
				Process.Kill();
			}
		}
	}
}