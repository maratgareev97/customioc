package org.example.services;

import org.example.annotation.CustomAutowired;
import org.example.annotation.CustomComponent;
import org.example.annotation.CustomPreDestroy;
import org.example.dao.DAOTest;
import org.example.dao.DAOTestOne;
import org.example.dao.DAOTestTwo;

@CustomComponent
public class ServiceTwoImpl implements ServiceTwo{

    @CustomAutowired
    DAOTestTwo daoTestTwo;

    @Override
    public void serviceTwo() {
        System.out.println("ServiceTwo");
    }
}
