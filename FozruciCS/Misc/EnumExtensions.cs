using System.ComponentModel;
using FozruciCS.M68K;

namespace FozruciCS.Misc{
	public static class EnumExtensions{
		public static string toString(this EventType eventType){
			var attributes = (DescriptionAttribute[])eventType
				.GetType()
				.GetField(eventType.ToString())
				.GetCustomAttributes(typeof(DescriptionAttribute), false);
			return attributes.Length > 0 ? attributes[0].Description : string.Empty;
		}

		public static char getSymbol(this Size size){
			var attributes = (DescriptionAttribute[])size.GetType()
			                                             .GetField(size.ToString())
			                                             .GetCustomAttributes(typeof(DescriptionAttribute), false);
			return attributes.Length > 0 ? attributes[0].Description[0] : '\0';
		}

		public static byte getSize(this Size size){
			switch(size){
			case Size.Byte: return 8;
			case Size.Word: return 16;
			case Size.LongWord: return 32;
			default: return 0;
			}
		}
	}
}