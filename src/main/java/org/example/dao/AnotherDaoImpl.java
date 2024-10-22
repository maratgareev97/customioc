package org.example.dao;

import org.example.annotation.CustomComponent;
import org.example.annotation.CustomQualifier;

@CustomComponent
@CustomQualifier("anotherDaoImpl")
public class AnotherDaoImpl implements AnotherDao {
    @Override
    public int getNum() {
        return 0;
    }
//    //    @CustomProperty(type = DataType.INTEGER)
//    private int num;
//
//    //    @CustomProperty(type = DataType.STRING)
//    private String value;
//
//    //    @CustomProperty(type = DataType.STRING)
//    private double order;
//
//    @Override
//    public int getNum() {
//        return num;
//    }
//
//    @Override
//    public String getValue() {
//        return value;
//    }
//
//    @Override
//    public double getOrder() {
//        return order;
//    }
}
