package me.id.idmesocialcontacts.YahooContacts;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


/**
 * Created by rubenroman on 8/25/14.
 */
class YahooContactDeserializer implements JsonDeserializer<JsonObject>
{
    @Override
    public JsonObject deserialize(JsonElement je, Type type,
                                  JsonDeserializationContext jdc) throws JsonParseException
    {
        try
        {
            JsonObject result = je.getAsJsonObject();

            return result;
        } catch (Exception e)
        {
            Log.d("Deserializer Error", e.getMessage());
            return null;
        }


    }

}
