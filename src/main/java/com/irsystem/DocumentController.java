package com.irsystem;

import java.io.*;
import java.util.*;

/**
 * Controls documents in applications directories and adds functionality
 */
public class DocumentController {

    /**
     * Removes stop words from collections in original directory
     */
    public static void removeStopWords() {
        List<String> stopWords;
        try {
            FileController fc = new FileController(GlobalVariables._STOPWORDSFILEPATH);
            stopWords = fc.readFile();

            processFiles(stopWords);
            System.out.println("Success: Finished stop word removal");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Processes each file from the original directory
     * @param stopWords Stop words as List of String
     * @throws Exception Throws Exception
     */
    private static void processFiles(List<String> stopWords) throws Exception {
        File[] files = FileController.getFilesFromDirectory(GlobalVariables._ORIGINALDIRECTORY);

        for (File file : files) {
            try {
                FileController fc = new FileController(file);
                List<String> document = fc.readFile();

                document = removeStopWords(document, stopWords);
                FileController.createTextFile(GlobalVariables._NO_STOPWORDDIRECTORY + "/" + file.getName(), document);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
    }

    /**
     * Removes stop words from a single document
     * @param document Document to be processed
     * @param stopWords Stop words as List of String
     * @return Returns processed document as List of String
     */
    private static List<String> removeStopWords(List<String> document, List<String> stopWords) {
        List<String> processedDocument = new ArrayList<>();

        for (String line : document) {
            String cleanedLine = line.replaceAll("[^\\w\\s']+", "").replaceAll("'(?!\\w)|(?<!\\w)'", "");
            String[] words = cleanedLine.split("\\s+");
            StringBuilder processedLine = new StringBuilder();

            for (String word : words) {
                if (!stopWords.contains(word.toLowerCase())) processedLine.append(word).append(" ");
            }

            processedDocument.add(processedLine.toString().trim());
        }

        return processedDocument;
    }

}
