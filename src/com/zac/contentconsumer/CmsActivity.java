package com.zac.contentconsumer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

import java.util.List;

public class CmsActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final String EXTRA_MENU_ID = "com.zac.contentconsumer.MENU_ID";

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int ID_MENU_ROOT = 1;

    private List<CmsMenu> mSiblingMenus;
    private CmsMenu mCurrentMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        long menuId = intent.getLongExtra(EXTRA_MENU_ID, 0);

        if (menuId == 0) {
            menuId = getRootCmsMenu().getId();
        }

        mSiblingMenus = getCmsMenuManager().getSiblingMenusById(menuId, true);
        int position = CmsMenuHelper.getPosition(menuId, mSiblingMenus);
        mCurrentMenu = mSiblingMenus.get(position);

        setContentView(R.layout.activity_cms);
        final ActionBar actionBar = getActionBar();

        if (!mCurrentMenu.isRoot()) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        boolean showChildrenAsTabs = false;
        if (mCurrentMenu.getId() == 12) showChildrenAsTabs = true; // TODO remove

        if (showChildrenAsTabs) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        } else if (mSiblingMenus.size() > 1) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        }

        switch (actionBar.getNavigationMode()) {
            case ActionBar.NAVIGATION_MODE_STANDARD:
                setTitle(mCurrentMenu.getTitle());
                loadCmsListFragment(mCurrentMenu.getId());
                break;

            case ActionBar.NAVIGATION_MODE_LIST:
                actionBar.setDisplayShowTitleEnabled(false);
                SpinnerAdapter spinnerAdapter = getSpinnerAdapter(mSiblingMenus, this);
                actionBar.setListNavigationCallbacks(spinnerAdapter, getOnNavigationListener());
                actionBar.setSelectedNavigationItem(position);
                loadCmsListFragment(mCurrentMenu.getId());
                break;

            case ActionBar.NAVIGATION_MODE_TABS:
                setTitle(mCurrentMenu.getTitle());
                List<CmsMenu> children = getCmsMenuManager().getMenusByParentId(mCurrentMenu.getId());
                mCurrentMenu.setChildren(children);

                for (CmsMenu cmsMenu : mCurrentMenu.getChildren()) {
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
        if (!mCurrentMenu.isRoot()) {
            String title = getRootCmsMenu().getTitle();
            MenuItem menuItem = menu.add(Menu.NONE, ID_MENU_ROOT, Menu.NONE, title);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

            case ID_MENU_ROOT:
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

    private static SpinnerAdapter getSpinnerAdapter(List<CmsMenu> menus, Context context) {
        String[] menuTitleArray = CmsMenuHelper.getMenuTitleArray(menus);
        return new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1,
                menuTitleArray);
    }

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
        return getCmsMenuManager().getRootMenuWithChildren(); // TODO no need children
    }

    private ICmsMenuManager getCmsMenuManager() {
        return new CmsMenuManager(getApplicationContext());
    }

}
