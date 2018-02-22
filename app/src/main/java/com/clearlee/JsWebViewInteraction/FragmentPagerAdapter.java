package com.clearlee.JsWebViewInteraction;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * Created by cy on 2015/12/16.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    public List<Fragment> fragmentList;
    FragmentManager fragmentManager;

    public FragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList){
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.fragmentList = fragmentList;
    }
    /**
     * 得到每个页面
     */
    @Override
    public Fragment getItem(int arg0) {
        return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
    }
    /**
     * 页面的总个数
     */
    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

}
