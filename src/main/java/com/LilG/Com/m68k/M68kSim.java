package com.LilG.Com.m68k;

import ch.qos.logback.classic.Logger;
import com.LilG.Com.utils.LilGUtil;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by ggonz on 8/16/2016.
 */
public class M68kSim {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(M68kSim.class);
    private static final int ramSize = 0x10000;
    private static Unsafe unsafe = LilGUtil.getUnsafe();
    private static final long ramStart = unsafe.allocateMemory(ramSize);
    private static int[] dataRegisters = new int[8];
    private static int[] addressRegisters = new int[8];
    private static int programCounter;

    public static void adda(Size size, int data, int ea) {
        switch (size) {
            case Byte:
                addByte(ea, (byte) data);
                break;
            case Word:
                addWord(ea, (short) data);
                break;
            case LongWord:
                addLongWord(ea, data);
        }
    }

    private static byte getByte(int address) {
        return unsafe.getByte(ramStart + address);
    }

    private static short getWord(int address) {
        return unsafe.getShort(ramStart + address);
    }

    private static int getLongWord(int address) {
        return unsafe.getInt(ramStart + address);
    }

    private static void setByte(int address, byte num) {
        unsafe.putByte(ramStart + address, num);
    }

    private static void setWord(int address, short num) {
        unsafe.putShort(ramStart + address, num);
    }

    private static void setLongWord(int address, int num) {
        unsafe.putInt(ramStart + address, num);
    }

    private static void addByte(int address, byte num) {
        num += getByte(address);
        setByte(address, num);
    }

    private static void addWord(int address, short num) {
        num += getWord(address);
        setWord(address, num);
    }

    private static void addLongWord(int address, int num) {
        num += getLongWord(address);
        setLongWord(address, num);
    }

    public static synchronized void clearMem() {
        long time = System.currentTimeMillis();
        for (int address = 0; address < ramSize; address += 8) {
            unsafe.putLong(address + ramStart, 0L);
        }
        LOGGER.info("Took " + (System.currentTimeMillis() - time) + " ms to clear M68k memory");
    }

    public static ByteArrayOutputStream getMem() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(ramSize);
        for (int address = 0; address <= ramSize; address++) {
            bytes.write(getByte(address));
        }
        return bytes;
    }

    public static void memDump() {
        File memDumpFile = new File("data/M68kDump.bin");
        try (FileWriter writer = new FileWriter(memDumpFile)) {
            LOGGER.info("Starting mem dump");
            StringBuilder sb = new StringBuilder();
            for (int address = 0; address <= ramSize; address++) {
                sb.append(String.format("%02X ", getByte(address)));
                if (address != 0 && address % 16 == 0) {
                    sb.append('\n');
                }
            }
            writer.write(sb.toString());
            LOGGER.info("finished dump");
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

    public static long getRamStart() {
        return ramStart;
    }

    public enum Size {
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

    private enum DataRegister {
        d0, d1, d2, d3, d4, d5, d6, d7
    }

    private enum AddressRegister {
        a0, a1, a2, a3, a4, a5, a6, a7
    }
}
