package com.zac.contentconsumer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // todo remove
        Temporary.recreateMenus(getApplicationContext());
                
        startActivity(new Intent(this, CmsListActivity.class));
		finish();
    }

}
