package com.irsystem;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Controls text files in filesystem
 */
public class FileController {

    // File to control
    private File _file;
    // Reader with a buffer that reads the file
    public BufferedReader bufferedReader; 

    /**
     * Initializing the class with file and declaring a BufferedReader
     */
    public FileController(String filePath) throws Exception{
        this._file = new File(filePath);
        try {
            this.bufferedReader = new BufferedReader(new FileReader(_file));
        } catch (Exception e) {
            throw new Exception(e);
        }
    } 
    public FileController(File file) throws Exception{
        this._file = file;
        try {
            this.bufferedReader = new BufferedReader(new FileReader(_file));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Reads all lines of the file and returns a List of String
     */
    public List<String> readFile() throws Exception{
        try {
            List<String> document = new ArrayList<String>();
            String line;
            while((line = this.bufferedReader.readLine()) != null){
                line.trim();
                document.add(line);
            }

            bufferedReader.close();
            return document;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Creates a new text file and writes List of lines
     * @param fileName The file name
     * @param lines The file data as lines
     * @throws Exception Throws java.nio.file Exception
     */
    public static void createTextFile(String filename, List<String> lines) throws Exception{
        Files.createDirectories(Paths.get(filename.split("/")[0] + "/" + filename.split("/")[1]));
        File file = new File(filename);
        file.createNewFile();
        FileWriter writer = new FileWriter(filename);
        try {
            for (String line : lines) {
                writer.write(line + "\n");
            }
            writer.close();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Returns all files from a directory
     * @param directoryPath Path to the directory
     * @return Returns all files from a directory as Array
     */
    public static File[] getFilesFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.listFiles();
    }

    /**
     * Returns all files from a directory
     * @param directoryPath Path to the directory
     * @return Returns all files from a directory as Array
     */
    public static String[] getFilenamesFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.list();
    }

    /**
     * Creates a standardized file name
     * All lower case letters and spaces replaces with underscores
     * @param number Number usually referring to the line position
     * @param name Name of the file
     * @return Returns standardized name
     */
    public static String getStandardizedFilename(String number, String name){
        name = name.toLowerCase();                                        // Sets all letters to lower case
        name = name.trim();                                               // Removes all spaces at the beginning and end
        name = name.replaceAll(" ", "_");               // Replaces all spaces with underscores
        name = name.replaceAll("[\\\\/:*?\"<>|]", "");  // Removes all illegal characters
        return number + "_" + name;
    }
}
