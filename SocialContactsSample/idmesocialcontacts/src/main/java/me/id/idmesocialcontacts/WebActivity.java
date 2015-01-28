package me.id.idmesocialcontacts;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import me.id.idmesocialcontacts.YahooContacts.RetrieveYahooV1Contacts;
import me.id.idmesocialcontacts.YahooContacts.RetrieveYahooV2Contacts;


public class WebActivity extends Activity
{
    private String ApiKey;
    private String SecretKey;
    private String callBackUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        int requestType = getIntent().getIntExtra(SocialContactsUtil.REQUEST_TYPE, 0);
        ApiKey = getIntent().getStringExtra(SocialContactsUtil.API_KEY);
        SecretKey = getIntent().getStringExtra(SocialContactsUtil.SECRET_KEY);
        callBackUrl = getIntent().getStringExtra(SocialContactsUtil.CALLBACK_URL);

        DetermineAPIRequest(requestType);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Determines which site will be requested.
     *
     * @param RequestType The request type defined by user.
     */
    private void DetermineAPIRequest(int RequestType)
    {
        WebView webView = (WebView) findViewById(R.id.webView);
        switch (RequestType)
        {
            case SocialContactsUtil.YAHOO_V1:
                RetrieveYahooV1Contacts retrieveYahooContactsV1 = new RetrieveYahooV1Contacts(
                        webView, this, ApiKey, SecretKey);


                retrieveYahooContactsV1.FetchData();
                break;
            case SocialContactsUtil.YAHOO_V2:
                RetrieveYahooV2Contacts retrieveYahooContactsV2 = new RetrieveYahooV2Contacts(
                        webView, this, ApiKey, SecretKey, callBackUrl);


                retrieveYahooContactsV2.FetchData();
                break;
        }

    }
}
