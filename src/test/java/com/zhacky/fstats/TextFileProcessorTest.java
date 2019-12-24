package com.zhacky.fstats;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
}
