package com.irsystem;

import java.io.*;
import java.util.*;

/**
 * Controls search functionality
 */
public class SearchController {

    /**
     * Performs linear search with query and files directory
     * @param query Query input
     * @param documentsSource Files directory
     */
    public static void linearSearch(String query, String documentsSource) {
        try {
            List<String> result = doBooleanLinearSearch(query, documentsSource);
            writeOutput(result);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Performs boolean linear search with query and files directory
     * @param query Query input
     * @param filesDirectory Files directory
     * @return Returns file names as List of String
     * @throws Exception Throws Exception
     */
    private static List<String> doBooleanLinearSearch(String query, String filesDirectory) throws Exception {
        File[] files = FileController.getFilesFromDirectory(filesDirectory);
        FileController fc;
        List<String> foundFiles = new ArrayList<String>();

        for (File file : files) {
            try {
                fc = new FileController(file);
                List<String> document = fc.readFile();

                boolean check = false;
                for (String line : document) {
                    if (line.toLowerCase().contains(query.toLowerCase())) {
                        check = true;
                        break;
                    }
                }
                if (check)
                    foundFiles.add(file.getName());
            } catch (Exception e) {
                throw new Exception(e);
            }
        }

        return foundFiles;
    }

    /**
     * Writes output native to search success
     * @param output Output to be written as List of String
     */
    private static void writeOutput(List<String> output) {
        for (String line : output) {
            System.out.println(line);
        }
    }
}
