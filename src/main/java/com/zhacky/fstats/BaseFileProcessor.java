package com.zhacky.fstats;

import com.zhacky.fstats.messages.Messages;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.logging.Level.INFO;

public abstract class BaseFileProcessor implements FileProcessor {
    static Logger logger;
    private File sourceDir;
    private File processedDir;
    List<File> files;
    private boolean listening;


    @Override
    public String initialize(String[] args) {
        if (logger == null) {
            logger = Logger.getLogger(BaseFileProcessor.class.getName());
            logger.setLevel(INFO);
        }
        listening = false;
        files = new ArrayList<>();

        String message = "";
        if (args.length == 0) {
            message = Messages.INTRO;
            message += "\n";
            message += Messages.USAGE;
            setUpDefaultDirectories();
            watchDirectory(sourceDir.toPath(), files);
            return message;
        } else {
            List<String> params = Arrays.asList(args);
            if (!params.contains("-s") || !params.contains("-p")) {
                message = Messages.USAGE;
                return message;
            } else {
                int sourceIndex = params.indexOf("-s") + 1;
                try {
                    int processedIndex = params.indexOf("-p") + 1;
                    String prc = params.get(processedIndex);
                    if (isDirectory(prc)) {
                        processedDir = new File(prc);
                        // TODO: Add process
                        logger.log(Level.INFO, "Processed Directory = " + prc + "\n");
                    } else {
                        return "[-p] directory is invalid or does not exist";
                    }

                    String src = params.get(sourceIndex);
                    if (isDirectory(src)) {
                        sourceDir = new File(src);
                        logger.log(Level.INFO, "Source Directory = " + src + "\n");

                        // TODO: listen for changes
                        watchDirectory(sourceDir.toPath(), files);


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

    private void setUpDefaultDirectories() {
        sourceDir = new File("./in");
        if (!Files.exists(sourceDir.toPath(), NOFOLLOW_LINKS)) {
            boolean created = sourceDir.mkdir();
            System.out.println("dir created: " + created);
        }
        processedDir = new File("./out");
        if (Files.exists(processedDir.toPath(), NOFOLLOW_LINKS)) {
            processedDir.mkdir();
        }

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


        // initially process files
        startProcessingFiles();

        try {
            WatchService service = fs.newWatchService();
            path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            WatchKey key;
            while (listening) {
                key = service.take();

                WatchEvent.Kind<?> kind;

                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue; // skip
                    } else if (ENTRY_CREATE == kind) {
                        // new filepath created
                        Path newPath = (Path) watchEvent.context();
                        logger.log(INFO, "New path created: " + newPath);
                        // get files from this directory

                        // if there is a change, process the file right away
                        startProcessingFiles();
                    } else if (ENTRY_MODIFY == kind) {
                        // a file has been modified
                        Path modPath = (Path) watchEvent.context();
                        //TODO: Add process
                        startProcessingFiles();
                        logger.log(INFO, "Path has been modified: " + modPath);
                    } else if (ENTRY_DELETE == kind) {
                        // a file has been deleted
                        Path rmPath = (Path) watchEvent.context();
                        startProcessingFiles();
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


    }

    private void startProcessingFiles() {
        if (sourceDir.listFiles() != null && sourceDir.listFiles().length > 0 && processedDir.exists()) {
            files = Arrays.asList(sourceDir.listFiles());
            if (files != null && !files.isEmpty()) {
                processFiles(files);
            }
        }
    }

    @Override
    public void processFiles(List<File> files) {
        for (File file :
                files) {
            processFile(file);
        }
    }

    private void lockFile(File file) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            FileChannel fc = randomAccessFile.getChannel();
            FileLock lock = fc.lock();
            try {
                lock = fc.tryLock();
                if (lock != null) {
                    //Important: this is where file gets actually processed
                    System.out.println("processing " + file.getName());
                    while (randomAccessFile.read() == -1) {
                        // TODO: fix conflict with OS builtin file-locking
                        System.out.println(randomAccessFile.readLine());
                    }
                }
            } catch (OverlappingFileLockException ignored) {
            }
            assert lock != null;
            lock.release();
            fc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void processFile(File file) {
        logger.log(INFO, "Processing file...\n" + file.getName());
        try {
            printStats(file);
            String destination = processedDir.getAbsolutePath();
            moveToProcessedFolder(file, destination);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveToProcessedFolder(File file, String destination) {
        Path destFile = Paths.get(destination + "/" + file.getName()); //.replace(" ", "\\ ")
        logger.log(INFO, "Destination Path: " +
                "" + destFile.toString());
        try {
            Path temp = Files.move(Paths.get(String.valueOf(file.getAbsoluteFile())), destFile, REPLACE_EXISTING);
            logger.log(INFO, "File has been moved here: " + temp);
            if (temp != null) {
                logger.log(INFO, "File moved successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected abstract void printStats(File file) throws Exception;

}
