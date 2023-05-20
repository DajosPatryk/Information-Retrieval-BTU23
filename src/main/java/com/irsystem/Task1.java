package com.irsystem;
import java.util.ArrayList;
import java.util.List;

public class Task1 {

    /**
     * Creates a collection of divided documents out of a text document
     * @param filePath Filesystem path to the text document
     */
    public static void createDocumentCollection(String filePath){
        try {
            FileController fc = new FileController(filePath);
            List<List<String>> collection = divideDocument(fc);

            for (List<String> document : collection) {
                String filename = getStandardizedFilename(document.get(0), document.get(1));
                document.remove(0);
                fc.createTextFile("./collection_original/" + filename + ".txt", document);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Task Finished with Success");
    }

    /**
     * Creates collection by dividing one document
     * Creates new document after 3 blank lines
     * @param fc The Filecontroller reading the text file
     * @return Returns a collection of documents
     * @throws Exception Throws BufferedReader Exception
     */
    private static List<List<String>> divideDocument(FileController fc) throws Exception{
        String line;
        long linePos = 1;
        List<List<String>> collection = new ArrayList<List<String>>();
        List<String> document = new ArrayList<String>();
        document.add("0" + String.valueOf(linePos));  // Writes number of the starting line for the first document
        int blankLineCounter = 0;   // Counts the number of blank lines in a row

        try {
            while((line = fc.bufferedReader.readLine()) != null){   // Reads each line of the document

                if(blankLineCounter >= 3){      // If counter is over 3, writes new document
                    if(line.isBlank()){         // If line is blank, skips line and iterates counter
                        blankLineCounter++;
                    } else{                     // Else resets counter, saves old document to collection and writes new document
                        blankLineCounter = 0;

                        collection.add(document);
                        document = new ArrayList<String>();

                        if(linePos < 10){   // Sets a leading 0 for numbers < 10
                            document.add("0" + String.valueOf(linePos));
                        } else{
                            document.add(String.valueOf(linePos));
                        }
                        document.add(line);
                    }
                } else if(line.isBlank()){     // If line is blank, skips line and iterates counter
                    blankLineCounter++;
                } else{                 // Else resets counter and adds line to document
                    blankLineCounter = 0;

                    document.add(line);
                }
                linePos++;
            }

            collection.add(document);
            return collection;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Creates a standardized file name
     * All lower case letters and spaces replaces with underscores
     * @param number Number usually referring to the line position
     * @param name Name of the file
     * @return Returns standardized name
     */
    private static String getStandardizedFilename(String number, String name){
        name = name.toLowerCase();                                        // Sets all letters to lower case
        name = name.trim();                                               // Removes all spaces at the beginning and end
        name = name.replaceAll(" ", "_");               // Replaces all spaces with underscores
        name = name.replaceAll("[\\\\/:*?\"<>|]", "");  // Removes all illegal characters
        return number + "_" + name;
    }
}
