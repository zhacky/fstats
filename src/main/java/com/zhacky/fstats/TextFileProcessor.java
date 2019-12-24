package com.zhacky.fstats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class TextFileProcessor extends BaseFileProcessor {



    @Override
    public String initialize(String[] args) {
        files = new ArrayList<>();
        logger = Logger.getLogger(TextFileProcessor.class.getName());
        logger.setLevel(INFO);
        String res = super.initialize(args);

        return res;
    }

    @Override
    protected void printStats(File file) throws Exception {
        List<String> contents = Files.readAllLines(file.toPath());
        System.out.println(contents.toString());
    }
}
