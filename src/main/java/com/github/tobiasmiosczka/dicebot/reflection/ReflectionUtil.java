package com.github.tobiasmiosczka.dicebot.reflection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtil {

    public static <T> List<Class<? extends T>> getClassesImplementing(String packageName, Class<? extends T> implementedInterface) throws IOException {
        return getClasses(packageName).stream()
                .filter(implementedInterface::isAssignableFrom)
                .map(a -> (Class<? extends T>)a)
                .collect(Collectors.toList());
    }

    public static  List<Class<?>> getClasses(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            dirs.add(new File(resources.nextElement().getFile()));
        }
        return dirs.stream()
                .map(directory -> findClasses(directory, packageName))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        if (!directory.exists()) {
            return new ArrayList<>();
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

}
