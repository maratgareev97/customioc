package org.example.dao;

import org.example.annotation.CustomComponent;
import org.example.annotation.CustomQualifier;

@CustomComponent
@CustomQualifier("daoTestOne")
public class DAOTestOne implements DAOTest{

    @Override
    public void daoTest(){
        System.out.println("DaoTestOne");
    }
}
