# IDmeSocialContacts
-
IDmeSocialContacts is a simply library that helps extract contacts from Third Party API's.
#Release Information 
- **Version:** 1.0.0 (January 28, 2015)
- **Maintained By:** [Ruben Roman](https://github.com/rubsnick)

For more information please email us at mobile@id.me or visit us at http://developer.id.me.

##Changelog
- Initial Upload


##Installation Instructions
### Gradle
```
repositories
        {
            maven {
                url 'https://oss.sonatype.org/content/groups/public'
            }
        }

dependencies {
       compile 'me.id.idmesocialcontacts:SocialContacts:1.0'
}
```

### Manual
Copy the `.aar` file into your project's `libs` folder.

##Setup
Add the following to gradle 

```
repositories {

flatDir {
   dirs 'libs'
     }
}

dependencies {
compile fileTree(dir: 'libs', include: ['*.jar'])
compile(name: 'IDmeSocialContacts', ext: 'aar')
}
```

### Sample Project
To run the sample project you'll need to replace the `ApiKey`, `SecretKey` and `callbackUrl` values in `MainActivity.java` with those values that are stored withing your registered app at Yahoo.com.

## Execution
The OAuth flow  occurs through a WebView that initialized from within the Library. Upon successful completion the activity will close and the Contacts List will be sent to the calling activity.

To fetch contacts you must initialize a `SocialContacts` object, and call the `FetchContacts()` method.

      SocialContacts socialContacts = new SocialContacts(requesType, activity, apiKey, secretKey,
                    callBackURL, new DataRetriever()
        {
             @Override public void RetrieveContacts(ArrayList<HashMap<String, String>> contacts)
            {
                if (contacts != null)
                {
                //Do Something with Contacts
                }
            }
        });
        socialContacts.FetchContacts();  


The params in the constructor are as follows:

- `requestType`: The type of request to be made.
- `activity`: The activity that is calling creating the Class Object.
- `apiKey `: The apiKey provided by Yahoo when registering your app.
- `secretKey `: The secretKey provided to Yahoo when registering your app at Yahoo.
- `callBackURL `: The URL you want your users to view once completed with oauth.
- `DataRetriever`: The defined interface that will deliver your results.

To retrieve the data your `onActivityResult` method will look like the following.

```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
          socialContacts.onActivityResultsListener(requestCode, resultCode, data);
    }
```

If you want a to get the Local Contacts a simplified constructor was added for your convenience.

    SocialContacts socialContacts=new SocialContacts(SocialContactsUtil.LOCAL,this,new DataRetriever()
        {
            @Override public void RetrieveContacts(ArrayList<HashMap<String, String>> contacts)
            {
               if (contacts != null)
            {
                //Do Something
            }

            }
        });
        
- `requestType`: The type of request to be made (in this case Local).
- `activity`: The activity that is calling creating the Class Object.
- `DataRetriever`: The defined interface that will deliver your results.

There is no need to add `socialContacts.onActivityResultsListener()` to your Activity.

##Results
Each successful request returns the an array List of Contacts.


