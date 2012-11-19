package com.zac.contentconsumer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.zac.contentconsumer.cms.CmsMenu;

import java.util.List;

public class CmsDetailPagerAdapter extends FragmentPagerAdapter {

    public CmsDetailPagerAdapter(FragmentManager fragmentManager, List<CmsMenu> cmsMenus) {
        super(fragmentManager);
        this.cmsMenus = cmsMenus;
    }

    private List<CmsMenu> cmsMenus;

    @Override
    public Fragment getItem(int i) {
        CmsMenu cmsMenu = getCmsMenu(i);
        Fragment fragment = new CmsDetailFragment();
        Bundle args = new Bundle();
        args.putLong(CmsActivity.EXTRA_MENU_ID, cmsMenu.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return cmsMenus.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CmsMenu cmsMenu = getCmsMenu(position);
        return cmsMenu.getTitle();
    }

    private CmsMenu getCmsMenu(int position) {
        return cmsMenus.get(position);
    }

}
