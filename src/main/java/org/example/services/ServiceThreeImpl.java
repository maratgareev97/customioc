package org.example.services;

import org.example.annotation.CustomAutowired;
import org.example.annotation.CustomPostConstruct;
import org.example.annotation.CustomPreDestroy;
import org.example.dao.AnotherDao;

public class ServiceThreeImpl implements ServiceThree {
    @Override
    public void serviceThree() {

    }

    @CustomAutowired
    private AnotherDao anotherDao;

    public void DAOTestThree(AnotherDao anotherDao) {
        this.anotherDao = anotherDao;
    }

    @CustomPostConstruct
    public void init() {
        System.out.println("DAOTestThree initialized");
    }

//    @Override
//    public void daoTest() {
//        System.out.println("DaoTestThree method");
//    }

    @CustomPreDestroy
    public void destroy() {
        System.out.println("DAOTestThree destroyed");
    }
}
