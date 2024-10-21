package org.example.scanner;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ClassScanner {

    private static final Set<String> classNames = new HashSet<>();

    public Set<String> scan(String packageName) throws Exception {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource != null) {
            File directory = new File(resource.getFile());
            listFilesRecursively(directory, packageName);
        } else {
            throw new Exception("Пакет не найден");
        }
        return classNames;
    }

    private void listFilesRecursively(File directory, String packageName) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        listFilesRecursively(file, packageName + "." + file.getName());
                    } else if (file.getName().endsWith(".class")) {
                        classNames.add(packageName + "." + file.getName().replace(".class", ""));
                    }
                }
            }
        }
    }
}
