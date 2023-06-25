package com.irsystem;

import java.io.*;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class InvertedList {
    private Map<String, List<String>> _index = new HashMap<>();

    public void indexCollection(String filesDirectory) throws Exception {
        File[] files = FileController.getFilesFromDirectory(filesDirectory);
        FileController fc;

        // Not stemmed
        for (File file : files) {
            fc = new FileController(file);
            String filename = file.getName();
            List<String> document = fc.readFile();
            indexDocument(filename, document, false);
        }
        writeDocument(filesDirectory + "/" + GlobalVariables._INVERTEDINDEXPATH);

        this._index = new HashMap<>();

        // Not stemmed
        for (File file : files) {
            fc = new FileController(file);
            String filename = file.getName();
            List<String> document = fc.readFile();
            indexDocument(filename, document, true);
        }
        writeDocument(filesDirectory + "/" + GlobalVariables._INVERTEDINDEXSTEMMEDPATH);

        this._index = new HashMap<>();

        System.out.println("Success: Finished word indexing for " + filesDirectory);
    }

    /**
     * Indexes all words from a document for Inverted List search
     * 
     * @param filename File name
     * @param document Document content
     * @param stemming Use stemming of words
     */
    public void indexDocument(String filename, List<String> lines, boolean stemming) {

        for (String line : lines) {
            String[] words = line.split("\\W+");
            for (String word : words) {
                if (word != "" || word != null) {
                    if (stemming)
                        word = new Stemmer(word).getWord().toLowerCase();
                    else
                        word = word.toLowerCase();
                    if (!this._index.containsKey(word)) {
                        this._index.put(word, new ArrayList<String>());
                    }
                    else if(!this._index.get(word).contains(filename)){
                        this._index.get(word).add(filename);
                    }
                }
            }
        }
    }

    public void readDocument(String filePath) throws IOException {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Reader reader = new FileReader(filePath);
            java.lang.reflect.Type typeOfHashMap = new TypeToken<Map<String, List<String>>>() {
            }.getType();
            this._index = gson.fromJson(reader, typeOfHashMap);
            reader.close();
        } catch (Exception err) {
            throw new IOException(err);
        }

    }

    public void writeDocument(String filePath) throws IOException {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = new FileWriter(filePath);
            gson.toJson(this._index, writer);
            writer.close();
        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    public Map<String, List<String>> getIndex() {
        return this._index;
    }
}
