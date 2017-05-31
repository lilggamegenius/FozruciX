using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using FozruciCS.Utils;
using org.pircbotx.hooks.events;

namespace FozruciCS.Console {
	public class CMD {
		public Process Process{ get; }
		public MessageEvent Event;
		
		private Thread _thread;
		private List<string> _output;
		
		public CMD(MessageEvent @event, string[] strings){
			Event = @event;
			_output = new List<string>();
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
				_output.Add(e.Data);
			};
			Process.StartInfo.FileName = !LilGUtil.IsLinux ? "/bin/bash" : "cmd.exe";
		}

		public void Start(){ Process.Start(); }

		public void Interrupt(){
			if(!Process.HasExited){
				Process.Kill();
			}
			_thread.Interrupt();
		}
	}
}