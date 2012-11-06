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

import com.zac.contentconsumer.support.CmsMenu;
import com.zac.contentconsumer.support.CmsMenuHelper;
import com.zac.contentconsumer.support.CmsMenuManager;
import com.zac.contentconsumer.support.ICmsMenuManager;

public class CmsListActivity extends ListActivity {

	public final static String EXTRA_MENU_ID = "com.zac.contentconsumer.MENU_ID";

	private final static String EMPTY_LIST = "Empty List";
	
	private CmsMenu currentMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		long menuId = intent.getLongExtra(EXTRA_MENU_ID, 0);
		
		ICmsMenuManager cmsMenuManager = new CmsMenuManager(this);
		
		if (menuId == 0) {
			currentMenu = cmsMenuManager.getRootMenuWithChildren();
		} else {
			currentMenu = cmsMenuManager.getMenuWithChildrenById(menuId);
		}
		
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
		if (currentMenu.getParentId() != 0) {
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
				handleOnItemClick(parent, view, position, id);				
			}
		});
	}
	
	private void handleOnItemClick(AdapterView<?> parent, View view, int position, long id) {
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, CmsListActivity.class);
	        	intent.putExtra(CmsListActivity.EXTRA_MENU_ID, currentMenu.getParentId());
	            NavUtils.navigateUpTo(this, intent);
				//NavUtils.navigateUpFromSameTask(this);
				return true;
				
			case R.id.menu_home:
				Toast.makeText(getApplicationContext(), "Go home", Toast.LENGTH_SHORT).show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
