/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;

import toronto.amazinglocations.com.discovertoronto.R;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {
    private RightShimmerView mRightShimmerView;
    private LeftShimmerView mLeftShimmerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
    }

    protected void onResume() {
        super.onResume();

        // Keeping the screen on.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRightShimmerView = (RightShimmerView)findViewById(R.id.rightShimmerView);
        mRightShimmerView.startAnimation();

        mLeftShimmerView = (LeftShimmerView)findViewById(R.id.leftShimmerView);
        mLeftShimmerView.startAnimation();

        String url = getIntent().getExtras().getString("url");
        WebView pointOfInterestWebView = (WebView)findViewById(R.id.pointOfInterestWebView);
        pointOfInterestWebView.setWebViewClient(new WebViewClient());
        pointOfInterestWebView.loadUrl(url);

        Button mapButton = (Button)findViewById(R.id.mapButton);
        mapButton.setOnClickListener(this);
    }

    protected void onPause() {
        super.onPause();

        if(mRightShimmerView != null) {
            mRightShimmerView.stopAnimation();
        }
        if(mLeftShimmerView != null) {
            mLeftShimmerView.stopAnimation();
        }
    }

    public void onClick(View v) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtras(getIntent().getExtras());
        startActivity(mapIntent);
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            ProgressBar pBar = (ProgressBar) findViewById(R.id.webProgressbar);

            pBar.setVisibility(View.GONE);
        }
    }
}
