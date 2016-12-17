package com.sale.pomocnikzarezije;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sale on 20.11.2016..
 */
public class RezijeYear extends Rezije{

    private List<Integer> monthsPayed = new ArrayList<>();

    public void addToMonthsPayed(int i) {
        monthsPayed.add(i);
    }

    public List<Integer> getMonthsPayed() {
        return monthsPayed;
    }
}
