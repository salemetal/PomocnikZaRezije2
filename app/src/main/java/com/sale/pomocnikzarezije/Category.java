package com.sale.pomocnikzarezije;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sale on 10.4.2016..
 */
public class Category {

    private int id;
    private String name;
    private boolean isPayedThisMonth;

    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPayedThisMonth() {
        return isPayedThisMonth;
    }

    public void setPayedThisMonth(boolean payedThisMonth) {
        isPayedThisMonth = payedThisMonth;
    }

    public String getName() {
        return name;
    }
    public String toString()
    {
        return name;
    }
}
