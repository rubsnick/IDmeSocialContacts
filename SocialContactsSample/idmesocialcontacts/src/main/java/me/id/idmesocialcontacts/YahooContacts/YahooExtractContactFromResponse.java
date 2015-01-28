package me.id.idmesocialcontacts.YahooContacts;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import me.id.idmesocialcontacts.SocialContactsUtil;

/**
 * Parses Yahoo Response and Extracts Contacts
 */
class YahooExtractContactFromResponse
{
    private ArrayList<HashMap<String, String>> lstContacts = new ArrayList<HashMap<String, String>>();

    /**
     * Extracts Contacts from Json Result from Yahoo.
     *
     * @param contacts Json array from Yahoo.
     * @param activity The calling activity
     */
    public void getYahooContactsNameAndEmail(JsonArray contacts, Activity activity)
    {
        for (int i = 0; i < contacts.size(); i++)
        {
            JsonArray fields = contacts.get(i).getAsJsonObject().getAsJsonArray("fields");
            ExtractDataFromRequest(fields);
        }
        SortContacts();
        FinishActivity(lstContacts, activity);

    }

    /**
     * Sorts contacts by alphabetical order
     */
    private void SortContacts()
    {
        Collections.sort(lstContacts, new NameComparator());
    }

    /**
     * The Json array parser
     *
     * @param jsonArray Json array to be parsed
     */
    private void ExtractDataFromRequest(JsonArray jsonArray)
    {
        String firstName;
        String lastName;
        String email;
        JsonObject nameObject;
        JsonObject emailObject;
        HashMap contact = new HashMap();


        nameObject = getNameObject(jsonArray);
        emailObject = getEmailObject(jsonArray);
        firstName = getFirstNameFromObject(nameObject);
        lastName = getLastNameFromObject(nameObject);
        email = getEmailFromObject(emailObject);

        if (email != null && firstName != null)
        {
            contact.put(SocialContactsUtil.NAME, firstName + " " + lastName);
            contact.put(SocialContactsUtil.Email, email);
            lstContacts.add(contact);
        }


    }

    /**
     * Gets the name From Json array.
     *
     * @param jsonArray Json array to be parsed
     * @return Returns Json object
     */
    private JsonObject getNameObject(JsonArray jsonArray)
    {
        JsonObject nameObject = null;

        if (jsonArray.get(0) != null)
        {
            nameObject = jsonArray.get(0).getAsJsonObject();
        }
        else
        {
            Log.e("Error Retrieving Name", "Name is Empty");
        }
        return nameObject;
    }

    /**
     * Gets email from Json array
     *
     * @param jsonArray Json array to be parsed
     * @return Json Object with Email
     */
    private JsonObject getEmailObject(JsonArray jsonArray)
    {
        JsonObject emailObject = null;

        if (jsonArray.size() > 2)
        {
            if (jsonArray.get(2) != null)
            {
                emailObject = jsonArray.get(2).getAsJsonObject();
            }
            else
            {
                Log.e("Error Retrieving Email", "Email is Empty");
            }
        }
        return emailObject;
    }

    /**
     * Gets Email String from Json Object
     *
     * @param jsonObject Json email Object
     * @return Email String
     */
    private String getEmailFromObject(JsonObject jsonObject)
    {
        String email = null;
        if (jsonObject != null)
        {

            email = jsonObject.get("value").getAsString();

        }
        else
        {
            Log.e("Error Retrieving Email", "Email is Empty");
        }
        return email;
    }

    /**
     * Gets first name from Json object
     *
     * @param jsonObject Json name Object
     * @return First name string
     */
    private String getFirstNameFromObject(JsonObject jsonObject)
    {
        String firstName = null;
        if (jsonObject != null)
        {
            if (jsonObject.getAsJsonObject("value").get("givenName") != null)
            {
                firstName = jsonObject.getAsJsonObject("value").get("givenName").getAsString();
            }

        }
        else
        {
            Log.e("Error Retrieving First Name", "First Name is Empty");
        }
        return firstName;
    }

    /**
     * Gets last name from name object
     *
     * @param jsonObject Json name Object
     * @return Last name String
     */
    private String getLastNameFromObject(JsonObject jsonObject)
    {
        String lastName = null;
        if (jsonObject != null)
        {
            if (jsonObject.getAsJsonObject("value").get("familyName") != null)
            {
                lastName = jsonObject.getAsJsonObject("value").get("familyName").getAsString();
            }
        }
        else
        {
            Log.e("Error Retrieving Last Name", "Last Name is Empty");
        }
        return lastName;
    }

    /**
     * Instance of Comparator to alphabetically sort names
     */
    public class NameComparator implements Comparator<HashMap<String, String>>
    {
        public int compare(HashMap<String, String> left, HashMap<String, String> right)
        {
            return left.get(SocialContactsUtil.NAME).compareTo(right.get(SocialContactsUtil.NAME));
        }
    }

    /**
     * Finishes the web view activity and sends data back.
     *
     * @param lstContacts Array list of contacts
     * @param activity    the calling activity
     */
    private void FinishActivity(ArrayList<HashMap<String, String>> lstContacts, Activity activity)
    {

        activity.getIntent().putExtra("Contacts", lstContacts);
        activity.setResult(Activity.RESULT_OK, activity.getIntent());
        activity.finish();

    }
}
