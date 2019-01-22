package com.esprit.pim.breathlyzerv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        browser.getSettings().setLoadWithOverviewMode(true);

        browser.getSettings().setUseWideViewPort(true);
        browser.loadUrl("http://www.painfreesleep.ca/sites/www.painfreesleep.ca/tirednesstest_window/test.php#");

    }
}
