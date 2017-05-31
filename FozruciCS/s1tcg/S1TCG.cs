using System;
using System.Collections.Generic;
using java.lang;
using StringBuilder = System.Text.StringBuilder;

namespace FozruciCS.s1tcg {
	// ReSharper disable once InconsistentNaming
	public class S1TCG {

		private static readonly Dictionary<char, Letter> LetterMap;

		static S1TCG(){
			Letter[] letters = new Letter[]{
				new Letter(0, 16, 16, 'A'),
				new Letter(4, 16, 16, 'B'),
				new Letter(8, 16, 16, 'C'),
				new Letter(12, 16, 16, 'D'),
				new Letter(16, 16, 16, 'E'),
				new Letter(20, 16, 16, 'F'),
				new Letter(24, 16, 16, 'G'),
				new Letter(28, 16, 16, 'H'),
				new Letter(32, 8, 16, 'I'), // I is only 8x16
				// unknown: sprite index 34[+4] ($22)
				new Letter(38, 16, 16, 'L'),
				new Letter(42, 16, 16, 'M'),
				new Letter(46, 16, 16, 'N'),
				new Letter(50, 16, 16, 'O'),
				new Letter(54, 16, 16, 'P'),
				// missing Q
				new Letter(58, 16, 16, 'R'),
				new Letter(62, 16, 16, 'S'),
				new Letter(66, 16, 16, 'T'),
				// unknown: sprite number 70[+4] ($46)
				new Letter(74, 16, 16, 'Y'),
				new Letter(78, 16, 16, 'Z')
			};
			LetterMap = new Dictionary<char, Letter>();
			foreach(Letter l in letters) {
				LetterMap[l.letter] = l;
			}
		}

		private static string make_titlecard_start(string name, int count, string description) {
			string strcount = Convert.ToString(count & 255);
			string pad = strcount.Length < 3 ? strcount.Length < 2 ? "  " : " " : "";
			return name + ":\tdc.b " + strcount + pad + "\t\t; " + description;
		}

		private static string make_titlecard_line(Letter l, int xpos, int ypos) {
			var vpos = "$" + (ypos & 255).ToString("X");
			var ss = l.SpriteSize().ToString(); //s1tcg_sprite_size(width, height));
			var spi = "$" + (l.Sprite & 255).ToString("X");
			var hpos = "$" + (xpos & 255).ToString("X");
			if (vpos.Length < 3) vpos = " " + vpos;
			if (spi.Length < 3) spi = " " + spi;
			if (hpos.Length < 3) hpos = " " + hpos;
			return "\t\tdc.b " + vpos + ", " + ss + ", 0, " + spi + ", " + hpos + "\t; " + l.letter;
		}

		private static string make_titlecard_end() {
			return "\t\teven";
		}

		private static List<string> make_titlecard(string name, string text, byte xpos, byte ypos) {
			var letters = new List<Letter>();
			int sprites = 0;
			foreach(char c in text) {
				if (LetterMap.ContainsKey(c)) {
					letters.Add(LetterMap[c]);
					sprites++;
				} else {
					letters.Add(null);
				}
			}
			List<string> @out = new List<string>();
			if (name != null) {
				@out.Add(make_titlecard_start(name, sprites, name + " | " + text));
			}
			foreach(Letter l in letters) {
				if (l != null) {
					@out.Add(make_titlecard_line(l, xpos, ypos));
					xpos += (byte)l.PixelWidth;
				} else {
					@out.Add("\t\t; Space");
					xpos += 16;
				}
			}
			@out.Add(make_titlecard_end());
			return @out;
		}

		public static List<string> Process(string[] args) {
			byte x = 0xD4; // TODO algorithmically find out
			byte y = 0xF8;
			string label = null;
			var i = 0;
			for (; i < args.Length; i++) {
				string arg = args[i];
				if (arg.StartsWith("-")) {
					if (arg == ("--")) {
						i++;
						break;
					}
					if (arg == ("-x") || arg == ("--x")) {
						i++; // skip next argument
						if (i > args.Length) throw new ArgumentException("no x pos for -x");
						x = (byte) Convert.ToByte(args[i]);
					} else if (arg == ("-y") || arg == ("--y")) {
						i++; // skip next argument
						if (i > args.Length) throw new ArgumentException("no y pos for -y");
						y = (byte) Convert.ToByte(args[i]);
					} else if (arg == ("-j") || arg == ("--json")) {
						i++; // skip next argument
						if (i > args.Length) throw new ArgumentException("no JSON URL for --json");
						throw new UnsupportedOperationException("not implemented (--json)");
					} else if (arg == ("-l") || arg == ("--label")) {
						i++; // skip next argument
						if (i > args.Length) throw new ArgumentException("No label for --label");
						label = args[i];
					}
				} else {
					break;
				}
			}
			StringBuilder sb = new StringBuilder();
			for (int start = i; i < args.Length; i++) {
				if (i != start) sb.Append(" ");
				sb.Append(args[i]);
			}
			return make_titlecard(label, sb.ToString(), x, y);
		}

		private class Letter {
			public readonly int Width;
			public readonly int Height;
			public readonly int PixelWidth;
			public readonly int PixelHeight;
			public readonly int Sprite;
			public readonly int SpriteCount;
			public readonly char letter;

			public Letter(int spriteIndex, int width, int height, char l) {
				// genesis-corrected sprite size
				int properWidth = (width / 8) & 3;
				int properHeight = (height / 8) & 3;
				this.Width = properWidth - 1;
				this.Height = properHeight - 1;
				this.PixelWidth = properWidth * 8;
				this.PixelHeight = properHeight * 8;
				this.Sprite = spriteIndex;
				this.SpriteCount = properWidth * properHeight;
				this.letter = l;
			}

			public int SpriteSize() {
				return ((Width & 3) << 2) | (Height & 3);
			}
		}
	}

}