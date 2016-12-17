package com.sale.pomocnikzarezije;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sale on 18.10.2016..
 */
public class Rezije extends PDF417 {

    private int id;
    protected float amount;
    private Date datePayed;
    private int categoryId;
    protected String categoryName;
    private Date dateCreated;
    private Date dateEdited;

    public int getId()
    {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String name)
    {
        this.categoryName = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDatePayed() {
        return datePayed;
    }

    public void setDatePayed(Date datePayed) {
        this.datePayed = datePayed;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateEdited() {
        return dateEdited;
    }

    public void setDateEdited(Date dateEdited) {
        this.dateEdited = dateEdited;
    }

}
