package com.irsystem;

import java.io.*;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class EvaluationController {
    private final String _FILENAMEFORMAT_REGEX = "^[0-9]+";
    private Map<String, List<Integer>> _index;

    private double _precision = 0.0;
    private double _recall = 0.0;

    public EvaluationController() {
        try {
            readDocument(GlobalVariables._EVALUATIONFILEPATH);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void readDocument(String filePath) throws IOException {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Reader reader = new FileReader(filePath);
            java.lang.reflect.Type typeOfHashMap = new TypeToken<Map<String, List<Integer>>>() {
            }.getType();
            this._index = gson.fromJson(reader, typeOfHashMap);
            reader.close();
        } catch (Exception err) {
            throw new IOException(err);
        }
    }

    public List<Integer> convertFilenamesToIndex(List<String> filenames) {
        Pattern pattern = Pattern.compile(this._FILENAMEFORMAT_REGEX);
        List<Integer> res = new ArrayList<Integer>();

        for (String filename : filenames) {
            try {
                Matcher matcher = pattern.matcher(filename);
                if (matcher.find())
                    res.add(Integer.parseInt(matcher.group()));
                else
                    throw new Exception();
            } catch (Exception e) {
                System.out.println("Couldn't convert " + filename + " to Index number.");
            }
        }

        return res;
    }

    public void evaluate(String[] queries, String operator, List<String> queryResult) throws Exception {
        try {
            List<Integer> res = convertFilenamesToIndex(queryResult);

            // Retrieves Query Result of Document
            List<List<Integer>> docResQueries = new ArrayList<List<Integer>>();
            for (String query : queries) {
                if (this._index.containsKey(query))
                    docResQueries.add(this._index.get(query));
                else
                    throw new Exception("-1");
            }

            // Evaluates Query Results of Document
            List<Integer> docRes = new ArrayList<Integer>();
            if (operator != null) {
                switch (operator) {
                    case "&":
                        docResQueries.get(0).retainAll(docResQueries.get(1));
                        docRes = docResQueries.get(0);
                        break;
                    case "|":
                        docRes.addAll(docResQueries.get(0));
                        docRes.addAll(docResQueries.get(1));
                        break;
                    case "-":
                        List<String> filenames = new ArrayList<String>(Arrays
                                .asList(FileController.getFilenamesFromDirectory(GlobalVariables._ORIGINALDIRECTORY)));
                        List<Integer> fileIndex = new ArrayList<Integer>(convertFilenamesToIndex(filenames));
                        if (fileIndex.size() != 0)
                            fileIndex.removeAll(docResQueries.get(0));
                        break;
                }
            } else {
                docRes = docResQueries.get(0);
            }

            // Calculates Precision and Recall
            List<Integer> truePositive = new ArrayList<Integer>(docRes);
            truePositive.retainAll(res);
            List<Integer> falsePositive = new ArrayList<Integer>(res);
            falsePositive.removeAll(truePositive);
            List<Integer> falseNegative = new ArrayList<>(docRes);
            falseNegative.removeAll(truePositive);

            this._precision = (double)truePositive.size() / ((double)truePositive.size() + (double)falsePositive.size());
            this._recall = (double)truePositive.size() / ((double)truePositive.size() + (double)falseNegative.size());
        } catch (Exception e) {
            switch (e.getMessage()) {
                case "-1":
                    this._precision = -1.0;
                    this._recall = -1.0;
                    break;
                default:
                    throw new Exception(e);
            }
        }
    }

    public String getPrecision(){
        if(this._precision != -1.0) return Double.toString(this._precision);
        else return "?";
    }

    public String getRecall(){
        if(this._recall != -1.0) return Double.toString(this._recall);
        else return "?";
    }
}
