package org.example.dao;

import org.example.annotation.CustomAutowired;
import org.example.annotation.CustomComponent;
import org.example.annotation.CustomQualifier;

@CustomComponent
@CustomQualifier("daoTestThree")
public class DAOTestThree implements DAOTest {

//    @CustomAutowired
//    private final AnotherDao anotherDao;
//
//    @CustomAutowired
//    public DAOTestThree(AnotherDao anotherDao) {
//        this.anotherDao = anotherDao;
//    }

    @Override
    public void daoTest() {
        System.out.println("DaoTestThree");
    }
}
