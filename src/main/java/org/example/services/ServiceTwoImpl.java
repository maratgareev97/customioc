package org.example.services;

import org.example.annotation.CustomAutowired;
import org.example.annotation.CustomComponent;
import org.example.dao.DAOTest;
import org.example.dao.DAOTestOne;

@CustomComponent
public class ServiceTwoImpl implements ServiceTwo{

    @CustomAutowired
    DAOTestOne daoTestOne;

    @Override
    public void serviceTwo() {
        System.out.println("ServiceTwo");
    }
}
