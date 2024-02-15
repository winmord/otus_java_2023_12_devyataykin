package ru.otus.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.Log;

public class Ioc {
    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {
        throw new UnsupportedOperationException("Do not instantiate this class, use statically.");
    }

    public static Object createClass(Object instance) {
        InvocationHandler handler = new DemoInvocationHandler(instance);
        Class<?> instanceInterface = instance.getClass().getInterfaces()[0];
        return Proxy.newProxyInstance(Ioc.class.getClassLoader(), new Class<?>[] {instanceInterface}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final Object obj;
        private final Set<Method> cachedMethods = new HashSet<>();

        public DemoInvocationHandler(Object obj) {
            this.obj = obj;
            cacheMethods();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            cachedMethods.stream()
                    .filter(m -> method.getName().equals(m.getName())
                            && Arrays.equals(method.getParameterTypes(), m.getParameterTypes()))
                    .forEach(m -> logger.info("executed method: {}, param: {}", method.getName(), args));

            return method.invoke(obj, args);
        }

        private void cacheMethods() {
            for (Method m : obj.getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(Log.class)) {
                    cachedMethods.add(m);
                }
            }
        }
    }
}
