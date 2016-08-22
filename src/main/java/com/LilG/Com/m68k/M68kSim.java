package com.LilG.Com.m68k;

import java.io.ByteArrayOutputStream;

/**
 * Created by ggonz on 8/20/2016.
 */
public interface M68kSim {
    static M68kSim getInstance() {
        return M68kSimImpl.instance;
    }


    static void setNull() {
        M68kSimImpl.instance = null;
    }

    void adda(Size size, long data, int ea);

    void move(Size size, long data, int ea);

    short getByte(int address);

    int getWord(int address);

    long getLongWord(int address);

    void clearMem();

    ByteArrayOutputStream getMem();

    void memDump();

    long getRamStart();

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
