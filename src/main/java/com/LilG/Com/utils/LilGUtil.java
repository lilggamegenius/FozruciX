package com.LilG.Com.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ggonz on 8/13/2016.
 */

@SuppressWarnings("unused")
public class LilGUtil {
    private final static transient Random rand = new Random();
    private transient final static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(LilGUtil.class);

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */

    public static int randInt(int min, int max) {


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    public static String getBytes(@NotNull String bytes) {
        byte[] Bytes = bytes.getBytes();
        return Arrays.toString(Bytes);
    }

    public static String formatFileSize(long size) {
        String hrSize;

        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format((double) size).concat(" B");
        }

        return hrSize;
    }

    public static boolean isNumeric(@NotNull String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * This method guarantees that garbage collection is
     * done unlike <code>{@link System#gc()}</code>
     */
    public static int gc() {
        int timesRan = 0;
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
        //noinspection UnusedAssignment
        obj = null;
        while (ref.get() != null) {
            System.gc();
            timesRan++;
        }
        return timesRan;
    }

    public static Unsafe getUnsafe() throws SecurityException {
        return Unsafe.getUnsafe();
    }

    @NotNull
    public static String[] splitMessage(String stringToSplit) {
        return splitMessage(stringToSplit, 0);
    }

    @NotNull
    public static String[] splitMessage(String stringToSplit, int amountToSplit) {
        return splitMessage(stringToSplit, amountToSplit, true);
    }

    @NotNull
    public static String[] splitMessage(@Nullable String stringToSplit, int amountToSplit, boolean removeQuotes) {
        if (stringToSplit == null)
            return new String[0];

        LinkedList<String> list = new LinkedList<>();
        Matcher argSep = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(stringToSplit);
        while (argSep.find())
            list.add(argSep.group(1));

        if (removeQuotes) {
            if (amountToSplit != 0) {
                for (int i = 0; list.size() > i; i++) { // go through all of the
                    list.set(i, list.get(i).replaceAll("\"", "")); // remove quotes left in the string
                    list.set(i, list.get(i).replaceAll("''", "\"")); // replace double ' to quotes
                    // go to next string
                }
            } else {
                for (int i = 0; list.size() > i || amountToSplit > i; i++) { // go through all of the
                    list.set(i, list.get(i).replaceAll("\"", "")); // remove quotes left in the string
                    list.set(i, list.get(i).replaceAll("''", "\"")); // replace double ' to quotes
                    // go to next string
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static boolean containsAny(@NotNull String check, @NotNull String... contain) {
        for (String aContain : contain) {
            if (check.contains(aContain)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equalsAny(@NotNull String check, @NotNull String... equal) {
        for (String aEqual : equal) {
            if (check.contains(aEqual)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs a wildcard matching for the text and pattern
     * provided.
     *
     * @param text    the text to be tested for matches.
     * @param pattern the pattern to be matched for.
     *                This can contain the wildcard character '*' (asterisk).
     * @return <tt>true</tt> if a match is found, <tt>false</tt>
     * otherwise.
     */
    public static boolean wildCardMatch(@NotNull String text, @NotNull String pattern) {
        // Create the cards by splitting using a RegEx. If more speed
        // is desired, a simpler character based splitting can be done.
        String[] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards) {
            int idx = text.indexOf(card);

            // Card not detected in the text.
            if (idx == -1) {
                return false;
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }

        return true;
    }

    public static boolean matchHostMask(@NotNull String hostmask, @NotNull String pattern) {
        String nick = hostmask.substring(0, hostmask.indexOf("!"));
        String userName = hostmask.substring(hostmask.indexOf("!") + 1, hostmask.indexOf("@"));
        String hostname = hostmask.substring(hostmask.indexOf("@") + 1);

        String patternNick = pattern.substring(0, pattern.indexOf("!"));
        String patternUserName = pattern.substring(pattern.indexOf("!") + 1, pattern.indexOf("@"));
        String patternHostname = pattern.substring(pattern.indexOf("@") + 1);
        if (wildCardMatch(nick, patternNick)) {
            if (wildCardMatch(userName, patternUserName)) {
                if (wildCardMatch(hostname, patternHostname)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void pause(int time) throws InterruptedException {
        LOGGER.debug("Sleeping for " + time + " seconds");
        Thread.sleep(time * 1000);
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(search)) {
                return each;
            }
        }
        return null;
    }

    public static void removeDuplicates(LinkedList<String> list) {
        LinkedList<String> ar = new LinkedList<>();
        while (list.size() > 0) {
            ar.add(list.get(0));
            list.removeAll(Collections.singleton(list.get(0)));
        }
        list.addAll(ar);
    }
}