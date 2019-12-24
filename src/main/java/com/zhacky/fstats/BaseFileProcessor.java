package com.zhacky.fstats;

import com.zhacky.fstats.messages.Messages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.FINE;

public abstract class BaseFileProcessor implements FileProcessor {
    static Logger logger;
    private File sourceDir;
    private File processedDir;
    private List<File> files;
    private boolean listening;
    private String stats;


    @Override
    public String initialize(String[] args) {
        if (logger == null) {
            logger = Logger.getLogger(BaseFileProcessor.class.getName());
            logger.setLevel(INFO);
        }
        listening = false;
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
                        logger.log(Level.INFO, "Source Directory = " + src + "\n");
                        // TODO: listen for changes
                        watchDirectory(sourceDir.toPath(), files);
                        //TODO: Add process

                    }

                    int processedIndex = params.indexOf("-p") + 1;
                    String prc = params.get(processedIndex);
                    if (isDirectory(prc)) {
                        processedDir = new File(prc);
                        // TODO: Add process
                        logger.log(Level.INFO, "Processed Directory = " + prc + "\n");
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
    public void watchDirectory(Path path, List<File> files) {
        listening = true;
        FileSystem fs = path.getFileSystem();

        try {
            WatchService service = fs.newWatchService();
            path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            WatchKey key = null;
            while (listening) {
                key = service.take();

                WatchEvent.Kind<?> kind = null;

                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue; // skip
                    } else if (ENTRY_CREATE == kind) {
                        // new filepath created
                        Path newPath = (Path) watchEvent.context();
                         logger.log(INFO, "New path created: " + newPath);
                    } else if (ENTRY_MODIFY == kind) {
                        // a file has been modified
                        Path modPath = (Path) watchEvent.context();
                        logger.log(INFO, "Path has been modified: " + modPath);
                    } else if (ENTRY_DELETE == kind) {
                        // a file has been deleted
                        Path rmPath = (Path) watchEvent.context();
                        logger.log(INFO, "Path has been deleted: " + rmPath);
                    }

                    if (!key.reset()) {
                        break; // check again
                    }

                }
            } // end listening loop

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }


        files = Arrays.asList(sourceDir.listFiles());
    }

    @Override
    public void processFiles(List<File> files, Path destination) {

        for (File file :
                files) {
            // create file lock
            createFileLock(destination, file);

        }
    }

    private void createFileLock(Path destination, File file) {
        RandomAccessFile randomAccessFile = null;

        try {
            randomAccessFile = new RandomAccessFile(file.getName(),"rw");
            FileChannel fc = randomAccessFile.getChannel();
            ByteBuffer buffer = null;
            FileLock fileLock = fc.tryLock();
            if (fileLock != null) {

                processFile(file, destination);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OverlappingFileLockException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFile(File file, Path destination) {

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
