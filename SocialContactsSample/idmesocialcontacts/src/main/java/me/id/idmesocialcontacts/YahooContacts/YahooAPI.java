package me.id.idmesocialcontacts.YahooContacts;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by rubenroman on 11/7/14.
 */
interface YahooAPI
{
    @GET ("/v1/user/{guid}/contacts?format=json") void GetContacts(
            @Header ("Authorization") String auth, @Path ("guid") String guid,
            Callback<JsonObject> callback);

}
