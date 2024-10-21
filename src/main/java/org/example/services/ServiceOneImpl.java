package org.example.services;

import org.example.annotation.*;
import org.example.dao.DAOTest;
import org.example.dao.DAOTestOne;
import org.example.dao.DAOTestTwo;

@CustomComponent
public class ServiceOneImpl implements ServiceOne {

    private final DAOTest daoTestOne;
    private final DAOTest daoTestTwo;

    @CustomAutowired
    public ServiceOneImpl(@CustomQualifier("daoTestOne") DAOTest daoTestOne,
                          @CustomQualifier("daoTestTwo") DAOTest daoTestTwo) {
        this.daoTestOne = daoTestOne;
        this.daoTestTwo = daoTestTwo;
    }

    @CustomPostConstruct
    public void init() {
        System.out.println("ServiceOne initialized");
    }

    @CustomPreDestroy
    public void destroy() {
        System.out.println("ServiceOne destroyed");
    }

    @Override
    public void serviceOne() {
        System.out.println("ServiceOne");
        daoTestOne.daoTest();
        daoTestTwo.daoTest();
    }
}
