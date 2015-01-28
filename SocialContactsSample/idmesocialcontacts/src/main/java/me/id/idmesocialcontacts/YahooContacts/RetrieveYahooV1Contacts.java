package me.id.idmesocialcontacts.YahooContacts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rubenroman on 10/27/14.
 */
public class RetrieveYahooV1Contacts
{
    private ArrayList<HashMap<String, String>> contacts;
    private String ApiKey;
    private String SecretKey;
    private final String CONTACTS_ENDPOINT = "https://social.yahooapis.com/v1/user/guid/contacts?format=json";
    private final String CALLBACK_URL = "https://api.login.yahoo.com/oauth/v2/request_auth";
    private WebView webView;
    private OAuthService service;
    private Token token;
    private String VerificationCode;
    private Activity activity;


    /**
     * Default Constructor
     *
     * @param webView   The activity's web view
     * @param activity  The calling activity
     * @param ApiKey    The api key given by Yahoo.
     * @param SecretKey The secret Key give by Yahoo
     */
    public RetrieveYahooV1Contacts(WebView webView, Activity activity, String ApiKey,
                                   String SecretKey)
    {
        this.webView = webView;
        this.activity = activity;
        this.ApiKey = ApiKey;
        this.SecretKey = SecretKey;
        contacts = new ArrayList<HashMap<String, String>>();
        SetUpWebView();
    }


    //region AuthenticationLogic

    //region WebViewLogic

    /**
     * Code Configures web view Setting and Places All Necessary Calls  where they belong.
     */
    private void SetUpWebView()
    {
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(mWebViewClient);
        webView.addJavascriptInterface(new JavaScriptInterface(activity.getApplicationContext()),
                "android");

    }

    /**
     * Custom Web View Client to grab Short Code from Web  Page
     */
    private WebViewClient mWebViewClient = new WebViewClient()
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {

            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            if ((url != null) && (url.equals(CALLBACK_URL)))
            {
                //We Get The Verification Code so that the User doesn't have to Copy and Paste it.
                view.stopLoading();
                view.loadUrl(
                        "javascript:void(android.getYahooVerificationCode(document.getElementById('shortCode').innerHTML))");
                view.setVisibility(View.INVISIBLE);
            }
            ScrollWebViewForYahoo();
        }
    };
    //endregion

    //region OAuthLogic
    //Builds OauthURL and Loads it in webView

    /**
     * Fetch data  from URL
     */
    public void FetchData()
    {
        (new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                service = new ServiceBuilder().provider(YahooApi.class).apiKey(ApiKey).apiSecret(
                        SecretKey).build();
                token = service.getRequestToken();
                return service.getAuthorizationUrl(token);
            }

            @Override
            protected void onPostExecute(String url)
            {
                webView.loadUrl(url);
            }
        }).execute();
    }

    /**
     * Signs into Yahoo and request contacts
     */
    private void Verify()
    {
        Verifier verifier = new Verifier(VerificationCode);
        Token accessToken = service.getAccessToken(token, verifier);

        String guid = getYahooGUIDFromRawToken(accessToken.getRawResponse());
        String Endpoint = CONTACTS_ENDPOINT.replace("guid", guid);
        OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, Endpoint);
        service.signRequest(accessToken, oAuthRequest);


        Response response = oAuthRequest.send();

        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(response.getBody());
        YahooExtractContactFromResponse yahooExtractContactFromResponse = new YahooExtractContactFromResponse();

        yahooExtractContactFromResponse.getYahooContactsNameAndEmail(
                object.get("contacts").getAsJsonObject().get("contact").getAsJsonArray(), activity);
    }

    /**
     * Gets Verification code and executes it from Javascript
     */
    private class JavaScriptInterface
    {
        Context mContext;

        JavaScriptInterface(Context c)
        {
            mContext = c;
        }

        @JavascriptInterface
        public void getYahooVerificationCode(String code)
        {
            VerificationCode = code;
            Verify();
        }
    }
    //endregion

    //endregion

    //region WebViewDisplay

    /**
     * Calculates Screen Dimension
     */

    private double CalculateScreenDimensions()
    {
        WindowManager windowManager = (WindowManager) activity.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float screenWidth = size.x;
        float screenHeight = size.y;
        return CalculateScreenInches(screenWidth, screenHeight);
    }

    /**
     * Calculates Screen inches
     *
     * @param screenWidth  Screen Width
     * @param screenHeight Screen Height
     * @return Screen inches in double
     */
    private double CalculateScreenInches(float screenWidth, float screenHeight)
    {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) activity.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        double x = Math.pow(screenWidth / dm.xdpi, 2);
        double y = Math.pow(screenHeight / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }


    /**
     * Move Scroll by Y depending on screen size
     */
    private void ScrollWebViewForYahoo()
    {
        double screenInches = CalculateScreenDimensions();
        int DpValue;
        if (Math.round(screenInches) > 5)
        {
            DpValue = 225;

        }
        else
        {
            DpValue = 120;
        }
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DpValue,
                activity.getApplicationContext().getResources().getDisplayMetrics());
        webView.setScrollX(px);
    }
    //endregion


    //region ExtractData

    /**
     * Gets yahoo Guid from raw token to use in request.
     *
     * @param rawToken Raw response token
     * @return Guid to be used in request
     */
    private String getYahooGUIDFromRawToken(String rawToken)
    {
        int indexofGuid = rawToken.lastIndexOf("xoauth_yahoo_guid");
        rawToken = rawToken.substring(indexofGuid);

        return rawToken.substring(rawToken.indexOf("=") + 1);
    }

    //endregion
}
