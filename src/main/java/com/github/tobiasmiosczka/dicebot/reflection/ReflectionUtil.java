package com.github.tobiasmiosczka.dicebot.reflection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ReflectionUtil {

    public static <T> List<Class<? extends T>> getClassesImplementing(String packageName, Class<T> implementedInterface) throws IOException {
        List<Class<? extends T>> list = new ArrayList<>();
        for (Class<?> a : getClasses(packageName)) {
            if (implementedInterface.isAssignableFrom(a)) {
                list.add((Class<? extends T>) a);
            }
        }
        return list;
    }

    public static  List<Class<?>> getClasses(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            dirs.add(new File(resources.nextElement().getFile()));
        }
        List<Class<?>> list = new ArrayList<>();
        for (File directory : dirs) {
            list.addAll(findClasses(directory, packageName));
        }
        return list;
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
