package com.LilG.Com.m68k;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;

/**
 * Created by ggonz on 8/16/2016.
 */
public interface M68kSim extends Library {

    void start();

    void exit();

    void setByte(short address, byte num);

    void setWord(short address, short num);

    void setLongWord(short address, NativeLong num);

    void addByte(short address, byte num);

    void addWord(short address, short num);

    void addLongWord(short address, NativeLong num);

    byte getByte(short address);

    short getWord(short address);

    NativeLong getLongWord(short address);

    void clearMem();

    long getRamStart();

    short getRamSize();

    void lea(short address, int An);

    void pea(short address);

    void add(int size, int dn, short ea);

    void add(int size, short ea, int dn);

    void adda(Size size, short ea, int an);

    void addi(int size, short ea, NativeLong data);

    void and(int size, int dn, short ea);

    void and(int size, short ea, int dn);

    void move(int size, short source, short destination);

    void move(int size, short source, int dn);

    void move(int size, int dn, short destination);

    void move(int size, int dn1, int dn2);

    void moveq(byte data, short destination);

    void memDump();

    enum Size {
        Byte((byte) 8, 'b'), Word((byte) 16, 'w'), LongWord((byte) 32, 'l');

        final byte size;
        final char symbol;

        Size(byte size, char symbol) {
            this.size = size;
            this.symbol = symbol;
        }

        public byte getSize() {
            return size;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    enum DataRegister {
        d0, d1, d2, d3, d4, d5, d6, d7
    }

    enum AddressRegister {
        a0, a1, a2, a3, a4, a5, a6, a7
    }
}
