package com.expensify.integration.exceptions;

public class FileNotFoundException extends Exception {
    public FileNotFoundException(String str) {
        System.out.println(str);
    }
}
