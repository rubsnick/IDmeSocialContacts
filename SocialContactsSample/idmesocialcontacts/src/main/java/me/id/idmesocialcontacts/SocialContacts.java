package me.id.idmesocialcontacts;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by rubenroman on 10/28/14.
 */
public class SocialContacts
{
    private Activity activity;
    private DataRetriever dataRetriever;
    private int RequestType;


    private String apiKey;
    private String secretKey;
    private String callbackUrl;

    /**
     * Constructor used only when to Retrieve Local Contacts
     *
     * @param RequestType   The type of Data to be Requested
     * @param activity      The applications calling activity.
     * @param dataRetriever The DataRetriever interfaced Defined.
     */
    public SocialContacts(int RequestType, Activity activity, DataRetriever dataRetriever)
    {
        this.activity = activity;
        this.RequestType = RequestType;
        this.dataRetriever = dataRetriever;
        apiKey = null;
        secretKey = null;
    }

    /**
     * Default Constructor For Library
     *
     * @param RequestType   The type of Data to be Requested
     * @param activity      The applications calling activity.
     * @param dataRetriever The DataRetriever interfaced Defined.
     */
    public SocialContacts(int RequestType, Activity activity, String apiKey, String secretKey,
                          String callbackUrl, DataRetriever dataRetriever)
    {
        this.activity = activity;
        this.RequestType = RequestType;
        this.dataRetriever = dataRetriever;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.callbackUrl = callbackUrl;
    }

    //Fetch Local Contacts

    /**
     * Called After Creating an Instance of Object to Retrieve Contacts
     */
    public void FetchContacts()
    {
        switch (RequestType)
        {
            case SocialContactsUtil.LOCAL:

                RetrieveLocalContacts retrieveLocalContacts = new RetrieveLocalContacts(activity,
                        dataRetriever);
                retrieveLocalContacts.FetchContacts();
                break;

            case SocialContactsUtil.YAHOO_V1:
            case SocialContactsUtil.YAHOO_V2:
                if (apiKey != null && secretKey != null)
                {
                    Intent webActivity = new Intent(activity, WebActivity.class);
                    webActivity.putExtra(SocialContactsUtil.REQUEST_TYPE, RequestType);
                    webActivity.putExtra(SocialContactsUtil.API_KEY, apiKey);
                    webActivity.putExtra(SocialContactsUtil.SECRET_KEY, secretKey);
                    webActivity.putExtra(SocialContactsUtil.CALLBACK_URL, callbackUrl);
                    activity.startActivityForResult(webActivity, 50820);
                }
                else
                {
                    Log.e("Null Error", "API Key and Secret Key Cannot be Null");
                }
                break;

        }
    }

    /**
     * The method that will be hit to listen for results.
     *
     * @param requestCode The activities request code
     * @param resultCode  The activities results code
     * @param data        The activities Data Intent
     */
    public void onActivityResultsListener(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            DetermineRequestCode(requestCode, data);
        }
        else
        {
            dataRetriever.RetrieveContacts(null);
        }
    }

    /**
     * Determines the request code if it's valid then
     *
     * @param requestCode The activities request code
     * @param data        The activities data intent
     */
    public void DetermineRequestCode(int requestCode, Intent data)
    {
        if (requestCode == 50820)
        {
            ArrayList<HashMap<String, String>> contacts = (ArrayList<HashMap<String, String>>) data.getExtras().get(
                    "Contacts");
            dataRetriever.RetrieveContacts(contacts);
        }
        else
        {
            dataRetriever.RetrieveContacts(null);
        }
    }

}

