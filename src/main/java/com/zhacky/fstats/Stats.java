package com.zhacky.fstats;

import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;

public class Stats implements Serializable {
    final List<String> dataString;

    public Stats(List<String> dataString) {
        this.dataString = dataString;
    }

    public int getNumberOfLines() {

        return dataString.size();
    }

    public int getNumberOfDots() {
        int count = 0;
        for (int i = 0; i < dataString.size(); i++) {
            char[] chars = dataString.get(i).toCharArray();

            for (int c = 0; c < chars.length; c++) {
                if (chars[c] == '.') {
                    count++;
                }
            }
        }

        return count;
    }

    public int getNumberOfWords() {

        int wordCount = 0;
        for (int i = 0; i < dataString.size(); i++) {
            String line = dataString.get(i);
            StringTokenizer st = new StringTokenizer(line);
            wordCount += st.countTokens();
        }
        return wordCount;
    }

}
