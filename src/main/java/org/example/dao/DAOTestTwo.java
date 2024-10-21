package org.example.dao;

import org.example.annotation.CustomComponent;
import org.example.annotation.CustomQualifier;

@CustomComponent
@CustomQualifier("daoTestTwo")
public class DAOTestTwo implements DAOTest{
    @Override
    public void daoTest(){
        System.out.println("DaoTestTwo");
    }
}
