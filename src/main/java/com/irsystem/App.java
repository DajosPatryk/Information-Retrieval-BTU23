package com.irsystem;

import java.io.*;
import java.util.*;

public class App {
    public static void main(String[] args) {
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in)); // Reads user input with a
                                                                                          // buffer
        while (true) { // Runs console endlessly
            try {
                String command = lineReader.readLine();
                commandController(command);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Asserts and runs commands
     * 
     * @param command Command to be asserted
     */
    private static void commandController(String command) {
        List<String> args = new ArrayList<String>();
        for (String arg : command.split(" ")) {
            args.add(arg.toLowerCase());
        }

        // Stopwatch Variables
        long start;
        long elapsedTime;
        double elapsedTimeInMilliSeconds;

        // Query Variables
        String[] parsedQuery;
        String operator;
        String[] queries;
        List<String> result;
        EvaluationController ec = new EvaluationController();

        switch (args.get(0)) {
            case "--extract-collection":
                CollectionController.createDocumentCollection(args.get(1));
                DocumentController.removeStopWords();

                try {
                    InvertedList invertedIndex = new InvertedList();
                    invertedIndex.indexCollection(GlobalVariables._ORIGINALDIRECTORY);
                    invertedIndex.indexCollection(GlobalVariables._NO_STOPWORDDIRECTORY);
                } catch (Exception err) {
                    System.out.println(err);
                }
                break;
            case "--test":
                Stemmer s = new Stemmer(args.get(1));
                s.step1a();
                s.step1b();
                s.step1c();
                s.step2();
                s.step3();
                s.step4();
                s.step5a();
                System.out.println(s.getWord());
                break;
            default:
                try {
                    String model = "";
                    String searchMode = "";
                    String documentSource = "";
                    String query = "";
                    boolean stemming = false;

                    int argPos;

                    argPos = args.indexOf("--model");
                    model = args.get(argPos + 1);

                    argPos = args.indexOf("--search-mode");
                    searchMode = args.get(argPos + 1);

                    argPos = args.indexOf("--query");
                    query = args.get(argPos + 1).replace("\"", "");

                    if (args.indexOf("--stemming") != -1)
                        stemming = true;

                    argPos = args.indexOf("--documents");
                    switch (args.get(argPos + 1)) {
                        case "\"original\"":
                            documentSource = GlobalVariables._ORIGINALDIRECTORY;
                            break;
                        case "\"no_stopword\"":
                            documentSource = GlobalVariables._NO_STOPWORDDIRECTORY;
                            break;
                        default:
                            System.out.println("Exception: Illegal Flag");
                    }

                    switch (model) {
                        case "\"bool\"":
                            switch (searchMode) {
                                case "\"linear\"":
                                    start = System.nanoTime();

                                    // Parsing Query
                                    parsedQuery = SearchController.parseQuery(query);
                                    operator = parsedQuery[0];
                                    queries = Arrays.copyOfRange(parsedQuery, 1, parsedQuery.length);

                                    // Launch Search
                                    result = SearchController.linearSearch(queries, operator, documentSource, stemming);

                                    // Stop Timer
                                    elapsedTime = System.nanoTime() - start;
                                    elapsedTimeInMilliSeconds = elapsedTime / 1_000_000.0;

                                    // Evaluate
                                    ec.evaluate(queries, operator, result);

                                    System.out.println("T=" + elapsedTimeInMilliSeconds + "ms,P=" + ec.getPrecision()
                                            + ",R=" + ec.getRecall());
                                    break;
                                case "\"inverted\"":
                                    start = System.nanoTime();

                                    // Parsing Query
                                    parsedQuery = SearchController.parseQuery(query);
                                    operator = parsedQuery[0];
                                    queries = Arrays.copyOfRange(parsedQuery, 1, parsedQuery.length);

                                    // Launch Search
                                    result = SearchController.invertedListSearch(queries, operator, documentSource,
                                            stemming);

                                    // Stop Timer
                                    elapsedTime = System.nanoTime() - start;
                                    elapsedTimeInMilliSeconds = elapsedTime / 1_000_000.0;

                                    // Evaluate
                                    ec.evaluate(queries, operator, result);

                                    System.out.println("T=" + elapsedTimeInMilliSeconds + "ms,P=" + ec.getPrecision()
                                            + ",R=" + ec.getRecall());
                                    break;
                                default:
                                    System.out.println("Exception: Illegal Flag");
                            }
                            break;
                        case "\"vector\"":
                            try {
                                start = System.nanoTime();

                                // Parsing Query
                                parsedQuery = SearchController.parseQuery(query);
                                operator = parsedQuery[0];
                                queries = Arrays.copyOfRange(parsedQuery, 1, parsedQuery.length);

                                // Launch Search
                                result = SearchController.vectorSpaceSearch(queries, operator, documentSource,
                                        stemming);

                                // Stop Timer
                                elapsedTime = System.nanoTime() - start;
                                elapsedTimeInMilliSeconds = elapsedTime / 1_000_000.0;

                                // Evaluate
                                ec.evaluate(queries, operator, result);

                                System.out
                                        .println("T=" + elapsedTimeInMilliSeconds + "ms,P=" + ec.getPrecision() + ",R="
                                                + ec.getRecall());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            System.out.println("Exception: Illegal Flag");
                    }

                } catch (Exception e) {
                    System.out.println("Exception: Illegal Command");
                }
        }
    }
}