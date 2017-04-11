package com.LilG.utils

import de.tudarmstadt.ukp.jwktl.JWKTL

import java.io.File

/**
 * Created by ggonz on 11/27/2015.
 * Wikitionary parser
 */
internal object JWKTLParser {
    private val PATH_TO_DUMP_FILE = "C:\\Users\\ggonz\\workspace\\FozruciX\\Data\\enwiktionary-20151123-pages-articles.xml"
    private val TARGET_DIRECTORY = "C:\\Users\\ggonz\\workspace\\FozruciX\\Data\\Wiktionary"
    private val OVERWRITE_EXISTING_FILES = true

    @Throws(Exception::class)
    @JvmStatic fun main(args: Array<String>) {
        val dumpFile = File(PATH_TO_DUMP_FILE)
        val outputDirectory = File(TARGET_DIRECTORY)

        JWKTL.parseWiktionaryDump(dumpFile, outputDirectory, OVERWRITE_EXISTING_FILES)
    }
}
