package org.example.container;

import org.example.injector.DependencyInjector;
import org.example.scanner.ClassScanner;

public class Containers {

    private final BeanFactory beanFactory = new BeanFactory();
    private final DependencyInjector injector = new DependencyInjector();
    private final ClassScanner scanner = new ClassScanner();

    public void initialize(String basePackage) throws Exception {
        var classNames = scanner.scan(basePackage);

        beanFactory.registerBeans(classNames);

        injector.injectDependencies(beanFactory.getAllBeans());

        System.out.println("Beans initialized: " + beanFactory.getAllBeans());
    }

    public void destroyBeans() throws Exception {
        beanFactory.destroyBeans();
    }

    public Object getBean(Class<?> clazz) {
        return beanFactory.getBean(clazz);
    }
}
