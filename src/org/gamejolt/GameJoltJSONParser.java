/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gamejolt;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * parses format=json responses
 * @author Kevin
 */
public class GameJoltJSONParser extends GameJoltResponseParser{

    @Override
    public PropertyContainer parsePropertiesFrom(String response, String[] properties) {
        JSONObject resp = parseResponseString(response);
        return parsePropertiesFrom(resp, properties);
    }
    
    private PropertyContainer parsePropertiesFrom(JSONObject response, String[] properties) {
        PropertyContainer container = new PropertyContainer();
        for (String str : properties) {
            container.addProperty(str, response.get(str).toString());
        }
        return container;
    }

    @Override
    public ArrayList<PropertyContainer> parsePropertiesFromArray(String response, String arrayName, String[] properties) {
        ArrayList<PropertyContainer> containers = new ArrayList<>();
        JSONObject resp = parseResponseString(response);
        if (isSuccessful(resp)) { // only attempt to parse if it was successful
            // get all the elements in the JSONArray list
            JSONArray entries = (JSONArray)resp.get(arrayName);
            for (Object o : entries) {
                // parse each element in the JSON array, and add it to the list of containers
                containers.add(parsePropertiesFrom((JSONObject) o, properties));
            }
        }
        return containers;
    }

    @Override
    public boolean isSuccessful(String response) {
        JSONObject resp = parseResponseString(response);
            if (response == null)
                return false;
        return isSuccessful(resp);
    }
    
    private boolean isSuccessful(JSONObject resp) {
        return resp.get("success").toString().equals("true");
    }

    @Override
    public ArrayList<String> parseDatastoresKeysResponse(String response) {
        ArrayList<String> keys_list = new ArrayList<>();
        JSONObject resp = parseResponseString(response);
        if (!isSuccessful(resp)) {
                //if (verbose) { System.err.println("GameJoltAPI: Could not get " + type + " DataStores."); }
                return null;
        }
        JSONArray keys = (JSONArray)resp.get("keys");
        for (Object o : keys) {
            keys_list.add(((JSONObject)o).get("key").toString());
        }
        return keys_list;
    }
    
    @Override
    public int parseHighscoreRankResponse(String response) {
        Highscore h = new Highscore(parsePropertiesFrom(response, 
                                    new String[]{"rank"}));
        return Integer.parseInt(h.getProperty("rank"));
    }

    @Override
    public User parseUserRequestResponse(String response) {
         // get the first user in the list because this methodo only returns one user
        JSONObject resp = (JSONObject)(((JSONArray)(parseResponseString(response)).get("users")).get(0));
        User u = new User(parsePropertiesFrom(resp, getUserProperties()));
        u.setType(User.UserType.valueOf(resp.get("type").toString().toUpperCase()));
        u.setStatus(User.UserStatus.valueOf(resp.get("status").toString().toUpperCase()));
        return u;
    }
    
    /**
    * Parses text returned as a response to a request into a JSONObject.
    * @param responseText The string returned as a response to a request in a JSON format.
    * @return The parsed response object. If there was an error while parsing, it returns null.
    */
   private JSONObject parseResponseString(String responseText) {
       try {
               JSONParser parser = new JSONParser();
               return (JSONObject)((JSONObject)(parser.parse(responseText))).get("response");
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }
}
