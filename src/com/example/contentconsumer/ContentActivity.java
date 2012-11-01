package com.example.contentconsumer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

import com.example.contentconsumer.support.ContentMenu;
import com.example.contentconsumer.support.ContentMenuFactory;
import com.example.contentconsumer.support.ContentMenuManager;

public class ContentActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the message from the intent
     	Intent intent = getIntent();
     	long menuId = intent.getLongExtra(MenuActivity.EXTRA_MENU_ID, 0);
     	
     	ContentMenuManager menuManager = ContentMenuFactory.getMenuManager(this);
     	ContentMenu currentMenu = menuManager.findMenuById(menuId);
		
		setTitle(currentMenu.getTitle());
        setContentView(R.layout.activity_content);
        
        WebView webView = (WebView) findViewById(R.id.webView1);
 	   	webView.getSettings().setJavaScriptEnabled(true);
 	   	//webView.loadUrl("http://www.google.com");
  
 	   	String content = currentMenu.getTitle();
 	   	String customHtml = String.format("<html><body style=\"color: red;\">%s</body></html>", content);
 	   	webView.loadData(customHtml, "text/html", "UTF-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_content, menu);
        return true;
    }
}
