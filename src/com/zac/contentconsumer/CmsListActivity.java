package com.zac.contentconsumer;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuHelper;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

public class CmsListActivity extends ListActivity {

    public final static String EXTRA_MENU_ID = "com.zac.contentconsumer.MENU_ID";
    private final static String EMPTY_LIST = "Empty List";
    private CmsMenu currentMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        long menuId = intent.getLongExtra(EXTRA_MENU_ID, 0);

        currentMenu = getCmsMenuById(menuId);

        if (currentMenu == null) {
            setUpEmptyList();
        } else {
            setUpNonEmptyList();
        }
    }

    private void setUpEmptyList() {
        setTitle(EMPTY_LIST);
        Toast.makeText(getApplicationContext(), EMPTY_LIST, Toast.LENGTH_LONG).show();
    }

    private void setUpNonEmptyList() {
        if (!isRootMenu()) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(currentMenu.getTitle());
        String[] menuTextArray = CmsMenuHelper.getMenuTitleArray(currentMenu.getChildren());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_cms_list, menuTextArray);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnItemClick(position);
            }
        });
    }

    private void handleOnItemClick(int position) {
        List<CmsMenu> childMenus = currentMenu.getChildren();
        CmsMenu nextMenu = childMenus.get(position);

        ICmsMenuManager cmsMenuManager = new CmsMenuManager(this);
        boolean hasChild = cmsMenuManager.hasChild(nextMenu.getId());

        Class<?> cls = hasChild ? CmsListActivity.class : CmsDetailActivity.class;
        Context applicationContext = getApplicationContext();
        Intent intent = new Intent(applicationContext, cls);
        intent.putExtra(EXTRA_MENU_ID, nextMenu.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cms_list, menu);

        if (isRootMenu()) {
            menu.removeItem(R.id.menu_home);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = getCmsListActivityIntent(currentMenu.getParentId());
                NavUtils.navigateUpTo(this, intent1);
                //NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.menu_home:
                CmsMenu nextMenu = getRootCmsMenu();
                Intent intent2 = getCmsListActivityIntent(nextMenu.getId());
                startActivity(intent2);
                //Toast.makeText(getApplicationContext(), "Go home", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isRootMenu() {
        return currentMenu.getParentId() == 0;
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
        intent.putExtra(EXTRA_MENU_ID, menuId);
        return intent;
    }

}
