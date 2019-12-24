package com.zhacky.fstats;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface FileProcessor {

    String initialize(String[] args);

    boolean isDirectory(String path);

    void watchDirectory(Path path, List<File> files);

    void processFiles(List<File> files, Path destination);

    void processFile(File file, Path destination);
}
