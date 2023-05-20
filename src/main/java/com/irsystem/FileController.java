package com.irsystem;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class FileController {

    // File to control
    private File _file;
    // Reader with a buffer that reads the file
    public BufferedReader bufferedReader; 

    /**
     * Initializing the class with file and declaring a BufferedReader
     * @param filePath Path to the file
     */
    public FileController(String filePath) throws Exception{
        this._file = new File(filePath);
        try {
            this.bufferedReader = new BufferedReader(new FileReader(_file));
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
    public void createTextFile(String filename, List<String> lines) throws Exception{
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
}
