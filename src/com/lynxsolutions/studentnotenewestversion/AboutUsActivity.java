package com.lynxsolutions.studentnotenewestversion;

import com.lynxsolutions.utils.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#343434")));
		// actionBar.setIcon(null);
		actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(true);

		WebView webView = (WebView) findViewById(R.id.webviewCompany);
		WebSettings wbset = webView.getSettings();
		wbset.setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(AboutUsActivity.this, description,
						Toast.LENGTH_SHORT).show();
			}
		});

		webView.loadUrl("http://www.lynxsolutions.eu/");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
