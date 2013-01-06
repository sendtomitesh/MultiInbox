
package com.MultiInbox;

import java.util.List;

import javax.mail.Message;



import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class InboxActivity extends Activity {

    private TextView textLoading;
    // private EditText textEmail,textPassword,textTo,textSubject,textMessage;
    private ListView listview;
    SharedPreferences gmailPreferences;
    SharedPreferences hotmailPreferences;
    SharedPreferences twitterPreferences;
    Twitter mTwitter;

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
    	menu.add("Add Account");
    	return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	if(item.toString().equals("Exit"))
    	{
    		Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_LONG).show();
    	}
    	else if(item.toString().equals("Contacts"))
    	{
    		Toast.makeText(getApplicationContext(), "Contacts", Toast.LENGTH_LONG).show();
    	}
    	else if(item.toString().equals("Add Account"))
    	{
    		Toast.makeText(getApplicationContext(), "Add Account", Toast.LENGTH_LONG).show();
    	}
    	return super.onOptionsItemSelected(item);
    }
    public void gotoCompose(View v) {
        Intent intent = new Intent(InboxActivity.this, SendMailActivity.class);
        intent.putExtra("inboxLoaded", "Gmail");
        startActivity(intent);
    }

    private void loadInbox() {
        CombineInbox.initalizeList();
        loadTwitter();
         loadGmail();
        //Toast.makeText(getApplicationContext(), "Size : " + CombineInbox.sCombineInboxList.size(),
          //      Toast.LENGTH_SHORT).show();
         loadHotmail();
        
        // }

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

        if ( accessToken != null && accessTokenSecret != null) {
            new RecieveTweets().execute();
        }
    }

    private void loadGmail() {
        String userName = gmailPreferences.getString(Const.GMAIL_ID_KEY, null);
        String userPassword = gmailPreferences.getString(Const.GMAIL_PASSWORD_KEY, null);
        
        if (userName !=null && userPassword != null) {
        	  
            new RecieveGmail().execute(userName, userPassword);
        } 
    }

    private void loadHotmail() {
        String userName =hotmailPreferences.getString(Const.HOTMAIL_EMAIL_KEY, null);
        String userPassword =hotmailPreferences.getString(Const.HOTMAIL_PASSWORD_KEY, null);
        if (userName != null && userPassword !=null) {
            //Toast.makeText(getApplicationContext(), "Authenticated!", Toast.LENGTH_SHORT).show();
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
            // textLoading.setVisibility(View.VISIBLE);
            // textLoading.setText("Loading gmail...");
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
            // textLoading.setVisibility(View.VISIBLE);
            // textLoading.setText("Loading hotmail...");
            Toast.makeText(getApplicationContext(), "Loading hotmail...", Toast.LENGTH_SHORT)
                    .show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(final String... params) {

            messageList = Hotmail.getMails(params[0], params[1]);
            if (messageList.length > 0) {
                return CombineInbox.setHotmailInbox(messageList);
                //return true;
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
        List<twitter4j.Status> tweetsList=null;
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
                tweetsList=mTwitter.getHomeTimeline();
                return CombineInbox.setTwitterInbox(tweetsList);
              //  return true;
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
                Toast.makeText(getApplicationContext(),"this is done", Toast.LENGTH_SHORT)
                        .show();

            } else {
                Toast.makeText(getApplicationContext(), "Unable to load tweets", Toast.LENGTH_SHORT)
                        .show();
            }

            super.onPostExecute(result);
        }

    }

}
