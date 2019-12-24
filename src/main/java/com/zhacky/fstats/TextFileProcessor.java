package com.zhacky.fstats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.OFF;

public class TextFileProcessor extends BaseFileProcessor {



    @Override
    public String initialize(String[] args) {
        files = new ArrayList<>();
        logger = Logger.getLogger(TextFileProcessor.class.getName());
        logger.setLevel(INFO); // set to INFO for verbose logging
        String res = super.initialize(args);

        return res;
    }

    @Override
    protected void printStats(File file) throws Exception {
        List<String> contents = Files.readAllLines(file.toPath());
        Stats stats = new Stats(contents);
        System.out.println("---------------------");
        System.out.println("File Name:\t\tType:");
        System.out.println("\"" + file.getName().substring(0,file.getName().indexOf(".")) + "\"\t\t" + file.getName().substring(file.getName().indexOf(".")));
        System.out.println("Number Of Lines: ");
        System.out.println(stats.getNumberOfLines());
        System.out.println("Number Of Words:");
        System.out.println(stats.getNumberOfWords());
        System.out.println("Number Of Dots:");
        System.out.println(stats.getNumberOfDots());
        System.out.println("---------------------");


    }
}
