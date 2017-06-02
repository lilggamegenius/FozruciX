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
		private readonly List<string> _output;

		public CMD(MessageEvent @event, string[] strings){
			Event = @event;
			_output = new List<string>();
			Process = new Process{
				StartInfo = {
					RedirectStandardOutput = true,
					RedirectStandardError = true,
					CreateNoWindow = true,
					Arguments = (LilGUtil.IsLinux ? "-c " : "/c ") + string.Join(" ", strings),
					FileName = LilGUtil.IsLinux ? "/bin/bash" : "cmd.exe"
				}
			};
			Process.OutputDataReceived += (s, e) => {
				_output.Add(e.Data);
			};
		}

		public void Start(){
			using(Process){
				Process.Start();
				Process.BeginOutputReadLine();
				(_thread = new Thread(SendCommand)).Start();
			}
		}

		public void Interrupt(){
			if(!Process.HasExited){
				Process.Kill();
			}
			_thread.Interrupt();
		}

		private void SendCommand(){
			try{
				while(!Process.HasExited || _thread.IsAlive){
					if(_output.Count != 0){
						Event.respondWith(_output[0]);
						_output.RemoveAt(0);
					}
					Thread.Sleep(1000);
				}
			}catch(ThreadInterruptedException e){
				System.Console.WriteLine("CMD thread interrupted" + e);
			}
		}
	}
}
