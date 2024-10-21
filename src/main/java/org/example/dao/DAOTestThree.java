package org.example.dao;

import org.example.annotation.CustomComponent;
import org.example.annotation.CustomQualifier;

@CustomComponent
@CustomQualifier("daoTestThree")
public class DAOTestThree implements DAOTest{

    @Override
    public void daoTest() {
        System.out.println("DaoTestThree");
    }
}
