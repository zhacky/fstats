package com.zhacky.fstats;

import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.OFF;

public class TextFileProcessor extends BaseFileProcessor {

    @Override
    public String initialize(String[] args) {
        logger = Logger.getLogger(TextFileProcessor.class.getName());
        logger.setLevel(INFO);
        String res = super.initialize(args);

        return res;
    }
}
