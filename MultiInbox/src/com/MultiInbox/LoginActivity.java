
package com.MultiInbox;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import com.MultiInbox.CombineInbox.InboxType;




import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private TextView textLoading;
    private EditText textEmail, textPassword;
    public static boolean checkLogin = false;
    SharedPreferences gmailPreference;
    SharedPreferences hotmailPreference;
    public Twitter mTwitter;
    public RequestToken mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeGlobals();
        checkPreferences();

    }

    private void checkPreferences() {
        SharedPreferences twitterPreferences = getSharedPreferences(Const.TWITTER_PREF_NAME,0);
        String accessToken = twitterPreferences.getString(Const.TWITTER_PREF_KEY_ACCESS_TOKEN,null);
        
        if(accessToken != null){
            gotoInbox();
        }
        
        String userName = gmailPreference.getString(Const.GMAIL_ID_KEY, null);
        if (userName != null) {
            gotoInbox();
        }
        String hotmailUsername = hotmailPreference.getString(Const.HOTMAIL_EMAIL_KEY,null);
        if(hotmailUsername != null){
            gotoInbox();
        }
    }

    private void initializeGlobals() {
        gmailPreference = getSharedPreferences(Const.GMAIL_PREF_NAME, MODE_PRIVATE);
        hotmailPreference = getSharedPreferences(Const.HOTMAIL_PREF_NAME, MODE_PRIVATE);
        textLoading = (TextView) findViewById(R.id.text_loading);
        
        textLoading.setVisibility(View.GONE);

    }

    
    public void showAlert(final InboxType  mailtype)
    {
    	
    	
    	Toast.makeText(getApplicationContext(), Utility.getGmailEmail(getApplicationContext()), Toast.LENGTH_LONG).show();
    	LayoutInflater inflater = this.getLayoutInflater();
    	final View dialogView =inflater.inflate(R.layout.login_view, null);
    	textEmail = (EditText)dialogView.findViewById(R.id.test_email);
    	textPassword = (EditText)dialogView.findViewById(R.id.text_password);
    	if(mailtype == CombineInbox.InboxType.GMAIL)
    	{
    		textEmail.setText(Utility.getGmailEmail(getApplicationContext()));
    		textPassword.requestFocus();
    	}
        
        
    	AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Login with "+mailtype.toString())
        		.setView(dialogView)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    	
                    	
                    	String email = textEmail.getText().toString();
                        String password = textPassword.getText().toString();
                        if(emailPassValidity(email, password));
                        {
	                  	  	if(mailtype == CombineInbox.InboxType.GMAIL)
	                  	  		gmailLogin(email,password);
	                  	  	else
	               	  			HotmailLogin(email, password);
                        }	  
                  		  
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                  	 
                    }
                });
        
         builder.create();
         builder.show();
    }
    public Boolean emailPassValidity(String email,String password)
    {
    	if (email.length() <= 0 || password.length() <=0) {
            Toast.makeText(getApplicationContext(), "Please fill all data", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Utility.isValidEmail(email)){
            Toast.makeText(getApplicationContext(), "Email not valid!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void GmailLoginClick(View v)
    {
    	showAlert(CombineInbox.InboxType.GMAIL);
    }
    public void HotmailLoginClick(View v)
    {
    	showAlert(CombineInbox.InboxType.HOTMAIL);
    }
    public void TweeterLoginClick(View v)
    {
    	loginTwitter();
    }
    public void facebookLoginClick(View v)
    {
    	Toast.makeText(getApplicationContext(), "facebook login clicked", Toast.LENGTH_LONG).show();
    }
    public void gmailLogin(String uname,String pass) {
    	
             new GmailLogin().execute(uname, pass);
        
    }
    
    public void HotmailLogin(String uname,String pass){
        
        new HotmailLogin().execute(uname, pass);
    }

    public void gotoInbox() {
        Intent intent = new Intent(LoginActivity.this, InboxActivity.class);
        startActivity(intent);
        finish();
    }
    

    public class GmailLogin extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            textLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            Properties props = System.getProperties();
            props.put("mail.imap.host", "imap.gmail.com");
            props.put("mail.imap.user", params[0]);
            // User SSL
            props.put("mail.imap.socketFactory", 993);
            props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.imap.port", 993);
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(params[0], params[1]);
                }

            };
            Session sessioned = Session.getDefaultInstance(props, auth);

            Store store;
            try {
                store = sessioned.getStore("imaps");
                store.connect("smtp.gmail.com", params[0], params[0]);

                Folder inbox = store.getFolder("inbox");

                inbox.open(Folder.READ_WRITE);
                if (inbox.exists()) {
                    // Utility.sGmailMessageList = inbox.getMessages();
                    SharedPreferences gmailPreference = getSharedPreferences(Const.GMAIL_PREF_NAME,
                            MODE_PRIVATE);
                    Utility.saveGmailPrefrences(gmailPreference, params[0], params[1]);
                    return true;
                } else {
                    return false;
                }

            } catch (NoSuchProviderException e) {
                return false;
            } catch (MessagingException e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            textLoading.setVisibility(View.GONE);
            if (result) {
                Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_LONG).show();
                gotoInbox();
            }
            else {

                Toast.makeText(getApplicationContext(), "Invalid login. Please try again",
                        Toast.LENGTH_LONG).show();

            }
            super.onPostExecute(result);
        }

    }

    public class HotmailLogin extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            textLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            
            Properties props = System.getProperties();
            
            props.setProperty("mail.pop3.ssl.enable", "true");
            props.setProperty("mail.pop3s.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.pop3s.socketFactory.fallback", "false");
            props.setProperty("mail.pop3s.port", "995");
            props.setProperty("mail.pop3s.socketFactory.port", "995");
            
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(params[0],params[1]);
                }
                
            };
            
            Session sessioned = Session.getDefaultInstance(props, auth);
            
            Store store;
            try {
                store = sessioned.getStore("pop3s");
                store.connect("pop3.live.com",995,params[0],params[1]);
               
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);
                if(inbox.exists()){
                    SharedPreferences hotmailPreference = getSharedPreferences(Const.HOTMAIL_PREF_NAME,0);
                    Utility.saveHotmailPrefrences(hotmailPreference,params[0],params[1]);
                    return true;
                }
                else{
                    return false;
               }
               
               
            } catch (NoSuchProviderException e) {
                return false;
            } catch (MessagingException e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            textLoading.setVisibility(View.GONE);
            if (result) {
                Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_LONG).show();
                gotoInbox();
            }
            else {

                Toast.makeText(getApplicationContext(), "Invalid login. Please try again",
                        Toast.LENGTH_LONG).show();

            }
            super.onPostExecute(result);
        }

    }
    
    
    public void loginTwitter() {

    	
    	new AsyncTask<String, Integer, String>(){

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				 return getauthURL();
			}
			protected void onPostExecute(String result) 
			{
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				if(result != null)
				{
		        Intent intent = new Intent(LoginActivity.this,TwitterLogin.class);
		        intent.putExtra(Const.TWITTER_AUTH_URL,
		                    result);
		        startActivityForResult(intent, 0);
				} 
				
			};
			}.execute();
    	
    	
    	
        
    }
    
