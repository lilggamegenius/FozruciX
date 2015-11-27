import de.tudarmstadt.ukp.jwktl.JWKTL;

import java.io.File;

/**
 * Created by ggonz on 11/27/2015.
 */
public class JWKTLParser {
    static final String PATH_TO_DUMP_FILE = "Database.xml";
    static final String TARGET_DIRECTORY = "Data\\Wiktionary";
    static final boolean OVERWRITE_EXISTING_FILES = true;
    
    public static void main(String[] args) throws Exception {
        File dumpFile = new File(PATH_TO_DUMP_FILE);
        File outputDirectory = new File(TARGET_DIRECTORY);
        boolean overwriteExisting = OVERWRITE_EXISTING_FILES;

        JWKTL.parseWiktionaryDump(dumpFile, outputDirectory, overwriteExisting);
    }
}
