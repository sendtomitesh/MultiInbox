package com.MultiInbox;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

public class Utility {

    public static Twitter getTwitterInstance(){
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        Configuration conf = confbuilder
                .setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET)
                .build();
        return new TwitterFactory(conf).getInstance();
    }
    public static String getGmailEmail(Context context)
    {
    	Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
    	Account[] accounts = AccountManager.get(context).getAccounts();
    	for (Account account : accounts) {
    	    if (emailPattern.matcher(account.name).matches()) {
    	        return account.name;
    	        
    	    }
    	}
    	return null;
    }
    
    public static Twitter getTwitterFeedsInstance(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Const.TWITTER_PREF_NAME,0);
        String token = preferences.getString(Const.TWITTER_PREF_KEY_ACCESS_TOKEN,null);
        String secret = preferences.getString(Const.TWITTER_PREF_KEY_ACCESS_TOKEN_SECRET,null);
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        confbuilder.setDebugEnabled(true).setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(secret);
        TwitterFactory tf = new TwitterFactory(confbuilder.build());
        return tf.getInstance();

    }

    public static void saveTwitterPrefrences(SharedPreferences sp,
            String accessToken, String accessTokenSecret) {
        Editor editor = sp.edit();
        
        editor.putString(Const.TWITTER_PREF_KEY_ACCESS_TOKEN,accessToken);
        editor.putString(Const.TWITTER_PREF_KEY_ACCESS_TOKEN_SECRET,accessTokenSecret);
        editor.commit();
        
    }
    
    public static void removeTwitterPrefrences(SharedPreferences sp) {
        Editor editor = sp.edit();
        editor.remove(Const.TWITTER_PREF_KEY_ACCESS_TOKEN);
        editor.remove(Const.TWITTER_PREF_KEY_ACCESS_TOKEN_SECRET);
        editor.commit();
    }

	public final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	public static void saveGmailPrefrences(SharedPreferences sp,
			String username, String password) {
		Editor editor = sp.edit();
		
		editor.putString(Const.GMAIL_ID_KEY,username);
		editor.putString(Const.GMAIL_PASSWORD_KEY,password);
		editor.commit();
	}
	
	public static void removeGmailPrefrences(SharedPreferences sp) {
		Editor editor = sp.edit();
		editor.remove(Const.GMAIL_ID_KEY);
		editor.remove(Const.GMAIL_PASSWORD_KEY);
		editor.commit();
	}
	
	public static void saveHotmailPrefrences(SharedPreferences sp,
            String username, String password) {
        Editor editor = sp.edit();
        
        editor.putString(Const.HOTMAIL_EMAIL_KEY,username);
        editor.putString(Const.HOTMAIL_PASSWORD_KEY,password);
        editor.commit();
    }
    
    public static void removeHotmailPrefrences(SharedPreferences sp) {
        Editor editor = sp.edit();
        editor.remove(Const.HOTMAIL_EMAIL_KEY);
        editor.remove(Const.HOTMAIL_PASSWORD_KEY);
        editor.commit();
    }
	
	
	public static boolean hasConnection(Context cont) {
		ConnectivityManager cm = (ConnectivityManager) cont
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

	
	
	public static void alert(Context context,String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setNeutralButton("OK", null);
        builder.create();
        builder.show();
    }
	
	public static void alert(Context context,String title,String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("OK", null);
        builder.create();
        builder.show();
    }

	public static JSONObject getjsonFromInputStream(InputStream is) {
		String result = "";
		JSONObject jArray = null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			//Log.e("log_tag", "Error converting result " + e.toString());
		}
		try {
			jArray = new JSONObject(result);
		} catch (JSONException e) {
			//Log.e("log_tag", "Error parsing data " + e.toString());
		}

		return jArray;

	}

}
