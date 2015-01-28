package me.id.idmesocialcontacts.YahooContacts;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

/**
 * Created by rubenroman on 10/27/14.
 */
public class RetrieveYahooV2Contacts
{
    private String ApiKey;
    private String SecretKey;
    private final String CONTACTS_ENDPOINT = "https://social.yahooapis.com";
    private String callbackUrl;//https://id.me
    private WebView webView;
    private Activity activity;
    private String OAUTH_URL = "https://api.login.yahoo.com/oauth2/request_auth?client_id=apiKey&redirect_uri=callbackUrl&response_type=token";

    /**
     * Default Constructor
     *
     * @param webView     Web view of calling activity
     * @param activity    Calling activity
     * @param ApiKey      Api Key given by Yahoo
     * @param SecretKey   Secret Key given by Yahoo
     * @param callbackUrl Your call back URL
     */
    public RetrieveYahooV2Contacts(WebView webView, Activity activity, String ApiKey,
                                   String SecretKey, String callbackUrl)
    {
        this.webView = webView;
        this.activity = activity;
        this.ApiKey = ApiKey;
        this.SecretKey = SecretKey;
        this.callbackUrl = callbackUrl;
        OAUTH_URL = OAUTH_URL.replace("apiKey", this.ApiKey);
        OAUTH_URL = OAUTH_URL.replace("callbackUrl", this.callbackUrl);
        SetUpWebView();
    }


    //region AuthenticationLogic

    //region WebViewLogic

    //Code Configures WebView Setting and Places All Necessary Calls  where they belong.

    /**
     * Configures Web View
     */
    private void SetUpWebView()
    {
        webView.clearCache(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(mWebViewClient);

    }

    /**
     * Custom Web View Client to extract data
     */
    private WebViewClient mWebViewClient = new WebViewClient()
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {

            super.onPageStarted(view, url, favicon);
            view.setVisibility(View.INVISIBLE);

        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            if ((url != null) && (url.contains("access_token")))
            {

                //We Get The Verification Code so that the User doesn't have to Copy and Paste it.
                view.stopLoading();


                String header = "Bearer " + view.getUrl().substring(view.getUrl().indexOf("=") + 1,
                        view.getUrl().indexOf("&"));
                String yahooGuid = view.getUrl().substring(view.getUrl().indexOf(
                        "xoauth_yahoo_guid=") + "xoauth_yahoo_guid=".length());
                DoRetroFitCall(yahooGuid, header);

            }
            else
            {
                view.setVisibility(View.VISIBLE);
                view.stopLoading();
            }


        }
    };
    //endregion

    /**
     * Executes Retrofit call
     *
     * @param yahooGuid Yahoo Guid extract from URL
     * @param header    Authorization Header
     */
    private void DoRetroFitCall(String yahooGuid, String header)
    {
        Gson MerchantParser = new GsonBuilder().registerTypeAdapter(HashMap.class,
                new YahooContactDeserializer()).create();

        GsonConverter MerchantConverter = new GsonConverter(MerchantParser);

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(CONTACTS_ENDPOINT).setConverter(
                MerchantConverter).build();

        YahooAPI yahooAPI = adapter.create(YahooAPI.class);

        yahooAPI.GetContacts(header, yahooGuid, new Callback<JsonObject>()
        {
            @Override public void success(JsonObject result, retrofit.client.Response response)
            {
                JsonArray jsonArray = result.get("contacts").getAsJsonObject().get(
                        "contact").getAsJsonArray();

                YahooExtractContactFromResponse yahooExtractContactFromResponse = new YahooExtractContactFromResponse();
                yahooExtractContactFromResponse.getYahooContactsNameAndEmail(jsonArray, activity);
            }

            @Override public void failure(RetrofitError error)
            {
                Log.d("RetroFit Failure: ", error.getMessage());
            }
        });

    }

    //region OAuthLogic
    //Builds OauthURL and Loads it in webView

    /**
     * Fetches Data
     */
    public void FetchData()
    {
        webView.loadUrl(OAUTH_URL);
    }


    //endregion

    //endregion
}
