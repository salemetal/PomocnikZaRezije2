package com.sale.pomocnikzarezije;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Sale on 19.10.2016..
 */
public class AdapterPager extends FragmentStatePagerAdapter {
    int numOfTabs;

    public AdapterPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.numOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TabHome();
            case 1:
                return new TabMjesecno();
            case 2:
                return new TabGodisnje();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}