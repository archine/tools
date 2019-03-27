package com.gjing.handle;

import com.gjing.annotation.NonNull;

/**
 * @author Archine
 **/
public class TestDemo {

    @NonNull
    private String param1;
    private String param2;


    private static void test(String param1, String param2) {
        System.out.println(param1 + param2);
    }
}
