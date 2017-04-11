package com.LilG.utils

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import sun.misc.Unsafe
import java.lang.management.ManagementFactory
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import javax.management.Attribute
import javax.management.ObjectName

/**
 * Created by ggonz on 8/13/2016.
 * Utility class - contains misc functions
 */

object LilGUtil {
    private val rand = Random()
    private val LOGGER = LoggerFactory.getLogger(LilGUtil::class.java) as Logger

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * `Integer.MAX_VALUE - 1`.

     * @param min Minimum value
     * *
     * @param max Maximum value.  Must be greater than min.
     * *
     * @return Integer between min and max, inclusive.
     * *
     * @see java.util.Random.nextInt
     */

    fun randInt(min: Int, max: Int): Int {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt(max - min + 1) + min
    }

    fun randDec(min: Double, max: Double): Double {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextFloat() * (max - min) + min
    }

    fun getBytes(bytes: String): String {
        val Bytes = bytes.toByteArray()
        return Arrays.toString(Bytes)
    }

    fun formatFileSize(size: Long): String {
        val hrSize: String

        val k = size / 1024.0
        val m = size / 1024.0 / 1024.0
        val g = size / 1024.0 / 1024.0 / 1024.0
        val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0

        val dec = DecimalFormat("0.00")

        if (t > 1) {
            hrSize = dec.format(t) + " TB"
        } else if (g > 1) {
            hrSize = dec.format(g) + " GB"
        } else if (m > 1) {
            hrSize = dec.format(m) + " MB"
        } else if (k > 1) {
            hrSize = dec.format(k) + " KB"
        } else {
            hrSize = dec.format(size.toDouble()) + " B"
        }

        return hrSize
    }

    fun isNumeric(str: String): Boolean {
        return str.matches("-?\\d+(\\.\\d+)?".toRegex())  //match a number with optional '-' and decimal.
    }

    /**
     * This method guarantees that garbage collection is
     * done unlike `[System.gc]`
     */
    fun gc(): Int {
        var timesRan = 0
        var obj: Any? = Any()
        val ref = WeakReference<Any>(obj)

        obj = null
        while (ref.get() != null) {
            System.gc()
            timesRan++
        }
        LOGGER.info("Took $timesRan attempt(s) to run GC")
        return timesRan
    }

    val unsafe: Unsafe?
        get() {
            try {
                val f = Unsafe::class.java.getDeclaredField("theUnsafe")
                f.isAccessible = true
                return f.get(null) as Unsafe
            } catch (e: NoSuchFieldException) {
                LOGGER.error("Error getting unsafe class", e)
            } catch (e: IllegalAccessException) {
                LOGGER.error("Error getting unsafe class", e)
            }

            return null
        }

    fun sizeOf(o: Any): Long {
        val u = unsafe!!
        val fields = HashSet<Field>()
        var c: Class<*> = o.javaClass
        while (c != Any::class.java) {
            c.declaredFields.filterTo(fields) { it.modifiers and Modifier.STATIC == 0 }
            c = c.superclass
        }

        // get offset
        val maxSize: Long = fields
                .map { u.objectFieldOffset(it) }
                .max()
                ?: 0

        return (maxSize / 8 + 1) * 8   // padding
    }

    @JvmOverloads fun splitMessage(stringToSplit: String?, amountToSplit: Int = 0, removeQuotes: Boolean = true): Array<String?> {
        if (stringToSplit == null)
            return arrayOfNulls(0)

        val list = LinkedList<String>()
        val argSep = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(stringToSplit)
        while (argSep.find())
            list.add(argSep.group(1))

        if (removeQuotes) {
            if (amountToSplit != 0) {
                var i = 0
                while (list.size > i) { // go through all of the
                    list[i] = list[i].replace("\"".toRegex(), "") // remove quotes left in the string
                    list[i] = list[i].replace("''".toRegex(), "\"") // replace double ' to quotes
                    i++
                    // go to next string
                }
            } else {
                var i = 0
                while (list.size > i || amountToSplit > i) { // go through all of the
                    list[i] = list[i].replace("\"".toRegex(), "") // remove quotes left in the string
                    list[i] = list[i].replace("''".toRegex(), "\"") // replace double ' to quotes
                    i++
                    // go to next string
                }
            }
        }
        return list.toTypedArray()
    }

    fun containsAny(check: String, vararg contain: String): Boolean {
        for (aContain in contain) {
            if (check.contains(aContain)) {
                return true
            }
        }
        return false
    }

