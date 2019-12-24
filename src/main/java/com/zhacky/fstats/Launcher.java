package com.zhacky.fstats;

public class Launcher {
    public static void main(String[] args) {

        FileProcessor fp = new TextFileProcessor();
        String message = fp.initialize(args);
        System.out.println(message);
    }
}
