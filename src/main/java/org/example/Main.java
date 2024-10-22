package org.example;

import org.example.container.Containers;
import org.example.dao.AnotherDao;
import org.example.services.ServiceOne;

public class Main {

//    static SearchAnnotationByCustomComponent searchByAnnotation = new SearchAnnotationByCustomComponent();

    public static void main(String[] args) throws Exception {
        Containers containers = new Containers();
        containers.initialize("org.example");

        ServiceOne serviceOne = (ServiceOne) containers.getBean(ServiceOne.class);
        serviceOne.serviceOne();  // Вызов метода ServiceOne

//        AnotherDao anotherDao = (AnotherDao) containers.getBean(AnotherDao.class);
//        System.out.println(anotherDao.getNum());
//        System.out.println(anotherDao.getValue());
//        System.out.println(anotherDao.getOrder());

        // При завершении программы уничтожаем бины
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                containers.destroyBeans();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }

}
