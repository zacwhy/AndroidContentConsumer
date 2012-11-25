package com.zac.contentconsumer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

import java.util.List;

public class CmsSearchActivity extends Activity {

    private TextView mTextView;
    private ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cms_search);

        mTextView = (TextView) findViewById(R.id.text);
        mListView = (ListView) findViewById(R.id.list);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        //if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
            //Intent wordIntent = new Intent(this, MainActivity.class);
            //wordIntent.setData(intent.getData());
            //startActivity(wordIntent);
        //} else
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    private void showResults(String query) {
        mTextView.setText(getString(R.string.search_results, new Object[] {query}));

        final List<CmsMenu> mCmsMenus = getCmsMenuManager().searchMenusByTitle(query, -1, -1, true, true);

        String[] menuTextArray = CmsMenuHelper.getMenuTitleArray(mCmsMenus);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menuTextArray);
        mListView.setAdapter(adapter);

        // Define the on-click listener for the list items
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CmsMenu cmsMenu = mCmsMenus.get(position);
                Intent intent = new Intent(getApplicationContext(), CmsActivity.class);
                intent.putExtra(CmsActivity.EXTRA_MENU_ID, cmsMenu.getId());
                startActivity(intent);
            }
        });
    }

    private ICmsMenuManager getCmsMenuManager() {
        return new CmsMenuManager(getApplicationContext());
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.search:
//                onSearchRequested();
//                return true;
//            default:
//                return false;
//        }
//    }

}