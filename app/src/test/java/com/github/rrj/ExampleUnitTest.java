package com.github.rrj;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);

        String s = "res/mipmap-hdpi-v4/rrj_welcomecome.png";

        String str = s.substring(s.lastIndexOf("/")+1, s.lastIndexOf("."));

        System.out.println(""+str);

    }
}