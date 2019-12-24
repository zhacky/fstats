package com.zhacky.fstats;

import com.zhacky.fstats.messages.Messages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public abstract class BaseFileProcessor implements FileProcessor {
    private File sourceDir;
    private File processedDir;
    private List<File> files;

    @Override
    public String initialize(String[] args) {
        String message = "";
        if (args.length == 0) {
            message = Messages.INTRO;
            message += "\n";
            message += Messages.USAGE;
            return message;
        } else {
            List<String> params = Arrays.asList(args);
            if (!params.contains("-s") || !params.contains("-p")) {
                message = Messages.USAGE;
                return message;
            } else {
                int sourceIndex = params.indexOf("-s") + 1;
                try {
                    String src = params.get(sourceIndex);
                    if (isDirectory(src)) {
                        sourceDir = new File(src);
                        message = "Source Directory = " + src + "\n";
                        // TODO: listen for changes
                        //TODO: Add process

                    }

                    int processedIndex = params.indexOf("-p") + 1;
                    String prc = params.get(processedIndex);
                    if (isDirectory(prc)) {
                        processedDir = new File(prc);
                        // TODO: Add process
                        message += "Processed Directory = " + prc + "\n";
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    message = "Paths cannot be empty";
                    return message;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return message;
    }

    @Override
    public boolean isDirectory(String path) {
        File dir = new File(path);
        Path srcDir = dir.toPath();
        try {
            Boolean valid = (Boolean) Files.getAttribute(srcDir, "basic:isDirectory", NOFOLLOW_LINKS);
            if (!valid) {
                throw new IllegalArgumentException(path + " is not a valid directory");
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public void watchDirectory(Path path) {



        files = Arrays.asList(sourceDir.listFiles());
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    public File getProcessedDir() {
        return processedDir;
    }

    public void setProcessedDir(File processedDir) {
        this.processedDir = processedDir;
    }
}
