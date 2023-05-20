package com.irsystem;

import java.io.*;

public class App 
{
    public static void main( String[] args )
    {
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(System.in));   // Reads user input with a buffer
        while(true){    // Runs console endlessly
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
     * @param command Command to be asserted
     */
    private static void commandController(String command){
        String[] args = command.split(" ");

        switch(args[0].toLowerCase()){
            case "--extract-collection":
                Task1.createDocumentCollection(args[1]);
                break;
            default:
                System.out.println("Wrong command.");
        }
    }
}
