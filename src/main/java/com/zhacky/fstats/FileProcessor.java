package com.zhacky.fstats;

import java.nio.file.Path;

public interface FileProcessor {

    String initialize(String[] args);

    boolean isDirectory(String path);

    void watchDirectory(Path path);
}
