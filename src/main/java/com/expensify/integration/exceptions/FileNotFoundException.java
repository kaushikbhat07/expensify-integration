package com.expensify.integration.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileNotFoundException extends Exception {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public FileNotFoundException(String str) {
        super(str);
        logger.error(str);
    }
}
