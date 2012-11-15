package com.zac.contentconsumer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.zac.contentconsumer.cms.CmsContent;
import com.zac.contentconsumer.cms.CmsContentManager;
import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsContentManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

import java.util.List;

public class CmsDetailFragmentActivity extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    List<CmsMenu> menus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        long menuId = intent.getLongExtra(CmsListActivity.EXTRA_MENU_ID, 0);

        if (menuId == 0) {
            //throw new Exception("menuId should not be 0");
        }

        ICmsMenuManager cmsMenuManager = new CmsMenuManager(getApplicationContext());
        menus = cmsMenuManager.getSiblingMenusById(menuId);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_tabs_swipe);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                CharSequence title = mSectionsPagerAdapter.getPageTitle(position);
                setTitle(title);
            }
        });

        int position = getPosition(menuId, menus);

        if (position > 0) {
            mViewPager.setCurrentItem(position);
        }

        if (position == 0) {
            CharSequence title = mSectionsPagerAdapter.getPageTitle(position);
            setTitle(title);
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
                CmsMenu currentMenu = menus.get(position);
                Intent intent = getCmsListActivityIntent(currentMenu.getParentId());
                NavUtils.navigateUpTo(this, intent);
                return true;

            case R.id.menu_home:
                CmsMenu nextMenu = getRootCmsMenu();
                Intent intent2 = getCmsListActivityIntent(nextMenu.getId());
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private CmsMenu getRootCmsMenu() {
        return getCmsMenuById(0);
    }

    private CmsMenu getCmsMenuById(long menuId) {
        ICmsMenuManager cmsMenuManager = new CmsMenuManager(getApplicationContext());

        if (menuId == 0) {
            return cmsMenuManager.getRootMenuWithChildren();
        }

        return cmsMenuManager.getMenuWithChildrenById(menuId);
    }

    private Intent getCmsListActivityIntent(long menuId) {
        Intent intent = new Intent(getApplicationContext(), CmsListActivity.class);
        intent.putExtra(CmsListActivity.EXTRA_MENU_ID, menuId);
        return intent;
    }

    private static int getPosition(long menuId, List<CmsMenu> menus) {
        for (int i = 0; i < menus.size(); i++) {
            CmsMenu menu = menus.get(i);
            if (menu.getId() == menuId) {
                return i;
            }
        }
        return -1;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            CmsMenu menu = getCmsMenu(i);
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putLong(DummySectionFragment.ARG_MENU_ID, menu.getId());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return menus.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CmsMenu menu = getCmsMenu(position);
            return menu.getTitle();
        }

        private CmsMenu getCmsMenu(int position) {
            return menus.get(position);
        }
    }

    public static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {

        }

        public static final String ARG_MENU_ID = "menu_id";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            long menuId = args.getLong(ARG_MENU_ID);

            Context context = container.getContext();
            ICmsContentManager cmsContentManager = new CmsContentManager(context);
            CmsContent cmsContent = cmsContentManager.getContentByMenuId(menuId);

            String html = cmsContent.getContent();

            View view = inflater.inflate(R.layout.activity_cms_detail, container, false);
            WebView webView = (WebView) view.findViewById(R.id.webView1);
            webView.loadData(html, "text/html", "UTF-8");
            return view;
        }

    }
}
