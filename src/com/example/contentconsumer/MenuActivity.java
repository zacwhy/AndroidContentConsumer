package com.example.contentconsumer;

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
//import android.widget.Toast;

import com.example.contentconsumer.support.ContentMenu;
import com.example.contentconsumer.support.ContentMenuFactory;
import com.example.contentconsumer.support.ContentMenuHelper;
import com.example.contentconsumer.support.ContentMenuManager;

public class MenuActivity extends ListActivity {

	public final static String EXTRA_MENU_ID = "com.example.contentconsumer.MENU_ID";

	private ContentMenu currentMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_menu);
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the message from the intent
		Intent intent = getIntent();
		long menuId = intent.getLongExtra(EXTRA_MENU_ID, 0);
		
		ContentMenuManager menuManager = ContentMenuFactory.getMenuManager(this);
		currentMenu = menuManager.findMenuById(menuId);
		
		if (currentMenu.isRoot()) {
			ContentMenu rootMenu = currentMenu;
			
			ContentMenu firstChild = rootMenu.getFirstChild();
			
			if (firstChild == null) {
				currentMenu = new ContentMenu(0, "No menus", null);
			} else {
				menuId = firstChild.getId();
				currentMenu = menuManager.findMenuById(menuId);
			}
		}
		
		String title = currentMenu.getTitle();
		setTitle(title);
		
		String[] menuTextArray = ContentMenuHelper.getMenuTextArray(currentMenu.getChildren());
		setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_menu, menuTextArray));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Context applicationContext = getApplicationContext();
				
				List<ContentMenu> childMenus = currentMenu.getChildren();
				ContentMenu nextMenu = childMenus.get(position);
				
				Class<?> cls = nextMenu.hasChildren() ? MenuActivity.class : ContentActivity.class;
				
				Intent intent = new Intent(applicationContext, cls);
				intent.putExtra(EXTRA_MENU_ID, nextMenu.getId());
				startActivity(intent);
				
				//String text = "No children";
				//Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public void onBackPressed() {
	//		Intent intent = new Intent(Intent.ACTION_MAIN);
	//		intent.addCategory(Intent.CATEGORY_HOME);
	//		startActivity(intent);
	// }

}
