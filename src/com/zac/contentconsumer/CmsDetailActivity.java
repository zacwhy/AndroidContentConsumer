package com.zac.contentconsumer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.zac.contentconsumer.cms.CmsContent;
import com.zac.contentconsumer.cms.CmsContentManager;
import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsContentManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

public class CmsDetailActivity extends Activity {

	private long currentMenuParentId;
	//private CmsMenu currentMenu; // improve resource efficiency
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
     	Intent intent = getIntent();
     	long menuId = intent.getLongExtra(CmsListActivity.EXTRA_MENU_ID, 0);
     	
     	Context applicationContext = getApplicationContext();
     	
     	ICmsMenuManager cmsMenuManager = new CmsMenuManager(applicationContext);
		CmsMenu currentMenu = cmsMenuManager.getMenuWithChildrenById(menuId);
		currentMenuParentId = currentMenu.getParentId();
		
		setTitle(currentMenu.getTitle());
        setContentView(R.layout.activity_cms_detail);
        
		ICmsContentManager cmsContentManager = new CmsContentManager(applicationContext);
		CmsContent cmsContent = cmsContentManager.getContentByMenuId(currentMenu.getId());
		
		if (cmsContent != null) {
	 	   	String html = getRenderedHtml(cmsContent.getContent());
	        WebView webView = (WebView) findViewById(R.id.webView1);
	 	   	//webView.getSettings().setJavaScriptEnabled(true);
	 	   	//webView.loadUrl("http://www.google.com");
	 	   	webView.loadData(html, "text/html", "UTF-8");
		}
    }
    
    private static String getRenderedHtml(String content) {
    	String format = "<html><body style=\"color: red;\">%s</body></html>";
 	   	return String.format(format, content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cms_detail, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
        	Intent intent = new Intent(this, CmsListActivity.class);
        	intent.putExtra(CmsListActivity.EXTRA_MENU_ID, currentMenuParentId);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
}
