package ru.otus.appcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.error.DiException;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        for (Method method : configClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                appComponents.add(method);
            }
        }

        appComponents.sort((Comparator.comparingInt(
                o -> ((Method) o).getDeclaredAnnotation(AppComponent.class).order())));

        Constructor<?> constructor;
        Object configClassInstance;

        try {
            constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            configClassInstance = constructor.newInstance();
        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e) {
            throw new DiException(e.getMessage());
        }

        for (Object object : appComponents) {
            Method method = (Method) object;
            Object[] parameters;
            AppComponent annotation = method.getDeclaredAnnotation(AppComponent.class);

            if (!appComponentsByName.containsKey(annotation.name())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0) {
                    parameters = parameterTypes;
                } else {
                    parameters = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        parameters[i] = getAppComponent(parameterTypes[i]);
                    }
                }

                makeBean(method, configClassInstance, annotation.name(), parameters);
            } else {
                throw new DiException("method duplicates");
            }
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        Object appComponent = null;
        for (Map.Entry<String, Object> entry : appComponentsByName.entrySet()) {
            Object value = entry.getValue();
            if (componentClass.isInstance(value)) {
                if (appComponent != null) {
                    throw new DiException("multiple components for type");
                }
                appComponent = value;
            }
        }
        return (C) Objects.requireNonNull(appComponent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Object appComponent = appComponentsByName.get(componentName);
        return (C) Objects.requireNonNull(appComponent);
    }

    private void makeBean(Method method, Object instance, String name, Object... args) {
        method.setAccessible(true);

        try {
            Object bean = method.invoke(instance, args);
            appComponentsByName.put(name, bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DiException(e.getMessage());
        }
    }
}
