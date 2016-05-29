/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import toronto.amazinglocations.com.discovertoronto.R;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {
    private ShimmerView mRightShimmerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
    }

    protected void onResume() {
        super.onResume();

        // Keeping the screen on.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRightShimmerView = (ShimmerView)findViewById(R.id.rightShimmerView);
        mRightShimmerView.startAnimation();

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
    }

    public void onClick(View v) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtras(getIntent().getExtras());
        startActivity(mapIntent);
    }
}
