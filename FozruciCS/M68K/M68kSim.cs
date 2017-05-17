using System;
using System.ComponentModel;

namespace FozruciCS.M68K{
	public interface IM68KSim{
		void start();

		void exit(); void exit(object obj, EventArgs eventArgs);

		void setByte(short address, byte num);

		void setWord(short address, short num);

		void setLongWord(short address, long num);

		void addByte(short address, byte num);

		void addWord(short address, short num);

		void addLongWord(short address, long num);

		byte getByte(short address);

		short getWord(short address);

		long getLongWord(short address);

		void clearMem();

		long getRamStart();

		short getRamSize();

		void lea(short address, int an);

		void pea(short address);

		void add(int size, int dn, short ea);

		void add(int size, short ea, int dn);

		void adda(Size size, short ea, int an);

		void addi(int size, short ea, long data);

		void and(int size, int dn, short ea);

		void and(int size, short ea, int dn);

		void move(int size, short source, short destination);

		void move(int size, short source, int dn);

		void move(int size, int dn, short destination);

		void move(int size, int dn1, int dn2);

		void moveq(byte data, short destination);

		void memDump();
	}

	public enum DataRegister{
		D0,
		D1,
		D2,
		D3,
		D4,
		D5,
		D6,
		D7
	}

	public enum AddressRegister{
		A0,
		A1,
		A2,
		A3,
		A4,
		A5,
		A6,
		A7
	}

	public enum Size {
		[Description("b")] Byte,
		[Description("w")] Word,
		[Description("l")] LongWord
    }


}