private String getauthURL()
{
	try
	{
	mTwitter = Utility.getTwitterInstance();
    mTwitter.setOAuthAccessToken(null);
    mRequestToken = mTwitter.getOAuthRequestToken(Const.TWITTER_CALLBACK_URL);
    return mRequestToken.getAuthorizationURL();
	}
	catch (TwitterException e) {
    e.printStackTrace();
	}
	return null;
    
}
    
   /* public class TwitterLogins extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... args) {
            loginTwitter();
            return null;
        }
    } */

    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String oauthVerifier = intent.getExtras().getString(
                        Const.TWITTER_OAUTH_VERIFIER);

                new GetTwitterAccessToken().execute(oauthVerifier);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Twitter auth canceled.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    public class GetTwitterAccessToken extends AsyncTask<String, Void, AccessToken> {

        @Override
        protected AccessToken doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(AccessToken result) {
            // TODO Auto-generated method stub
            if (result != null) {
              SharedPreferences  twitterPreference = getSharedPreferences(Const.TWITTER_PREF_NAME,
                        MODE_PRIVATE);
                Utility.saveTwitterPrefrences(twitterPreference, result.getToken(),
                        result.getTokenSecret());
                gotoInbox();
                Toast.makeText(LoginActivity.this, "authorized",
                        Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

}