    fun equalsAny(check: String, vararg equal: String): Boolean {
        for (aEqual in equal) {
            if (check == aEqual) {
                return true
            }
        }
        return false
    }

    fun equalsAnyIgnoreCase(check: String, vararg equal: String): Boolean {
        for (aEqual in equal) {
            if (check.equals(aEqual, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun containsAnyIgnoreCase(check: String, vararg equal: String): Boolean {
        for (aEqual in equal) {
            if (check.toLowerCase().contains(aEqual.toLowerCase())) {
                return true
            }
        }
        return false
    }

    fun startsWithAny(check: String, vararg equal: String): Boolean {
        for (aEqual in equal) {
            if (check.startsWith(aEqual)) {
                return true
            }
        }
        return false
    }

    fun endsWithAny(check: String, vararg equal: String): Boolean {
        for (aEqual in equal) {
            if (check.endsWith(aEqual)) {
                return true
            }
        }
        return false
    }

    /**
     * Performs a wildcard matching for the text and pattern
     * provided.

     * @param text    the text to be tested for matches.
     * *
     * @param pattern the pattern to be matched for.
     * *                This can contain the wildcard character '*' (asterisk).
     * *
     * @return <tt>true</tt> if a match is found, <tt>false</tt>
     * * otherwise.
     */
    fun wildCardMatch(text: String, pattern: String): Boolean {
        var text = text
        // Create the cards by splitting using a RegEx. If more speed
        // is desired, a simpler character based splitting can be done.
        val cards = pattern.split("\\*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // Iterate over the cards.
        for (card in cards) {
            val idx = text.indexOf(card)

            // Card not detected in the text.
            if (idx == -1) {
                return false
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length)
        }

        return true
    }

    fun matchHostMask(hostmask: String, pattern: String): Boolean {
        val nick = hostmask.substring(0, hostmask.indexOf("!"))
        val userName = hostmask.substring(hostmask.indexOf("!") + 1, hostmask.indexOf("@"))
        val hostname = hostmask.substring(hostmask.indexOf("@") + 1)

        val patternNick = pattern.substring(0, pattern.indexOf("!"))
        val patternUserName = pattern.substring(pattern.indexOf("!") + 1, pattern.indexOf("@"))
        val patternHostname = pattern.substring(pattern.indexOf("@") + 1)
        if (wildCardMatch(nick, patternNick)) {
            if (wildCardMatch(userName, patternUserName)) {
                if (wildCardMatch(hostname, patternHostname)) {
                    return true
                }
            }
        }
        return false
    }

    @Throws(InterruptedException::class)
    @JvmOverloads fun pause(time: Int, echoTime: Boolean = true) {
        if (echoTime) {
            LOGGER.debug("Sleeping for $time seconds")
        }
        Thread.sleep((time * 1000).toLong())
    }

    fun <T : Enum<*>> searchEnum(enumeration: Class<T>, search: String): T? {
        for (each in enumeration.enumConstants) {
            if (each.name.equals(search, ignoreCase = true)) {
                return each
            }
        }
        return null
    }

    fun removeDuplicates(list: LinkedList<String>) {
        val ar = LinkedList<String>()
        while (list.size > 0) {
            ar.add(list[0])
            list.removeAll(setOf(list[0]))
        }
        list.addAll(ar)
    }

    @Throws(ClassCastException::class)
    fun <T, S> cast(type: T, cast: Class<S>): S {
        return cast.cast(type)
    }

    fun hash(string: String, maxNum: Int): Int {
        var hash = 0
        for (i in 0..string.length - 1) {
            val charCode = string[i].toInt()
            hash += charCode
        }
        return hash % maxNum
    }

    // usually takes a couple of seconds before we get real values
    // returns a percentage value with 1 decimal point precision
    val processCpuLoad: Double
        @Throws(Exception::class)
        get() {

            val mbs = ManagementFactory.getPlatformMBeanServer()
            val name = ObjectName.getInstance("java.lang:type=OperatingSystem")
            val list = mbs.getAttributes(name, arrayOf("ProcessCpuLoad"))

            if (list.isEmpty()) return java.lang.Double.NaN

            val att = list[0] as Attribute
            val value = att.value as Double
            if (value === -1.0) return java.lang.Double.NaN
            return (value * 1000).toInt() / 10.0
        }
}
