package com.expensify.integration.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationException extends Exception {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public AuthenticationException(String str) {
        super(str);
        logger.error(str);
    }
}
