package com.LilG.Com.m68k;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ggonz on 8/16/2016.
 */
public class M68kSimImpl implements M68kSim {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(M68kSimImpl.class);
    static M68kSim instance = new M68kSimImpl();

    public M68kSimImpl() {
    }

    public native void adda(Size size, long data, int ea);

    public native void move(Size size, long data, int ea);

    public native short getByte(int address);

    public native int getWord(int address);

    public native long getLongWord(int address);

    @Override
    public synchronized void clearMem() {
        long time = System.currentTimeMillis();
        clearMem0();
        LOGGER.info("Took " + (System.currentTimeMillis() - time) + " ms to clear M68k memory");
    }

    public native void clearMem0();

    @Override
    public ByteArrayOutputStream getMem() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(getRamSize());
        for (int address = 0; address < getRamSize(); address++) {
            bytes.write(getByte(address));
        }
        return bytes;
    }

    @Override
    public void memDump() {
        File memDumpFile = new File("data/M68kDump.bin");
        try (FileOutputStream os = new FileOutputStream(memDumpFile)) {
            LOGGER.info("Starting mem dump");
            for (int address = 0; address < getRamSize(); address++) {
                os.write(getByte(address));
            }
            LOGGER.info("finished dump");
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

    @Override
    public native long getRamStart();

    public native int getRamSize();
}
