package me.id.socialcontactssample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import me.id.idmesocialcontacts.DataRetriever;
import me.id.idmesocialcontacts.SocialContacts;
import me.id.idmesocialcontacts.SocialContactsUtil;


public class MainActivity extends Activity
{
    SocialContacts socialContacts;
    String apiKey = null;
    String secretKey = null;
    String callBackURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         socialContacts = new SocialContacts(SocialContactsUtil.YAHOO_V1, this, apiKey, secretKey,
                 callBackURL, new DataRetriever()
        {
            @Override public void RetrieveContacts(ArrayList<HashMap<String, String>> contacts)
            {
                if (contacts != null)
                {
                    TextView textView = (TextView) findViewById(R.id.txtView);
                    String results = "";
                    for (int i = 0; i < contacts.size(); i++)
                    {
                        results = results + "Name : " + contacts.get(i).get(
                                SocialContactsUtil.NAME) + " Email : " + contacts.get(i).get(
                                SocialContactsUtil.Email) + "\n\n";
                    }
                    textView.setText(results);
                }
            }
        });
        socialContacts.FetchContacts();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        socialContacts.onActivityResultsListener(requestCode, resultCode, data);
    }
}
