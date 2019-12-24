package com.zhacky.fstats;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StatsTest {
    private File file;

    private Stats stats;

    @Before
    public void setUp() throws Exception {
        file = new File("/data/Laboratory/Java/FileStatisticsApp/fstats/src/main/resources/demo_source/file1.txt");
        stats = new Stats(Files.readAllLines(file.toPath()));
    }

    @Test
    public void test_getNumberOfLines() throws IOException {
        // given


        // when
        int numberOfLines = stats.getNumberOfLines();
        // then
        Assert.assertEquals(numberOfLines, 11);
    }

    @Test
    public void test_getNumberOfDots() {
        // given

        // when
        int numberOfDots = stats.getNumberOfDots();
        // then
        Assert.assertEquals(numberOfDots, 29);
    }

    @Test
    public void test_getNumberOfWords() {
        // given

        // when
        int numberOfWords = stats.getNumberOfWords();
        // then
        Assert.assertEquals(numberOfWords, 290);


    }
}
