package com.zac.contentconsumer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.zac.contentconsumer.cms.CmsMenu;
import com.zac.contentconsumer.cms.CmsMenuManager;
import com.zac.contentconsumer.cms.ICmsMenuManager;

import static com.zac.contentconsumer.CmsActivity.EXTRA_MENU_ID;

public class CmsListFragment extends ListFragment {

    public CmsListFragment() {

    }

    private CmsMenu mCurrentMenu;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        long menuId = args.getLong(EXTRA_MENU_ID, 0);

        if (menuId == 0) {
            // TODO not supposed to
        }

        mCurrentMenu = getCmsMenuManager().getMenuWithChildrenById(menuId);

        String[] menuTextArray = CmsMenuHelper.getMenuTitleArray(mCurrentMenu.getChildren());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, menuTextArray);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CmsMenu cmsMenu = mCurrentMenu.getChildren().get(position);
        Class<?> cls = cmsMenu.hasChild() ? CmsActivity.class : CmsDetailFragmentActivity.class;
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra(EXTRA_MENU_ID, cmsMenu.getId());
        startActivity(intent);
    }

    private ICmsMenuManager getCmsMenuManager() {
        return new CmsMenuManager(getApplicationContext());
    }

    private Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

}