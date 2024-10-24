package org.example;

import org.example.container.Containers;
import org.example.dao.AnotherDao;
import org.example.services.ServiceOne;

public class Main {
    public static void main(String[] args) throws Exception {
        Containers containers = new Containers();
        containers.initialize("org.example");

        ServiceOne serviceOne = (ServiceOne) containers.getBean(ServiceOne.class);
        serviceOne.serviceOne();

        AnotherDao anotherDao = (AnotherDao) containers.getBean(AnotherDao.class);
        System.out.println("Num: " + anotherDao.getNum());
        System.out.println("Value: " + anotherDao.getValue());
        System.out.println("Order: " + anotherDao.getOrder());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                containers.destroyBeans();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }

}
