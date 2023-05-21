package com.irsystem;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls collections in applications directories and adds functionality
 */
public class CollectionController {

    /**
     * Creates a collection of divided documents out of a text document
     * @param filePath Filesystem path to the text document
     */
    public static void createDocumentCollection(String filePath){
        try {
            FileController fc = new FileController(filePath);
            List<List<String>> collection = divideDocument(fc);

            for (List<String> document : collection) {
                String filename = FileController.getStandardizedFilename(document.get(0), document.get(1));
                document.remove(0);
                FileController.createTextFile(GlobalVariables._ORIGINALDIRECTORY + "/" + filename + ".txt", document);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Success: Created collections out of documents");
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
            fc.bufferedReader.close();
            return collection;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
