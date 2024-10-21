package org.example.injector;

import org.example.annotation.CustomAutowired;
import org.example.annotation.CustomQualifier;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class DependencyInjector {

    public void injectDependencies(Map<String, Object> beans) throws Exception {
        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(CustomAutowired.class)) {
                    field.setAccessible(true);
                    Object dependency = resolveDependency(field, beans);
                    field.set(bean, dependency);
                }
            }
        }
    }

    private Object resolveDependency(Field field, Map<String, Object> beans) {
        // Проверьте аннотацию @CustomQualifier.
        if (field.isAnnotationPresent(CustomQualifier.class)) {
            CustomQualifier qualifier = field.getAnnotation(CustomQualifier.class);
            return beans.get(qualifier.value());
        }
        // В противном случае используйте тип поля
        return beans.get(field.getType().getName());
    }
}