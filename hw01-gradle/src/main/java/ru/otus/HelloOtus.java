package ru.otus;

import com.google.common.math.IntMath;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloOtus {
    public static void main(String[] args) {
        Logger log = Logger.getLogger(HelloOtus.class.getName());
        int result = IntMath.binomial(6, 3);
        log.log(Level.INFO, "{0}", result);
    }
}
