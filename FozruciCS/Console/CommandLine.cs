using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using System.Windows.Forms;
using com.jcraft.jsch;
using NLog;
using Logger = NLog.Logger;
using org.pircbotx.hooks.events;

namespace FozruciCS.Console {
	public class CommandLine {
		public Process Process{ get; }
		public MessageEvent Event;

		private Thread _thread;
		private readonly List<string> _output;
		private static readonly Logger Logger = new LogFactory().GetCurrentClassLogger();

		public CommandLine(MessageEvent @event, params string[] commandLine){
			Event = @event;
			_output = new List<string>();
			Process = new Process{
				StartInfo = {
					RedirectStandardInput = true,
					RedirectStandardOutput = true,
					RedirectStandardError = true,
					CreateNoWindow = true
				}
			};
			Process.StandardInput.AutoFlush = true;
			Process.OutputDataReceived += (ignored, args) => {
				Logger.Trace("CL: {0}", args.Data);
				_output.Add(args.Data);
			};
		}

		public CommandLine(){ _output = new List<string>();
			Process = new Process{
				StartInfo = {
					RedirectStandardInput = true,
					RedirectStandardOutput = true,
					RedirectStandardError = true,
					UseShellExecute = false,
					CreateNoWindow = true,
					FileName = "CMD.exe"
				}
			};
			//Process.StandardInput.AutoFlush = true;
			Process.OutputDataReceived += (ignored, args) => {
				Logger.Trace("CL: {0}", args.Data);
				_output.Add(args.Data);
			};
			Process.Start();
			Process.BeginOutputReadLine();
		}

		public void Start(){
			Process.Start();
			Process.BeginOutputReadLine();
			(_thread = new Thread(SendCommand)).Start();
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
				System.Console.WriteLine("CommandLine thread interrupted" + e);
			}
		}

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

		public bool isAlive(){ return !Process.HasExited; }

		public void doCommand(MessageEvent @event, string command){
			Event = @event;
			Process.StandardInput.WriteLineAsync(command).Start();
		}
	}
}
