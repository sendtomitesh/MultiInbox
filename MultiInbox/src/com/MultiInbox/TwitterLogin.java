package com.MultiInbox;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterLogin extends Activity {
    public static final String TAG = TwitterLogin.class.getSimpleName();

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.twitter_login);
       loadLogin();
       
    }
    
    @SuppressLint("SetJavaScriptEnabled")
	public void loadLogin(){
    	 WebView webView = (WebView) findViewById(R.id.twitterlogin);
         WebSettings webSettings = webView.getSettings();
         webSettings.setJavaScriptEnabled(true);
         webView.setWebViewClient(new WebViewClient() {
             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 boolean result = true;
                 if (url != null && url.startsWith(Const.TWITTER_CALLBACK_URL)) {
                     Uri uri = Uri.parse(url);
                     Log.v(TAG, url);
                     if (uri.getQueryParameter("denied") != null) {
                         setResult(RESULT_CANCELED);
                         finish();
                     } else {
                         String oauthToken = uri.getQueryParameter("oauth_token");
                         String oauthVerifier = uri.getQueryParameter("oauth_verifier");

                         Intent intent = getIntent();
                         intent.putExtra(Const.TWITTER_OAUTH_TOKEN, oauthToken);
                         intent.putExtra(Const.TWITTER_OAUTH_VERIFIER, oauthVerifier);
                         setResult(RESULT_OK, intent);
                         finish();
                     }
                 } else {
                     result = super.shouldOverrideUrlLoading(view, url);
                 }
                 return result;
             }
         });
         webView.loadUrl(this.getIntent().getExtras().getString("auth_url"));
    }
}