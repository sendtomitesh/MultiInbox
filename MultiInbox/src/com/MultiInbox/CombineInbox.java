
package com.MultiInbox;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.util.Log;

public class CombineInbox {
    private String mTitle = "";
    private String mDescription = "";
    private InboxType mType;
    public static ArrayList<CombineInbox> sCombineInboxList;

    public CombineInbox() {
        // TODO Auto-generated constructor stub
    }

    public CombineInbox(String title, String description, InboxType type) {
        this.mTitle = title;
        this.mDescription = description;
        this.mType = type;
    }
    
    
    public static enum InboxType{HOTMAIL,GMAIL,Twitter,FB}
        
        
    // public getters
    public String getTitle() {
        return this.mTitle;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public InboxType getType() {
        return this.mType;
    }

    public static ArrayList<CombineInbox> getCombinedInbox() {
        return sCombineInboxList;
    }

    // public setters
    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setType(InboxType type) {
        this.mType = type;
    }

    public static void initalizeList() {
        sCombineInboxList = new ArrayList<CombineInbox>();
    }
    
    

    public static boolean setGmailInbox(Message[] messageList) {

        if (messageList.length > 0)
        {
            String title = "";
            String description = "";
            for (int i = 0; i < messageList.length; i++) {
                try {

                    title = messageList[i].getSubject().toString();
                    description = InternetAddress.toString(messageList[i].getFrom());
                    CombineInbox inbox = new CombineInbox(title, description,InboxType.GMAIL);
                    sCombineInboxList.add(inbox);

                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    Log.d("GMAIL-COMBINE","Error in combining GAMIL");
                    return false;
                    //e.printStackTrace();
                }
            }
            return true;
        }
        else{
            return false;
        }
        
    }

    public static boolean setHotmailInbox(Message[] messageList) {

        if (messageList.length > 0)
        {
            String title = "";
            String description = "";
            for (int i = 0; i < messageList.length; i++) {
                try {

                    title = messageList[i].getSubject().toString();
                    description = InternetAddress.toString(messageList[i].getFrom());
                    CombineInbox inbox = new CombineInbox(title, description,InboxType.HOTMAIL);
                    sCombineInboxList.add(inbox);

                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    Log.d("HOTMAILL-COMBINE","Error in combining HOTMAIL");
                    return false;
                    //e.printStackTrace();
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean setTwitterInbox(List<twitter4j.Status> messageList) {

        if (messageList.size() > 0)
        {
            String title = "";
            String description = "";
            
            for (int i = 1; i <= messageList.size(); i++) {
                try {
                    if(messageList.get(i).getUser().getName().equals(null) || messageList.get(i).getText().equals(null) )
                        continue;
                    title =messageList.get(i).getUser().getName().toString();
                    description = messageList.get(i).getText().toString();
                    CombineInbox inbox = new CombineInbox(title, description,InboxType.Twitter);
                    sCombineInboxList.add(inbox);

                } catch (Exception e) {
                    Log.d("TWITTER-COMBINE","Error in combining TWITTER");
                    continue;
                }
            }
            return true;
        }
        else{
            return false;
        }
    }
    
    public static boolean setFBInbox(String json)
    {
    	
		String feed;
		try {
            

            JSONArray jsonNArray = new JSONArray(json);
            for (int i = 0; i < jsonNArray.length(); i++) {

                JSONObject jsonObject = jsonNArray.getJSONObject(i);
                feed=jsonObject.getString("description");
                CombineInbox inbox = new CombineInbox(feed, "",InboxType.FB);
                sCombineInboxList.add(inbox);
               // Toast.makeText(getApplicationContext(), jsonObject.getString("description"), Toast.LENGTH_LONG).show();
            }
            return true;
        }
        catch(JSONException e)
        {
       	 e.printStackTrace();
       	 return false;
        }
		
    }

}
