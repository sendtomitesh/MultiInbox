
package com.MultiInbox;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendMailActivity extends Activity {

    private TextView textHello;
    private EditText textTo,textSubject,textMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeGlobals();
        final Button send = (Button) this.findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = getIntent();
                if(intent.hasExtra("inboxLoaded")){
                    String inboxLoaded = intent.getStringExtra("inboxLoaded");
                    if(inboxLoaded.equals("Gmail")){
                        sendByGmail();
                    }
                    if(inboxLoaded.equals("Hotmail")){
                        sendByHotmail();
                    }
                }
            }
        });

    }

    public void sendByGmail(){
        SharedPreferences preferences = getSharedPreferences(Const.GMAIL_PREF_NAME,0);
        String email = preferences.getString(Const.GMAIL_ID_KEY,null);
        String password = preferences.getString(Const.GMAIL_PASSWORD_KEY,null);
        String emailTo = textTo.getText().toString();
        String subject = textSubject.getText().toString();
        String message = textMessage.getText().toString();
        
        if(emailTo.length() <= 0 || subject.length() <= 0 || message.length() <= 0){
            Toast.makeText(getApplicationContext(), "Please fill all data",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(!Utility.isValidEmail(emailTo)){
                Toast.makeText(getApplicationContext(), "Email not valid",Toast.LENGTH_SHORT).show();
                return;
            }
        }
 
        new SendMail().execute(email,password,emailTo,subject,message);
    }
    
    public void sendByHotmail(){
        SharedPreferences preferences = getSharedPreferences(Const.HOTMAIL_PREF_NAME,0);
        String email = preferences.getString(Const.HOTMAIL_EMAIL_KEY,null);
        String password = preferences.getString(Const.HOTMAIL_PASSWORD_KEY,null);
        String emailTo = textTo.getText().toString();
        String subject = textSubject.getText().toString();
        String message = textMessage.getText().toString();
 
        if(emailTo.length() <= 0 || subject.length() <= 0 || message.length() <= 0){
            Toast.makeText(getApplicationContext(), "Please fill all data",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(!Utility.isValidEmail(emailTo)){
                Toast.makeText(getApplicationContext(), "Email not valid!",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        new SendHotmailMail().execute(email,password,emailTo,subject,message);
    }

    private void initializeGlobals(){
        textHello = (TextView)findViewById(R.id.text_hello);
        textTo  = (EditText)findViewById(R.id.text_to);
        textSubject  = (EditText)findViewById(R.id.text_subject);
        textMessage  = (EditText)findViewById(R.id.text_message);
        textHello.setVisibility(View.INVISIBLE);
        
   }
    
    public void gotoInbox(View v){
      finish();   
    }
    
   public class SendMail extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            textHello.setVisibility(View.VISIBLE);
            textHello.setText("Sending mail please wait...");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {   
         
                Gmail gmail = new Gmail(params[0],params[1]);
                String[] to = new String[1];
                to[0] = params[2];
                gmail.setTo(to);
                gmail.setFrom(params[0]);
                gmail.setSubject(params[3]);
                gmail.setBody(params[4]);
               return gmail.send();
                
            } catch (Exception e) {   
                Log.e("SendMail", e.getMessage(), e);   
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            textHello.setVisibility(View.INVISIBLE);
            if(result){
                Toast.makeText(getApplicationContext(), "Mail sent!",Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Mail not sent!",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
        
    }
   
   
   public class SendHotmailMail extends AsyncTask<String,Integer,Boolean>{

       @Override
       protected void onPreExecute() {
           // TODO Auto-generated method stub
           textHello.setVisibility(View.VISIBLE);
           textHello.setText("Sending mail please wait...");
           super.onPreExecute();
       }
       @Override
       protected Boolean doInBackground(String... params) {
           // TODO Auto-generated method stub
           try {   
              
               Hotmail hotmail = new Hotmail(params[0],params[1]);
               String[] to = new String[1];
               to[0] = params[2];
               hotmail.setTo(to);
               hotmail.setFrom(params[0]);
               hotmail.setSubject(params[3]);
               hotmail.setBody(params[4]);
              return hotmail.send();
               
           } catch (Exception e) {   
               Log.e("SendMail", e.getMessage(), e);   
           }
           return null;
       }
       
       @Override
       protected void onPostExecute(Boolean result) {
           // TODO Auto-generated method stub
           textHello.setVisibility(View.INVISIBLE);
           if(result){
               Toast.makeText(getApplicationContext(), "Mail sent!",Toast.LENGTH_SHORT).show();
               finish();
           }
           else{
               Toast.makeText(getApplicationContext(), "Mail not sent!",Toast.LENGTH_SHORT).show();
           }
           super.onPostExecute(result);
       }
       
   }

}
