package com.zac.contentconsumer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SpinnerAdapter;
import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

import java.util.Arrays;
import java.util.List;

public class CmsActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final String EXTRA_MENU_ID = "com.zac.contentconsumer.MENU_ID";

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private List<CmsMenu> mSiblingMenus;
    private CmsMenu mCurrentMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cms);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        long menuId = intent.getLongExtra(EXTRA_MENU_ID, 0);

        int position;

        if (menuId == 0) {
            mSiblingMenus = Arrays.asList(getRootCmsMenu());
            position = 0;
        } else {
            mSiblingMenus = getCmsMenuManager().getSiblingMenusById(menuId, true, true);
            position = CmsMenuHelper.getPosition(menuId, mSiblingMenus);
        }

        CmsMenu currentMenu = mSiblingMenus.get(position);
        mCurrentMenu = currentMenu;

        final ActionBar actionBar = getActionBar();

        if (!currentMenu.isRoot()) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        boolean showChildrenAsTabs = false;
        if (currentMenu.getId() == 12) showChildrenAsTabs = true; // TODO remove

        if (showChildrenAsTabs) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        } else if (mSiblingMenus.size() > 1) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        }

        switch (actionBar.getNavigationMode()) {
            case ActionBar.NAVIGATION_MODE_STANDARD:
                setTitle(currentMenu.getTitle());
                loadFragment(currentMenu);
                //loadCmsListFragment(currentMenu.getId());
                break;

            case ActionBar.NAVIGATION_MODE_LIST:
                actionBar.setDisplayShowTitleEnabled(false);

                String[] menuTitleArray = CmsMenuHelper.getMenuTitleArray(mSiblingMenus);
                SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                        android.R.id.text1, menuTitleArray);

                actionBar.setListNavigationCallbacks(spinnerAdapter, getOnNavigationListener());
                actionBar.setSelectedNavigationItem(position);
                loadFragment(currentMenu);
                //loadCmsListFragment(currentMenu.getId());
                break;

            case ActionBar.NAVIGATION_MODE_TABS:
                setTitle(currentMenu.getTitle());
                List<CmsMenu> children = getCmsMenuManager().getMenusByParentId(currentMenu.getId());
                currentMenu.setChildren(children);

                for (CmsMenu cmsMenu : children) {
                    String text = cmsMenu.getTitle();
                    actionBar.addTab(actionBar.newTab().setText(text).setTabListener(this));
                }
                break;
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            int selectedNavigationItem = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
            getActionBar().setSelectedNavigationItem(selectedNavigationItem);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cms, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        if (mCurrentMenu.isRoot()) {
            menu.removeItem(R.id.menu_root);
        } else {
            MenuItem menuItem = menu.findItem(R.id.menu_root);
            menuItem.setTitle(getRootCmsMenu().getTitle());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = getCmsListActivityIntent(mCurrentMenu.getParentId());
                NavUtils.navigateUpTo(this, parentIntent);
                return true;

            case R.id.menu_root:
                Intent rootIntent = getCmsListActivityIntent(getRootCmsMenu().getId());
                startActivity(rootIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        CmsMenu cmsMenu = mCurrentMenu.getChildren().get(tab.getPosition());
        loadFragment(cmsMenu);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    //
    // Helpers

    private ActionBar.OnNavigationListener getOnNavigationListener() {
        return new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                CmsMenu cmsMenu = mSiblingMenus.get(position);
                loadFragment(cmsMenu);
                return true;
            }
        };
    }

    private void loadFragment(CmsMenu cmsMenu) {
        if (cmsMenu.hasChild()) {
            loadCmsListFragment(cmsMenu.getId());
        } else {
            loadCmsDetailFragment(cmsMenu.getId());
        }
    }

    private void loadCmsListFragment(long menuId) {
        replaceFragment(new CmsListFragment(), menuId);
    }

    private void loadCmsDetailFragment(long menuId) {
        replaceFragment(new CmsDetailFragment(), menuId);
    }

    private void replaceFragment(Fragment fragment, long menuId) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_MENU_ID, menuId);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private Intent getCmsListActivityIntent(long menuId) {
        Intent intent = new Intent(getApplicationContext(), CmsActivity.class);
        intent.putExtra(EXTRA_MENU_ID, menuId);
        return intent;
    }

    private CmsMenu getRootCmsMenu() {
        return getCmsMenuManager().getRootMenu();
    }

    private ICmsMenuManager getCmsMenuManager() {
        return new CmsMenuManager(getApplicationContext());
    }

}
