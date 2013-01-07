
package com.MultiInbox;

import java.util.List;

import javax.mail.Message;

import com.MultiInbox.CombineInbox.InboxType;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxActivity extends Activity {

    @SuppressWarnings("unused")
    private TextView textLoading;
    // private EditText textEmail,textPassword,textTo,textSubject,textMessage;
    private ListView listview;
    SharedPreferences gmailPreferences;
    SharedPreferences hotmailPreferences;
    SharedPreferences twitterPreferences;
    Twitter mTwitter;
    public RequestToken mRequestToken;
    List<String> accounts = null;
    
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        initializeGlobals();
        loadInbox();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add("Exit");
        menu.add("Contacts");
        menu.add("Compose");
        menu.add("Add Account");
        menu.add("Logout all");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.toString().equals("Exit"))
        {
            finish();
        }
        else if (item.toString().equals("Contacts"))
        {
            Intent intent = new Intent(InboxActivity.this,ContactActivity.class);
            startActivity(intent);
        }
        else if(item.toString().equals("Compose"))
        {
        	movetoCompose();
        }
        else if (item.toString().equals("Add Account"))
        {
            openDialog();
        }
        else if(item.toString().equals("Logout all"))
        {
        	Utility.logoutAllAccount(getApplicationContext());
        	
        	
        	Toast.makeText(getApplicationContext(), "Logout succesffull", Toast.LENGTH_LONG).show();
        	Intent i = new Intent();
        	i.setClass(getApplicationContext(), MultiLoginActivity.class);
        	startActivity(i);
        	finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog() {
        final CharSequence[] items = Utility.getLoginList(this).toArray(
                new CharSequence[Utility.getLoginList(this).size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add accounts");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Hotmail Login"))
                {
                   showAlert(CombineInbox.InboxType.HOTMAIL);
                }

                if (items[item].equals("Twitter Login"))
                {
                    loginTwitter();
                }

                if (items[item].equals("Existing Gmail"))
                {
                    showAlert(CombineInbox.InboxType.GMAIL);
                }
                if(items[item].equals("FB Login"))
                {
                	Intent i = new Intent();
                	i.setClass(getApplicationContext(), FBLogin.class);
                	startActivity(i);
                	finish();
                }
            }
        }).create().show();
    }

    public void gotoCompose(View v) {
        movetoCompose();
    }
    private void movetoCompose()
    {
    	Intent intent = new Intent(InboxActivity.this, SendMailActivity.class);
        intent.putExtra("inboxLoaded", "Gmail");
        startActivity(intent);
    }

    private void loadInbox() {
        CombineInbox.initalizeList();
        loadTwitter();
        loadGmail();
        loadHotmail();
        com.facebook.Session s= com.facebook.Session.getActiveSession();
        if(s.isOpened())getFBFeeds(s);

    }

    private void updateAdapter()
    {
        if (CombineInbox.sCombineInboxList.size() > 0) {
            CombineInboxAdapter adapter = new CombineInboxAdapter(getApplicationContext(),
                    CombineInbox.sCombineInboxList);
            listview.setAdapter(adapter);
        }
    }

    private void loadTwitter() {
        String accessToken = twitterPreferences
                .getString(Const.TWITTER_PREF_KEY_ACCESS_TOKEN, null);
        String accessTokenSecret = twitterPreferences
                .getString(Const.TWITTER_PREF_KEY_ACCESS_TOKEN_SECRET, null);

        if (accessToken != null && accessTokenSecret != null) {
            new RecieveTweets().execute();
        }
        
    }

    private void loadGmail() {
        String userName = gmailPreferences.getString(Const.GMAIL_ID_KEY, null);
        String userPassword = gmailPreferences.getString(Const.GMAIL_PASSWORD_KEY, null);

        if (userName != null && userPassword != null) {

            new RecieveGmail().execute(userName, userPassword);
        }
        
    }

    private void loadHotmail() {
        String userName = hotmailPreferences.getString(Const.HOTMAIL_EMAIL_KEY, null);
        String userPassword = hotmailPreferences.getString(Const.HOTMAIL_PASSWORD_KEY, null);
        if (userName != null && userPassword != null) {
            // Toast.makeText(getApplicationContext(), "Authenticated!",
            // Toast.LENGTH_SHORT).show();
            new RecieveHotmail().execute(userName, userPassword);
        } 
    }

    private void initializeGlobals() {
        // Get prefrences
        gmailPreferences = getSharedPreferences(Const.GMAIL_PREF_NAME, 0);
        hotmailPreferences = getSharedPreferences(Const.HOTMAIL_PREF_NAME, 0);
        twitterPreferences = getSharedPreferences(Const.TWITTER_PREF_NAME, 0);

        textLoading = (TextView) findViewById(R.id.text_loading);
        listview = (ListView) findViewById(R.id.listview_inbox);
        
    }

    public class RecieveGmail extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            
            Toast.makeText(getApplicationContext(), "Loading gmail...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            Message[] messageList = Gmail.getMails(params[0], params[1]);
            if (messageList != null) {
                return CombineInbox.setGmailInbox(messageList);
            }
            else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (result) {
                // textLoading.setVisibility(View.GONE);
                // textLoading.setText("");
                updateAdapter();
                Toast.makeText(getApplicationContext(), "Gmail Loaded", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Mail not recieved!", Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(result);
        }
    }

    public class RecieveHotmail extends AsyncTask<String, Integer, Boolean> {

        Message[] messageList = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "Loading hotmail...", Toast.LENGTH_SHORT)
                    .show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(final String... params) {

            messageList = Hotmail.getMails(params[0], params[1]);
            if (messageList.length > 0) {
                return CombineInbox.setHotmailInbox(messageList);
                // return true;
            }
            else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (result) {
                // textLoading.setVisibility(View.GONE);
                // textLoading.setText("");
                updateAdapter();
                Toast.makeText(getApplicationContext(), "Hotmail loaded!", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Mail not recieved!", Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(result);
        }
    }

    public class RecieveTweets extends
            AsyncTask<String, Integer, Boolean> {
        List<twitter4j.Status> tweetsList = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // textLoading.setVisibility(View.VISIBLE);
            // textLoading.setText("Loading tweets...");
            Toast.makeText(getApplicationContext(), "Loading tweets...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                mTwitter = Utility.getTwitterFeedsInstance(InboxActivity.this);
                tweetsList = mTwitter.getHomeTimeline();
                return CombineInbox.setTwitterInbox(tweetsList);
                // return true;
            } catch (TwitterException e) {
                return false;
            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (result) {
                // textLoading.setVisibility(View.INVISIBLE);
                // textLoading.setText("");
                updateAdapter();
                Toast.makeText(getApplicationContext(), "this is done", Toast.LENGTH_SHORT)
                        .show();

            } else {
                Toast.makeText(getApplicationContext(), "Unable to load tweets", Toast.LENGTH_SHORT)
                        .show();
            }

            super.onPostExecute(result);
        }

    }
    
    
    //Login code starts here

    public void showAlert(final InboxType  mailtype)
    {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView =inflater.inflate(R.layout.login_view, null);
       final EditText textEmail = (EditText)dialogView.findViewById(R.id.test_email);
       final EditText textPassword = (EditText)dialogView.findViewById(R.id.text_password);
        if(mailtype == CombineInbox.InboxType.GMAIL)
        {
            textEmail.setText(Utility.getGmailEmail(getApplicationContext()));
            textPassword.requestFocus();
        }
        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
        builder.setMessage("Login with "+mailtype.toString())
                .setView(dialogView)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        
                        String email = textEmail.getText().toString();
                        String password = textPassword.getText().toString();
                        if(emailPassValidity(email, password));
                        {
                            if(mailtype == CombineInbox.InboxType.GMAIL){
                                new GmailLogin().execute(email,password);
                            }
                            else{
                                new HotmailLogin().execute(email,password);
                            }
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
    
    public class GmailLogin extends AsyncTask<String, Integer, Boolean> {
        String username;
        String password;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "Executing Gmail login..", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            username = params[0];
            password = params[1];
            return Gmail.doGmailLogin(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (result) {
                SharedPreferences gmailPreference = getSharedPreferences(Const.GMAIL_PREF_NAME,0);
                Utility.saveGmailPrefrences(gmailPreference, username,password);
                Toast.makeText(getApplicationContext(), "Gmail Login success!", Toast.LENGTH_LONG).show();
                loadGmail();
            }
            else {

                Toast.makeText(getApplicationContext(), "Invalid login. Please try again",
                        Toast.LENGTH_LONG).show();

            }
            super.onPostExecute(result);
        }

    }

    public class HotmailLogin extends AsyncTask<String, Integer, Boolean> {
        String username;
        String password;
        
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "Executing Hotmail login..", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            username = params[0];
            password = params[1];
            return Hotmail.doHotmailLogin(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
           
            if (result) {
                SharedPreferences hotmailPreference = getSharedPreferences(Const.HOTMAIL_PREF_NAME,0);
                Utility.saveHotmailPrefrences(hotmailPreference,username,password);
                Toast.makeText(getApplicationContext(), "Hotmail Login success!", Toast.LENGTH_LONG).show();
                loadHotmail();
            }
            else {

                Toast.makeText(getApplicationContext(), "Invalid Hotmail login. Please try again",
                        Toast.LENGTH_LONG).show();

            }
            super.onPostExecute(result);
        }

    }
    
    
    //twitter login
    
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
		        Intent intent = new Intent(InboxActivity.this,TwitterLogin.class);
		        intent.putExtra(Const.TWITTER_AUTH_URL,
		                    result);
		        startActivityForResult(intent, 0);
				} 
				
			};
			}.execute();
    	
    	
    	
        
    }
 
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
             new RecieveTweets().execute();
         }
         super.onPostExecute(result);
     }
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


//fb

	private void getFBFeeds(final Session session) {
    // Make an API call to get user data and define a 
    // new callback to handle the response.
	
	String fqlQuery = "SELECT post_id,description FROM stream WHERE filter_key in (SELECT filter_key FROM stream_filter WHERE uid = me() AND type = 'newsfeed')";
        Bundle params = new Bundle();
        params.putString("q", fqlQuery);
        
        Request request = new Request(session,
            "/fql",                         
            params,                         
            HttpMethod.GET,                 
            new Request.Callback(){       
        		
                public void onCompleted(Response response) {
                	
                	extractFeeds(response);
                	
                  
                }                  
        }); 
        Request.executeBatchAsync(request);                
	
} 

private void extractFeeds(Response s)
{
	 GraphObject graphObject =s.getGraphObject();
	 if (graphObject != null)
     {
         if (graphObject.getProperty("data") != null)
         {
             
                String json = graphObject.getProperty("data").toString();
                CombineInbox.setFBInbox(json);
                updateAdapter();
                 
         }
     }
}


    


}
