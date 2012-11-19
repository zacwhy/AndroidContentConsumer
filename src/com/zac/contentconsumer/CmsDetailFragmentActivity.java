package com.zac.contentconsumer;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

import java.util.List;

public class CmsDetailFragmentActivity extends FragmentActivity {

    private CmsDetailPagerAdapter mCmsDetailPagerAdapter;
    private ViewPager mViewPager;
    private List<CmsMenu> cmsMenus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        long menuId = intent.getLongExtra(CmsActivity.EXTRA_MENU_ID, 0);

        if (menuId == 0) {
            //throw new Exception("menuId should not be 0");
        }

        cmsMenus = getCmsMenuManager().getSiblingMenusById(menuId, false);
        int position = CmsMenuHelper.getPosition(menuId, cmsMenus);

        setContentView(R.layout.activity_cms_detail_fragment);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (cmsMenus.size() > 1) {
            String[] titleArray = CmsMenuHelper.getMenuTitleArray(cmsMenus);
            SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, titleArray);

            ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int position, long itemId) {
                    mViewPager.setCurrentItem(position);
                    return true;
                }
            };

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(spinnerAdapter, onNavigationListener);
        } else {
            CmsMenu currentMenu = getCmsMenu(position);
            setTitle(currentMenu.getTitle());
        }

        mCmsDetailPagerAdapter = new CmsDetailPagerAdapter(getSupportFragmentManager(), cmsMenus);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCmsDetailPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        if (position > 0) {
            mViewPager.setCurrentItem(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cms_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int position = mViewPager.getCurrentItem();
                CmsMenu currentMenu = cmsMenus.get(position);
                Intent intentParent = getCmsListActivityIntent(currentMenu.getParentId());
                NavUtils.navigateUpTo(this, intentParent);
                return true;

            case R.id.menu_home:
                Intent intentRoot = getCmsListActivityIntent(getRootCmsMenu().getId());
                startActivity(intentRoot);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private CmsMenu getRootCmsMenu() {
        return getCmsMenuManager().getRootMenuWithChildren();
    }

    private ICmsMenuManager getCmsMenuManager() {
        return new CmsMenuManager(getApplicationContext());
    }

    private Intent getCmsListActivityIntent(long menuId) {
        Intent intent = new Intent(getApplicationContext(), CmsActivity.class);
        intent.putExtra(CmsActivity.EXTRA_MENU_ID, menuId);
        return intent;
    }

    private CmsMenu getCmsMenu(int position) {
        return cmsMenus.get(position);
    }

}
