package org.example.container;

import org.example.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanFactory {

    // Хранение созданных бинов
    private final Map<String, Object> beans = new HashMap<>();

    // хранения уже обработанных бинов, чтобы избежать повторной обработки
    private final Set<Object> processedBeans = new HashSet<>();

    // Метод для регистрации бинов, который принимает Set строк с именами классов
    public void registerBeans(Set<String> classNames) throws Exception {
        for (String className : classNames) {
            // Загружаем класс по имени
            Class<?> clazz = Class.forName(className);

            // Проверяем, помечен ли класс аннотацией @CustomComponent (является ли он бином)
            if (clazz.isAnnotationPresent(CustomComponent.class)) {
                // Создаем экземпляр класса (бин)
                Object instance = createInstance(clazz);

                // Если бин уже обработан, пропускаем его
                if (processedBeans.contains(instance)) {
                    continue;
                }

                // Сохраняем бин в карту, используя имя класса как ключ
                beans.put(clazz.getName(), instance);

                // Если класс имеет аннотацию @CustomQualifier, сохраняем бин с этим значением как ключом
                if (clazz.isAnnotationPresent(CustomQualifier.class)) {
                    CustomQualifier qualifier = clazz.getAnnotation(CustomQualifier.class);
                    beans.put(qualifier.value(), instance);
                }

                // Сохраняем бин с именами всех интерфейсов, которые он реализует
                for (Class<?> iface : clazz.getInterfaces()) {
                    System.out.println("Регистрируем бин для интерфейса: " + iface.getName() + " с экземпляром: " + instance);
                    beans.put(iface.getName(), instance);
                }

                // Инжектим зависимости в поля, помеченные аннотацией @CustomAutowired
                injectFields(instance);

                // Вызываем методы, помеченные аннотацией @CustomPostConstruct
                invokePostConstruct(instance);

                // Добавляем обработанный бин в список обработанных
                processedBeans.add(instance);
            }
        }
    }

    // Метод для создания экземпляра класса
    private Object createInstance(Class<?> clazz) throws Exception {
        System.out.println("Создаём экземпляр для класса: " + clazz.getName());

        // Ищем конструктор с аннотацией @CustomAutowired
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(CustomAutowired.class)) {
                // Определяем типы параметров конструктора
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] dependencies = new Object[parameterTypes.length];

                System.out.println("Конструктор помечен @CustomAutowired: " + constructor.getName());


                // Разрешаем зависимости для параметров конструктора
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
                // Создаем экземпляр через конструктор с параметрами
                return constructor.newInstance(dependencies);
            }
        }
        // Если нет конструктора с параметрами, используем пустой конструктор
        return clazz.getDeclaredConstructor().newInstance();
    }

    // Метод для инжекции зависимостей в поля объекта
    private void injectFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            // Если поле помечено аннотацией @CustomAutowired
            if (field.isAnnotationPresent(CustomAutowired.class)) {
                field.setAccessible(true); // Делаем поле доступным для изменения
                // Разрешаем зависимость для данного поля
                Object dependency = resolveDependency(field.getType(), field.getAnnotations());
                if (dependency == null) {
                    throw new Exception("Зависимость для поля не найдена  " + field.getName());
                }
                // Устанавливаем зависимость в поле
                field.set(instance, dependency);
            }
        }
    }

    // Метод для разрешения зависимостей (поиск бин-кандидата для инжекции)
    private Object resolveDependency(Class<?> type, Annotation[] annotations) {
        System.out.println("Пытаемся найти бин для типа: " + type.getName());


        // Проверяем наличие аннотации @CustomQualifier
        String qualifierValue = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof CustomQualifier) {
                qualifierValue = ((CustomQualifier) annotation).value();
                System.out.println("Найдена аннотация @CustomQualifier с значением: " + qualifierValue);

                break;
            }
        }

        // Если есть @CustomQualifier, ищем бин с данным квалификатором
        if (qualifierValue != null) {
//            return beans.get(qualifierValue);
            Object bean = beans.get(qualifierValue);
            System.out.println(bean != null ? "Бин с квалификатором найден: " + qualifierValue : "Бин с квалификатором не найден: " + qualifierValue);
            return bean;
        } else {
            // Если квалификатора нет, используем имя класса
//            return beans.get(type.getName());

            Object bean = beans.get(type.getName());
            System.out.println(bean != null ? "Бин по типу найден: " + type.getName() : "Бин по типу не найден: " + type.getName());
            return bean;
        }
    }

    // Метод для вызова методов, помеченных аннотацией @CustomPostConstruct
    private void invokePostConstruct(Object bean) throws Exception {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // Ищем методы с аннотацией @CustomPostConstruct
            if (method.isAnnotationPresent(CustomPostConstruct.class)) {
                method.setAccessible(true); // Делаем метод доступным для вызова
                method.invoke(bean); // Вызываем метод
            }
        }
    }

    // Метод для уничтожения бинов (вызывается при завершении программы)
    public void destroyBeans() throws Exception {
        // Проходим по всем уникальным бинам
        for (Object bean : processedBeans) {
            // Вызываем методы, помеченные аннотацией @CustomPreDestroy
            invokePreDestroy(bean);
        }
    }

    // Метод для вызова методов, помеченных аннотацией @CustomPreDestroy
    private void invokePreDestroy(Object bean) throws Exception {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // Ищем методы с аннотацией @CustomPreDestroy
            if (method.isAnnotationPresent(CustomPreDestroy.class)) {
                method.setAccessible(true); // Делаем метод доступным для вызова
                method.invoke(bean); // Вызываем метод
            }
        }
    }

    // Метод для получения бина по имени
    public Object getBean(String name) {
        return beans.get(name);
    }

    // Метод для получения бина по классу
    public Object getBean(Class<?> clazz) {
        return beans.get(clazz.getName());
    }

    // Метод для получения всех бинов
    public Map<String, Object> getAllBeans() {
        return beans;
    }
}
