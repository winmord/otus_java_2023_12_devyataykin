package ru.otus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);
    private static Integer passedTests = 0;
    private static Integer failedTests = 0;

    private TestRunner() {
        // empty constructor
    }

    public static void run(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor();

            List<Method> beforeAnnotatedMethods = getMethodsAnnotatedWith(Before.class, clazz);
            List<Method> testAnnotatedMethods = getMethodsAnnotatedWith(Test.class, clazz);
            List<Method> afterAnnotatedMethods = getMethodsAnnotatedWith(After.class, clazz);

            for (Method testAnnotatedMethod : testAnnotatedMethods) {
                runAllTests(constructor, beforeAnnotatedMethods, testAnnotatedMethod, afterAnnotatedMethods);
            }

            logger.info("Total tests count: {}", testAnnotatedMethods.size());
            logger.info("Passed: {}", passedTests);
            logger.info("Failed: {}", failedTests);
        } catch (ClassNotFoundException | NoSuchMethodException exc) {
            logger.error("Failed to start tests. Cause: {}", exc.getCause().toString());
        }
    }

    private static void runAllTests(
            Constructor<?> constructor,
            List<Method> beforeAnnotatedMethods,
            Method testAnnotatedMethod,
            List<Method> afterAnnotatedMethods) {
        try {
            runTest(constructor, beforeAnnotatedMethods, testAnnotatedMethod, afterAnnotatedMethods);
            ++passedTests;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error(
                    "Test {} failed. Cause: {}",
                    testAnnotatedMethod.getName(),
                    e.getCause().toString());
            ++failedTests;
        }
    }

    private static void runTest(
            Constructor<?> constructor,
            List<Method> beforeAnnotatedMethods,
            Method testAnnotatedMethod,
            List<Method> afterAnnotatedMethods)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        logger.info("Test name: {}", testAnnotatedMethod.getName());

        Object object = constructor.newInstance();
        for (Method beforeAnnotatedMethod : beforeAnnotatedMethods) {
            logger.info("Before method running: {}", beforeAnnotatedMethod.getName());
            beforeAnnotatedMethod.invoke(object);
        }

        logger.info("Test running");
        testAnnotatedMethod.invoke(object);

        for (Method afterAnnotatedMethod : afterAnnotatedMethods) {
            logger.info("After method running: {}", afterAnnotatedMethod.getName());
            afterAnnotatedMethod.invoke(object);
        }

        logger.info("Test passed");
    }

    public static List<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotation, Class<?> clazz) {
        List<Method> methods = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                methods.add(method);
            }
        }

        return methods;
    }
}
