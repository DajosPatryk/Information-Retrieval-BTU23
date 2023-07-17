package com.irsystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VectorSpace {

    public static double tf(double frequency, double N, double Nk) {
        return frequency * Math.log(N / Nk);
    }

    public static double idf(Map<String, double[]> TFdi, double N, Map<String, List<String>> Nk) {
        double idf = 0;

        for (String key : TFdi.keySet()) {
            double termFrequency = TFdi.get(key)[0];
            double currentNk = (double)Nk.get(key).size();
            idf = idf + Math.pow(termFrequency * Math.log(N / currentNk), 2);
        }

        return Math.sqrt(idf);
    }

    /**
     * Calculates term weight with the tf*idf formula
     * @param idf
     * @param tf Basic term frequency tf
     * @param N
     * @param Nk
     * @return Returns double
     */
    public static double tfidf(double idf, double tf, double N, double Nk) {
        return tf(tf, N, Nk) / idf;
    }

    public static Map<String, double[]> calculateQueryWeight(String[] query, double N, Map<String, Map<String, double[]>> Nk){
        Map<String, double[]> tf = new HashMap<String, double[]>();
        for (String word : query) {
            if(!tf.containsKey(word)){
                tf.put(word, new double[1]);
                tf.get(word)[0] = 1;
            } else{
                tf.get(word)[0]++;
            }
        }

        double maxTf = 0;
        for (String word : tf.keySet()) {
            if(tf.get(word)[0] > maxTf) maxTf = tf.get(word)[0];
        }

        Map<String, double[]> res = new HashMap<String, double[]>();
        for (String word : query) {
            if(!res.containsKey(word) && Nk.containsKey(word)){
                res.put(word, new double[1]);
                res.get(word)[0] = ((0.5 + (0.5 * tf.get(word)[0]) / maxTf)) * Math.log(N / Nk.get(word).keySet().size());
            }
        }
        
        return res;
    }

    /**
     * Calculates Query weight w_qk
     * @param query Terms of query
     * @param N
     * @param index The inverted list which includes all term weights of each document
     * @param filenameList The found files with inverted list search
     * @return Returns weight in double[0] for each key of filename type Map<String, double[]>
     */
    public static Map<String, double[]> calculateSimilarity(String[] query, double N, Map<String, Map<String, double[]>> index, List<String> filenameList){
        Map<String, double[]> res = new HashMap<String, double[]>();
        Map<String, double[]> queryWeight = calculateQueryWeight(query, N, index);

        for (String word : query) {
            if(index.containsKey(word)){
                for (String filename : filenameList) {
                    if(index.get(word).containsKey(filename)){
                        if(!res.containsKey(filename)){
                            res.put(filename, new double[1]);
                            res.get(filename)[0] = queryWeight.get(word)[0] * index.get(word).get(filename)[3];
                        } else {
                             res.get(filename)[0]+= queryWeight.get(word)[0] * index.get(word).get(filename)[3];
                        }
                    }
                }
            }
        }

        return res;
    }
}
