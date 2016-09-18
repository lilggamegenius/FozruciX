package s1tcg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author soniex2
 */
public class S1TCG {

    private static HashMap<Character, Letter> letterMap;

    static {
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
        letterMap = new HashMap<>();
        for (Letter l : letters) {
            letterMap.put(l.letter, l);
        }
    }

    private static String make_titlecard_start(String name, int count, String description) {
        String strcount = String.valueOf(count & 255);
        String pad = strcount.length() < 3 ? strcount.length() < 2 ? "  " : " " : "";
        return name + ":\tdc.b " + strcount + pad + "\t\t; " + description;
    }

    private static String make_titlecard_line(Letter l, int xpos, int ypos) {
        String vpos = "$" + Integer.toString(ypos & 255, 16);
        String ss = String.valueOf(l.spriteSize()); //s1tcg_sprite_size(width, height));
        String spi = "$" + Integer.toString(l.sprite & 255, 16);
        String hpos = "$" + Integer.toString(xpos & 255, 16);
        if (vpos.length() < 3) vpos = " " + vpos;
        if (spi.length() < 3) spi = " " + spi;
        if (hpos.length() < 3) hpos = " " + hpos;
        return "\t\tdc.b " + vpos + ", " + ss + ", 0, " + spi + ", " + hpos + "\t; " + l.letter;
    }

    private static String make_titlecard_end() {
        return "\t\teven";
    }

    private static ArrayList<String> make_titlecard(String name, String text, byte xpos, byte ypos) {
        ArrayList<Letter> letters = new ArrayList<>();
        int sprites = 0;
        for (char c : text.toCharArray()) {
            if (letterMap.containsKey(c)) {
                letters.add(letterMap.get(c));
                sprites++;
            } else {
                letters.add(null);
            }
        }
        ArrayList<String> out = new ArrayList<>();
        if (name != null) {
            out.add(make_titlecard_start(name, sprites, name + " | " + text));
        }
        for (Letter l : letters) {
            if (l != null) {
                out.add(make_titlecard_line(l, xpos, ypos));
                xpos += l.pixelWidth;
            } else {
                out.add("\t\t; Space");
                xpos += 16;
            }
        }
        out.add(make_titlecard_end());
        return out;
    }

    public static List<String> process(String[] args) {
        byte x = (byte) 0xD4; // TODO algorithmically find out
        byte y = (byte) 0xF8;
        String label = null;
        int i = 0;
        for (; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                if (arg.equals("--")) {
                    i++;
                    break;
                }
                if (arg.equals("-x") || arg.equals("--x")) {
                    i++; // skip next argument
                    if (i > args.length) throw new IllegalArgumentException("no x pos for -x");
                    x = (byte) Integer.parseInt(args[i]);
                } else if (arg.equals("-y") || arg.equals("--y")) {
                    i++; // skip next argument
                    if (i > args.length) throw new IllegalArgumentException("no y pos for -y");
                    y = (byte) Integer.parseInt(args[i]);
                } else if (arg.equals("-j") || arg.equals("--json")) {
                    i++; // skip next argument
                    if (i > args.length) throw new IllegalArgumentException("no JSON URL for --json");
                    throw new UnsupportedOperationException("not implemented (--json)");
                } else if (arg.equals("-l") || arg.equals("--label")) {
                    i++; // skip next argument
                    if (i > args.length) throw new IllegalArgumentException("No label for --label");
                    label = args[i];
                }
            } else {
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int start = i; i < args.length; i++) {
            if (i != start) sb.append(" ");
            sb.append(args[i]);
        }
        return make_titlecard(label, sb.toString(), x, y);
    }

    private static class Letter {
        public final int width;
        public final int height;
        public final int pixelWidth;
        public final int pixelHeight;
        public final int sprite;
        public final int spriteCount;
        public final char letter;

        public Letter(int sprite_index, int width, int height, char l) {
            // genesis-corrected sprite size
            int proper_width = (width / 8) & 3;
            int proper_height = (height / 8) & 3;
            this.width = proper_width - 1;
            this.height = proper_height - 1;
            this.pixelWidth = proper_width * 8;
            this.pixelHeight = proper_height * 8;
            this.sprite = sprite_index;
            this.spriteCount = proper_width * proper_height;
            this.letter = l;
        }

        public int spriteSize() {
            return ((width & 3) << 2) | (height & 3);
        }
    }
}
