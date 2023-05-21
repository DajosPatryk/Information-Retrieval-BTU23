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

        switch (args.get(0)) {
            case "--extract-collection":
                CollectionController.createDocumentCollection(args.get(1));
                DocumentController.removeStopWords();
                break;
            default:
                try {
                    String model = "";
                    String searchMode = "";
                    String documentSource = "";
                    String query = "";

                    int argPos;

                    argPos = args.indexOf("--model");
                    model = args.get(argPos + 1);

                    argPos = args.indexOf("--search-mode");
                    searchMode = args.get(argPos + 1);

                    argPos = args.indexOf("--query");
                    query = args.get(argPos + 1).replace("\"", "");

                    argPos = args.indexOf("--documents");
                    switch(args.get(argPos + 1)){
                        case "\"original\"":
                            documentSource = GlobalVariables._ORIGINALDIRECTORY;
                            break;
                        case "\"no_stopword\"":
                            documentSource = GlobalVariables._NO_STOPWORDDIRECTORY;
                            break;
                        default:
                            System.out.println("Exception: Illegal Flag");
                    }

                    switch(model){
                        case "\"bool\"":
                            switch(searchMode){
                                case "\"linear\"":
                                    SearchController.linearSearch(query, documentSource);
                                    break;
                                default:
                                    System.out.println("Exception: Illegal Flag");
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
