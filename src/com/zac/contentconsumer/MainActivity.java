package com.zac.contentconsumer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowTitleEnabled(false);

        // TODO remove
        Temporary.recreateCms(getApplicationContext());

        startActivity(new Intent(this, CmsActivity.class));
        finish();
    }

}
