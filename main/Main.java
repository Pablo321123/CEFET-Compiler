package main;

import java.io.IOException;

import syntactic.SyntacticAnalysis;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("\nPlease, enter with file path!\nExample: ./Main 'filePath.txt'");
            return;
        }

        try {
            SyntacticAnalysis parser = new SyntacticAnalysis(args[0]);
            parser.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
