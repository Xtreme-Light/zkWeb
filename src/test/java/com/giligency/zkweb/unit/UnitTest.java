package com.giligency.zkweb.unit;

import com.sun.org.apache.xpath.internal.XPath;
import org.junit.jupiter.api.Test;

public class UnitTest {
    private static String path = "/test/node1/node12";

    public static void main(String[] args) {
        test4Split(path);
    }
    @Test
    public static void test4Split(String path) {
        final int i = path.lastIndexOf("/");
        final String substring = path.substring(0, i);
        if (substring.length() > 0) {
            System.out.println(substring);
            test4Split(substring);
        }
    }
}
