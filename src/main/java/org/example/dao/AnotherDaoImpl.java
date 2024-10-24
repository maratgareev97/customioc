package org.example.dao;

import org.example.annotation.CustomComponent;
import org.example.annotation.CustomProperty;
import org.example.annotation.CustomQualifier;
import org.example.annotation.DataType;

@CustomComponent
@CustomQualifier("anotherDaoImpl")
public class AnotherDaoImpl implements AnotherDao {

    @CustomProperty(type = DataType.INTEGER)
    private int num;

    @CustomProperty(type = DataType.STRING)
    private String value;

    @CustomProperty(type = DataType.DOUBLE)
    private double order;

    @Override
    public int getNum() {
        return num;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public double getOrder() {
        return order;
    }
}
