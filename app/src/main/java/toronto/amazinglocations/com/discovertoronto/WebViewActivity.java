package toronto.amazinglocations.com.discovertoronto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
    }

    protected void onResume() {
        super.onResume();

        String url = getIntent().getExtras().getString("url");

        WebView pointOfInterestWebView = (WebView)findViewById(R.id.pointOfInterestWebView);
        pointOfInterestWebView.setWebViewClient(new WebViewClient());
        pointOfInterestWebView.loadUrl(url);

        Button mapButton = (Button)findViewById(R.id.mapButton);
        mapButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent mapIntent = new Intent(this, MapsActivity.class);

        mapIntent.putExtras(getIntent().getExtras());

        startActivity(mapIntent);
    }
}
