package com.irsystem;

import java.io.*;
import java.sql.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controls search functionality
 */
public class SearchController {



    /**
     * Performs linear search with query and files directory
     * 
     * @param query           Query input
     * @param documentsSource Files directory
     * @param stemming        Use stemmed words
     */
    public static List<String> linearSearch(String[] queries, String operator, String documentsSource, boolean stemming) throws Exception {
        try {
            List<String> result = doBooleanLinearSearch(queries, operator, documentsSource, stemming);
            writeOutput(result);
            return result;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Performs inverted list search with query and files directory
     * 
     * @param query           Query input
     * @param documentsSource Files directory
     * @param stemming        Use stemmed words
     */
    public static List<String> invertedListSearch(String[] queries, String operator, String documentsSource, boolean stemming) throws Exception {
        try {
            List<String> result = doInvertedListSearch(queries, operator, documentsSource, stemming);
            writeOutput(result);
            return result;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Parses the query
     * 
     * @param query Query input
     * @return Returns String Array of query
     */
    public static String[] parseQuery(String query) {
        Pattern pattern = Pattern.compile("(-)?(\\w+)([&|])?(\\w+)?");
        Matcher matcher = pattern.matcher(query);

        if (matcher.matches()) {
            String operator = matcher.group(1) != null ? "-" : matcher.group(3);
            String firstQuery = matcher.group(2);
            String secondQuery = matcher.group(4);

            if (secondQuery == null) {
                return new String[] { operator, firstQuery };
            } else {
                return new String[] { operator, firstQuery, secondQuery };
            }
        }

        throw new IllegalArgumentException("Invalid query format.");
    }

    /**
     * Performs boolean linear search with query and files directory
     * 
     * @param query          Query input
     * @param filesDirectory Files directory
     * @param stemming       Use stemmed words
     * @return Returns file names as List of String
     * @throws Exception Throws Exception
     */
    private static List<String> doBooleanLinearSearch(String[] query, String operator, String filesDirectory,
            boolean stemming)
            throws Exception {
        File[] files = FileController.getFilesFromDirectory(filesDirectory);
        FileController fc;
        List<String> foundFiles = new ArrayList<String>();

        for (File file : files) {
            try {
                fc = new FileController(file);
                List<String> document = fc.readFile();

                boolean[] check = new boolean[2];
                for (int i = 0; i < query.length; i++) {
                    for (String line : document) {
                        if (!stemming && line.toLowerCase().contains(query[i].toLowerCase())) {
                            check[i] = true;
                            break;
                        } else if (stemming) {
                            String[] words = line.toLowerCase().split(" ");
                            for (int j = 0; j < words.length; j++) {
                                words[j] = new Stemmer(words[j]).getWord();
                            }
                            if (Arrays.asList(words).contains(query[i].toLowerCase())) {
                                check[i] = true;
                                break;
                            }
                        }
                    }
                }

                if (operator != null) {
                    switch (operator) {
                        case "&":
                            if (check[0] && check[1])
                                foundFiles.add(file.getName());
                            break;
                        case "|":
                            if (check[0] || check[1])
                                foundFiles.add(file.getName());
                            break;
                        case "-":
                            if (!check[0])
                                foundFiles.add(file.getName());
                            break;
                    }
                } else {
                    if (check[0])
                        foundFiles.add(file.getName());
                }
            } catch (Exception e) {
                throw new Exception(e);
            }
        }

        return foundFiles;
    }

    private static List<String> doInvertedListSearch(String[] query, String operator, String filesDirectory,
        boolean stemming) throws Exception {
        List<String> foundFiles = new ArrayList<String>();

        InvertedList invertedList = new InvertedList();
        Map<String, List<String>> index;
        if(stemming){
            invertedList.readDocument(filesDirectory + "/" + GlobalVariables._INVERTEDINDEXSTEMMEDPATH);
            index = invertedList.getIndex();
        } else {
            invertedList.readDocument(filesDirectory + "/" + GlobalVariables._INVERTEDINDEXPATH);
            index = invertedList.getIndex();
        }

        List<List<String>> filenameList = new ArrayList<List<String>>();
        for (String queryParam : query) {
            try {
                if(index.containsKey(queryParam)) filenameList.add(index.get(queryParam));
            } catch (Exception e) {
                throw new Exception(e);
            }
        }

        if (operator != null) {
                    switch (operator) {
                        case "&":
                            filenameList.get(0).retainAll(filenameList.get(1));
                            foundFiles = filenameList.get(0);
                            break;
                        case "|":
                            foundFiles.addAll(filenameList.get(0));
                            foundFiles.addAll(filenameList.get(1));
                            break;
                        case "-":
                            List<String> filenames = new ArrayList<>(Arrays.asList(FileController.getFilenamesFromDirectory(filesDirectory)));
                            if(filenameList.size() != 0) filenames.removeAll(filenameList.get(0));
                            filenames.remove(GlobalVariables._INVERTEDINDEXPATH);
                            filenames.remove(GlobalVariables._INVERTEDINDEXSTEMMEDPATH);
                            foundFiles = filenames;
                            break;
                    }
                } else {
                    foundFiles = filenameList.get(0);
                }

        return foundFiles;
    }

    /**
     * Writes output native to search success
     * 
     * @param output Output to be written as List of String
     */
    private static void writeOutput(List<String> output) {
        for (String line : output) {
            System.out.println(line);
        }
    }
}
