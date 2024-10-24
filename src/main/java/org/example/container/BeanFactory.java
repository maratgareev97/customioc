package org.example.container;

import org.example.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class BeanFactory {
    private final Map<String, Object> beans = new HashMap<>();
    private final Set<Object> processedBeans = new HashSet<>();
    private Set<String> allClassNames;
    private Properties properties;

    public BeanFactory() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("Свойства успешно загружены");
            } else {
                System.out.println("Файл application.properties не найден");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void registerBeans(Set<String> classNames) throws Exception {
        this.allClassNames = classNames;

        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);

            if (clazz.isAnnotationPresent(CustomComponent.class)) {
                Object instance = createInstance(clazz);

                if (processedBeans.contains(instance)) {
                    continue;
                }

                beans.put(clazz.getName(), instance);

                if (clazz.isAnnotationPresent(CustomQualifier.class)) {
                    CustomQualifier qualifier = clazz.getAnnotation(CustomQualifier.class);
                    beans.put(qualifier.value(), instance);
                }

                for (Class<?> iface : clazz.getInterfaces()) {
                    System.out.println("Регистрируем бин для интерфейса: " + iface.getName() + " с экземпляром: " + instance);
                    beans.put(iface.getName(), instance);
                }

                injectFields(instance);

                invokePostConstruct(instance);

                processedBeans.add(instance);
            }
        }
    }

    private Object createInstance(Class<?> clazz) throws Exception {
        System.out.println("Создаём экземпляр для класса: " + clazz.getName());

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(CustomAutowired.class)) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] dependencies = new Object[parameterTypes.length];

                System.out.println("Конструктор помечен @CustomAutowired: " + constructor.getName());

                for (int i = 0; i < parameterTypes.length; i++) {
                    dependencies[i] = resolveDependency(parameterTypes[i], constructor.getParameterAnnotations()[i]);
                    if (dependencies[i] == null) {
                        System.out.println("Зависимость не найдена для параметра: " + parameterTypes[i].getName());
                        throw new Exception("Зависимость не найдена для " + parameterTypes[i].getName());
                    }
                    else {
                        System.out.println("Зависимость найдена для параметра: " + parameterTypes[i].getName() + ", объект: " + dependencies[i]);
                    }
                }
                return constructor.newInstance(dependencies);
            }
        }
        return clazz.getDeclaredConstructor().newInstance();
    }

    private void injectFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        System.out.println("Инжектируем зависимости для бина: " + clazz.getName());

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(CustomAutowired.class)) {
                field.setAccessible(true);
                Object dependency = resolveDependency(field.getType(), field.getAnnotations());
                if (dependency == null) {
                    throw new Exception("Зависимость для поля не найдена  " + field.getName());
                }
                field.set(instance, dependency);
            }

            if (field.isAnnotationPresent(CustomProperty.class)) {
                field.setAccessible(true);
                CustomProperty customProperty = field.getAnnotation(CustomProperty.class);

                // Формируем ключ для получения значения из файла свойств
                String key = clazz.getSimpleName() + "." + field.getName();
                String value = properties.getProperty(key);

                if (value != null) {
                    // Конвертируем значение в нужный тип
                    Object convertedValue = convertValue(value, customProperty.type());
                    field.set(instance, convertedValue);
                    System.out.println("Установлено значение для поля " + field.getName() + ": " + convertedValue);
                } else {
                    System.out.println("Значение для ключа " + key + " не найдено в файле свойств");
                }
            }
        }
    }

    private Object convertValue(String value, DataType dataType) {
        switch (dataType) {
            case INTEGER:
                return Integer.parseInt(value);
            case DOUBLE:
                return Double.parseDouble(value);
            case STRING:
                return value;
            default:
                return value;
        }
    }

    private Object resolveDependency(Class<?> type, Annotation[] annotations) throws Exception {
        System.out.println("Пытаемся найти бин для типа: " + type.getName());

        String qualifierValue = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof CustomQualifier) {
                qualifierValue = ((CustomQualifier) annotation).value();
                System.out.println("Найдена аннотация @CustomQualifier с значением: " + qualifierValue);
                break;
            }
        }

        Object bean = null;
        if (qualifierValue != null) {
            bean = beans.get(qualifierValue);
            System.out.println(bean != null ? "Бин с квалификатором найден: " + qualifierValue : "Бин с квалификатором не найден: " + qualifierValue);
        } else {
            bean = beans.get(type.getName());
            System.out.println(bean != null ? "Бин по типу найден: " + type.getName() : "Бин по типу не найден: " + type.getName());
        }

        if (bean == null) {
            if (type.isInterface()) {
                for (String className : allClassNames) {
                    Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotation() || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                        continue;
                    }

                    if (type.isAssignableFrom(clazz) && clazz.isAnnotationPresent(CustomComponent.class)) {
                        System.out.println("Нашли реализацию для интерфейса: " + type.getName() + " - " + clazz.getName());
                        bean = createInstance(clazz);
                        registerBean(clazz, bean);
                        break;
                    }
                }
            } else if (type.isAnnotationPresent(CustomComponent.class)) {
                bean = createInstance(type);
                registerBean(type, bean);
            }
        }

        if (bean == null) {
            throw new Exception("Зависимость не найдена для " + type.getName());
        }

        return bean;
    }

    private void registerBean(Class<?> clazz, Object instance) throws Exception {
        if (processedBeans.contains(instance)) {
            return;
        }

        beans.put(clazz.getName(), instance);

        if (clazz.isAnnotationPresent(CustomQualifier.class)) {
            CustomQualifier qualifier = clazz.getAnnotation(CustomQualifier.class);
            beans.put(qualifier.value(), instance);
        }

        for (Class<?> iface : clazz.getInterfaces()) {
            System.out.println("Регистрируем бин для интерфейса: " + iface.getName() + " с экземпляром: " + instance);
            beans.put(iface.getName(), instance);
        }

        injectFields(instance);
        invokePostConstruct(instance);
        processedBeans.add(instance);
    }


    private void invokePostConstruct(Object bean) throws Exception {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(CustomPostConstruct.class)) {
                method.setAccessible(true);
                method.invoke(bean);
            }
        }
    }

    public void destroyBeans() throws Exception {
        for (Object bean : processedBeans) {
            invokePreDestroy(bean);
        }
    }

    private void invokePreDestroy(Object bean) throws Exception {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(CustomPreDestroy.class)) {
                method.setAccessible(true);
                method.invoke(bean);
            }
        }
    }

    public Object getBean(String name) {
        return beans.get(name);
    }

    public Object getBean(Class<?> clazz) {
        return beans.get(clazz.getName());
    }

    public Map<String, Object> getAllBeans() {
        return beans;
    }
}
