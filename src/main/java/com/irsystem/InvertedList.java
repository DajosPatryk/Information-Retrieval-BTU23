package com.irsystem;

import java.io.*;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class InvertedList {
    /**
     * Format of the data is as follow:
     * <Word, <Filename, [Total terms, tf, idf, tf*idf]>>
     */
    private Map<String, Map<String, double[]>> _index = new HashMap<>();

    /**
     * Necessary global variables for TF
     */
    public double N;
    private Map<String, List<String>> _Nk = new HashMap<>();

    public void initN(String filePath) {
        File[] files = FileController.getFilesFromDirectory(filePath);
        this.N = Arrays.stream(files).filter(file -> file.getName().endsWith(".txt")).count();
    }

    public void indexCollection(String filesDirectory) throws Exception {
        File[] files = FileController.getFilesFromDirectory(filesDirectory);
        FileController fc;

        // Not stemmed
        this._Nk = new HashMap<>();
        for (File file : files) {
            this.N = files.length; // Initializing N
            fc = new FileController(file);
            String filename = file.getName();
            List<String> document = fc.readFile();
            index_Nk(filename, document, false); // Initializing _Nk
        }
        for (File file : files) {
            fc = new FileController(file);
            String filename = file.getName();
            List<String> document = fc.readFile();
            indexDocument(filename, document, false);
        }
        calculateTermWeight();

        writeDocument(filesDirectory + "/" + GlobalVariables._INVERTEDINDEXPATH);

        this._index = new HashMap<>();

        // Stemmed
        this._Nk = new HashMap<>();
        for (File file : files) {
            this.N = files.length; // Initializing N
            fc = new FileController(file);
            String filename = file.getName();
            List<String> document = fc.readFile();
            index_Nk(filename, document, true); // Initializing _Nk
        }
        for (File file : files) {
            fc = new FileController(file);
            String filename = file.getName();
            List<String> document = fc.readFile();
            indexDocument(filename, document, true);
        }
        calculateTermWeight();

        writeDocument(filesDirectory + "/" + GlobalVariables._INVERTEDINDEXSTEMMEDPATH);

        this._index = new HashMap<>();
        this._Nk = new HashMap<>();

        System.out.println("Success: Finished word indexing for " + filesDirectory);
    }

    /**
     * Indexes all words from a document for Inverted List search
     * 
     * @param filename File name
     * @param document Document content
     * @param stemming Use stemming of words
     */
    private void indexDocument(String filename, List<String> lines, boolean stemming) {
        int totalTerms = 0;

        // Counting total terms found in document for future evaluation
        for (String line : lines) {
            String[] words = line.split("\\W+");
            totalTerms += words.length;
        }

        // Collects all document frequencies
        Map<String, double[]> termFrequency = new HashMap<String, double[]>();
        for (String line : lines) {
            String[] words = line.split("\\W+");
            for (String word : words) {
                if (stemming)
                    word = new Stemmer(word).getWord().toLowerCase();
                else
                    word = word.toLowerCase();
                if (!termFrequency.containsKey(word)) {
                    termFrequency.put(word, new double[1]);
                    termFrequency.get(word)[0] = 1;
                } else {
                    termFrequency.get(word)[0]++;
                }
            }
        }

        for (String line : lines) {
            String[] words = line.split("\\W+");
            for (String word : words) {
                if (word != "" || word != null) {
                    if (stemming)
                        word = new Stemmer(word).getWord().toLowerCase();
                    else
                        word = word.toLowerCase();
                    if (!this._index.containsKey(word)) {
                        this._index.put(word, new HashMap<String, double[]>());
                        this._index.get(word).put(filename, new double[4]);
                        this._index.get(word).get(filename)[0] = totalTerms;
                        this._index.get(word).get(filename)[1] = 1;
                        this._index.get(word).get(filename)[2] = Double.parseDouble(
                                String.format("%.8f", VectorSpace.idf(termFrequency, this.N, this._Nk)));
                        this._index.get(word).get(filename)[3] = 0;
                    } else if (!this._index.get(word).containsKey(filename)) {
                        this._index.get(word).put(filename, new double[4]);
                        this._index.get(word).get(filename)[0] = totalTerms;
                        this._index.get(word).get(filename)[1] = 1;
                        this._index.get(word).get(filename)[2] = Double
                                .parseDouble(String.format("%.8f", VectorSpace.idf(termFrequency, this.N,
                                        this._Nk)));
                        this._index.get(word).get(filename)[3] = 0;
                    } else {
                        this._index.get(word).get(filename)[1]++;
                    }
                }
            }
        }
    }

    /**
     * Indexes all words from a document for Inverted List search
     * 
     * @param filename File name
     * @param document Document content
     * @param stemming Use stemming of words
     */
    private void index_Nk(String filename, List<String> lines, boolean stemming) {
        for (String line : lines) {
            String[] words = line.split("\\W+");
            for (String word : words) {
                if (word != "" || word != null) {
                    if (stemming)
                        word = new Stemmer(word).getWord().toLowerCase();
                    else
                        word = word.toLowerCase();
                    if (!this._Nk.containsKey(word)) {
                        this._Nk.put(word, new ArrayList<String>());
                        this._Nk.get(word).add(filename);
                    } else if (!this._Nk.get(word).contains(filename)) {
                        this._Nk.get(word).add(filename);
                    }
                }
            }
        }
    }

    /**
     * Calculates term weight for every word in a document.
     * Calculates term weight with the tf*idf formula.
     */
    private void calculateTermWeight() {
        for (String word : this._index.keySet()) {
            for (String file : this._index.get(word).keySet()) {
                this._index.get(word).get(file)[3] = Double.parseDouble(
                        String.format("%.8f", VectorSpace.tfidf(this._index.get(word).get(file)[2],
                                this._index.get(word).get(file)[1], this.N, this._Nk.get(word).size())));
            }
        }
    }

    public void readDocument(String filePath) throws IOException {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Reader reader = new FileReader(filePath);
            java.lang.reflect.Type typeOfHashMap = new TypeToken<Map<String, Map<String, double[]>>>() {
            }.getType();
            this._index = gson.fromJson(reader, typeOfHashMap);
            reader.close();
        } catch (Exception err) {
            throw new IOException(err);
        }

    }

    public void writeDocument(String filePath) throws IOException {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
            Writer writer = new FileWriter(filePath);
            gson.toJson(this._index, writer);
            writer.close();
        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    /**
     * Format of the data is as follow:
     * <Word, <Filename, [Total terms, Word frequency, Term weight]>>
     * 
     * @return Map<String, Map<String, int[]>> _index
     */
    public Map<String, Map<String, double[]>> getIndex() {
        return this._index;
    }
}
