package com.zac.contentconsumer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.zac.contentconsumer.cms.CmsContent;
import com.zac.contentconsumer.cms.CmsContentManager;
import com.zac.contentconsumer.cms.ICmsContentManager;

import static com.zac.contentconsumer.CmsActivity.EXTRA_MENU_ID;

public class CmsDetailFragment extends Fragment {

    public CmsDetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        long menuId = args.getLong(EXTRA_MENU_ID);

        ICmsContentManager cmsContentManager = new CmsContentManager(container.getContext());
        CmsContent cmsContent = cmsContentManager.getContentByMenuId(menuId);

        String html = cmsContent.getContent();

        View view = inflater.inflate(R.layout.fragment_cms_detail, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webView1);
        webView.loadData(html, "text/html", "UTF-8");
        return view;
    }

}