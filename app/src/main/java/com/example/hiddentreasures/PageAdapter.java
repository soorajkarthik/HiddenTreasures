package com.example.hiddentreasures;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    //Fields
    private int numOfTabs;

    /**
     * Constructor
     *
     * @param fragmentManager FragmentManager used by the current activity
     * @param numOfTabs       number of tabs in TabLayout
     */
    public PageAdapter(FragmentManager fragmentManager, int numOfTabs) {

        super(fragmentManager);
        this.numOfTabs = numOfTabs;
    }

    /**
     * @param position current position of ViewPager that PageAdapter is attached to
     * @return Fragment based on position
     */
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new Fragment();

            case 1:
                return new Fragment();

            case 2:
                return new Fragment();

            case 3:
                return new Fragment();

            case 4:
                return new Fragment();

            default:
                return null;
        }
    }

    /**
     * @return number of tabs in TabLayout
     */
    @Override
    public int getCount() {

        return numOfTabs;
    }
}