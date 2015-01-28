package me.id.idmesocialcontacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


class RetrieveLocalContacts
{
    private Context mContext;
    private ArrayList<HashMap<String, String>> lstContacts;
    private DataRetriever dataRetriever;

    /**
     * Default Constructor for Retrieve Local Contacts
     *
     * @param mContext      The context of the Application
     * @param dataRetriever Defined DataRetriever Interface
     */
    public RetrieveLocalContacts(Context mContext, DataRetriever dataRetriever)
    {
        this.mContext = mContext;
        this.dataRetriever = dataRetriever;
        lstContacts = new ArrayList<HashMap<String, String>>();
    }

    /**
     * Called After Creating an Instance of Object to Retrieve Local Contacts
     */
    public void FetchContacts()
    {

        getAllRawContacts();

        Collections.sort(lstContacts, new NameComparator());

        dataRetriever.RetrieveContacts(lstContacts);
    }

    //region Fetch Logic

    /**
     * Gets Raw Contacts from Phones Storage.
     */
    private void getAllRawContacts()
    {
        final String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID,                    // the contact id column
                ContactsContract.RawContacts.DELETED                        // column if this contact is deleted
        };

        final Cursor rawContacts = mContext.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                // the uri for raw contact provider
                projection, null,
                // selection = null, retrieve all entries
                null,
                // not required because selection does not contain parameters
                null);                                    // do not order

        final int contactIdColumnIndex = rawContacts.getColumnIndex(
                ContactsContract.RawContacts.CONTACT_ID);
        final int deletedColumnIndex = rawContacts.getColumnIndex(
                ContactsContract.RawContacts.DELETED);


        if (rawContacts.moveToFirst())
        {                    // move the cursor to the first entry
            while (!rawContacts.isAfterLast())
            {            // still a valid entry left?
                final int contactId = rawContacts.getInt(contactIdColumnIndex);
                final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);
                if (!deleted)
                {

                    String name = getContactName(contactId);
                    String email = getContactEmails(contactId);

                    if (name != null && email != null)
                    {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put(SocialContactsUtil.NAME, name);
                        hashMap.put(SocialContactsUtil.Email, email);
                        lstContacts.add(hashMap);
                    }
                }
                rawContacts.moveToNext();                // move to the next entry
            }
        }

        rawContacts.close();
    }

    /**
     * Extracts Contact Name Using Contact ID.
     *
     * @param contactId The unique identifier for the Contact.
     * @return Contact String Name
     */
    private String getContactName(int contactId)
    {
        final String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

        final Cursor contact = mContext.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, projection,
                ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(contactId)},
                null);

        if (contact.moveToFirst())
        {
            final String name = contact.getString(
                    contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


            contact.close();
            return name;
        }
        contact.close();
        return null;
    }

    /**
     * Extracts Contact Email Using Contact ID.
     *
     * @param contactId The unique identifier for the Contact.
     * @return Contact String Email
     */
    private String getContactEmails(int contactId)
    {
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.TYPE};

        final Cursor email = mContext.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection,
                ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)},
                null);
        String address = null;
        if (email.moveToFirst())
        {
            final int contactEmailColumnIndex = email.getColumnIndex(
                    ContactsContract.CommonDataKinds.Email.ADDRESS);


            while (!email.isAfterLast())
            {
                address = address + "," + email.getString(contactEmailColumnIndex);
                //final int type = email.getInt(contactTypeColumnIndex);

                email.moveToNext();

            }
            if (address != null)
            {
                address = address.replace("null,", "");
            }
            return address;

        }
        email.close();
        return null;
    }

    /**
     * Sorts Names by Alphabetical Order
     */
    public class NameComparator implements Comparator<HashMap<String, String>>
    {
        public int compare(HashMap<String, String> left, HashMap<String, String> right)
        {
            return left.get(SocialContactsUtil.NAME).compareTo(right.get(SocialContactsUtil.NAME));
        }
    }

    //endregion

}
