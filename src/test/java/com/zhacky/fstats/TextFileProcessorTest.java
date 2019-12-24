package com.zhacky.fstats;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;

public class TextFileProcessorTest {
    TextFileProcessor processor;
    @Before
    public void setUp() throws Exception {
        processor = new TextFileProcessor();
    }

    @Test
    public void test_initialize_with_no_args_prints_Usage() {

        // given
        String[] args = new String[0];


        // when
        System.out.println("---------------------------");
        String message = processor.initialize(args);
        // then
        assertTrue(message.contains("Usage:"));
    }

    @Test
    public void test_isDirectory_with_invalid_path_returns_false() {
        // given
        String path = "bogus_path";
        // when
        boolean result = processor.isDirectory(path);
        //then
        assertFalse(result);

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_isDirectory_with_file_name_returns_false() {
        // given
        String path = "/initrd.img";
        // when
        boolean result = processor.isDirectory(path);
        // then
        // should throw IllegalArgumentException
    }

    @Test
    public void test_moveToProcessedFolder_should_move_file() {
        // given
        File file = new File("/data/Laboratory/Java/FileStatisticsApp/fstats/src/main/resources/demo_source/file1.txt");
        String destination = "/data/Laboratory/Java/FileStatisticsApp/fstats/src/main/resources/demo_processed/file1.txt";
        // then
        processor.moveToProcessedFolder(file, destination);
        File destFile = new File(destination);
        assertNotNull(destFile);

    }
}
