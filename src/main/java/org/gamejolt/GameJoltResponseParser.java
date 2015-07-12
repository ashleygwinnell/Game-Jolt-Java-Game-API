/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gamejolt;

import java.util.ArrayList;

/**
 * An abstract parser for parsing the GameJolt responses
 * @author Kevin
 */
public abstract class GameJoltResponseParser {

    // properties to read from the responses for the different types of objects
    private final String[] userProperties = {"id", "username", "avatar_url", "signed_up",
                                    "last_logged_in"};

    private final String[] highscoreProperties = {"score", "extra_data", "user",
                                        "user_id", "guest", "stored"};

    private final String[] highscoreTableProperties = {"id",  "name", "description",
                                              "primary"};

    private final String[] trophyProperties = {"id", "title", "description",
                                    "difficulty", "image_url", "achieved"};

    private final String[] serverTimeProperties = {"year", "month", "day", "hour",
                                            "minute", "seconds"};
    
    /**
    * Takes a string, then parses the given properties out of the string
    * using the current format
    * @param response The raw response string
    * @param properties The list of the properties to parse out. if the KEYPAIRS 
    * format is the current format, this list is ignored
    * @return The PropertyContainer object will all the properties in the list
    * that it could find, or null if there was an error.
    */
    public abstract PropertyContainer parsePropertiesFrom(String response, String[] properties);
    
    /**
    * Takes a string, then parses many containers out of it, interpreting it as a 
    * large list.
    * @param response The raw response string
    * @param properties The list of the properties to parse out.
    * @param arrayName The name of the array to parse out. This is only needed
    *      for JSON or XML formats.
    * @return THe list of all the PropertyContainer from the array. If there
    * was an error or if the array that was parsed is empty, then an empty array is returned.
    */
    public abstract ArrayList<PropertyContainer> parsePropertiesFromArray(String response, 
           String arrayName, String[] properties);
    
    /**
     * Check if the response was successful
     * @param response THe response from GameJolt
     * @return If the response was successful
     */
    public abstract boolean isSuccessful(String response);
    
    /**
     * Create a ServerTime object from a GameJolt response
     * @param response The response from GameJolt
     * @return The new ServerTime object, or null if there was an error
     */
    public ServerTime parseServerTimeResponse(String response) {
        if (!isSuccessful(response)) {
            return null;
        }
        return new ServerTime(parsePropertiesFrom(response, getServerTimeProperties()));
    }
    
    /**
     * Create a Trophy object from a GameJolt response
     * @param response The response from GameJolt
     * @return The new Trophy object, or null if there was an error or it wasn't successful
     */
    public ArrayList<Trophy> parseTrophyResponse(String response) {
        if (!isSuccessful(response)) {
            return null;
        }
        ArrayList<PropertyContainer> containers = parsePropertiesFromArray(response, "trophies", getTrophyProperties());
        ArrayList<Trophy> trophies = new ArrayList<>();
        for (PropertyContainer pc : containers) {
            trophies.add(new Trophy(pc));
        }
        return trophies;
    }
    
    /**
     * Get a list of Datastore keys from a GameJolt response
     * @param response The response from GameJolt
     * @return A list of all the keys returned (this could be an empty array)
     */
    public abstract ArrayList<String> parseDatastoresKeysResponse(String response);
    
    /**
     * Get a list of HighscoreTables from a GameJolt response
     * @param response The response from GameJolt
     * @return A list of all the keys returned (this could be an empty array). If
     * the request is not successful, this returns null.
     */
    public ArrayList<HighscoreTable> parseHighscoreTableResponse(String response) {
        if (!isSuccessful(response)) {
            return null;
        }
        ArrayList<PropertyContainer> containers = parsePropertiesFromArray(response, "tables", getHighscoreTableProperties());
        ArrayList<HighscoreTable> tables = new ArrayList<>();
        for (PropertyContainer pc : containers) {
            tables.add(new HighscoreTable(pc));
        }
        return tables;
    }
    
    /**
     * Get the 'rank' property from the GameJolt response
     * @param response The response from GameJolt
     * @return The 'rank' property from the response
     */
    public abstract int parseHighscoreRankResponse(String response);
    
    /**
     * Get a list of Highscores from a GameJolt response
     * @param response The response from GameJolt
     * @return A list of all the highscores parsed from the response (this could be empty)
     */
    public ArrayList<Highscore> parseHighscoreResponse(String response) {
        if (!isSuccessful(response)) {
            return null;
        }
        ArrayList<PropertyContainer> containers = parsePropertiesFromArray(response, "scores", getHighscoreProperties());
        ArrayList<Highscore> highscores = new ArrayList<>();
        for (PropertyContainer pc : containers) {
            highscores.add(new Highscore(pc));
        }
        return highscores;
    }
    /**
     * Get a User object from a GameJolt response
     * @param response The response from GameJolt
     * @return The newly created User object
     */
    public abstract User parseUserRequestResponse(String response);

    
    // properties getters
    public String[] getUserProperties() {
        return userProperties;
    }

    public String[] getHighscoreProperties() {
        return highscoreProperties;
    }

    public String[] getHighscoreTableProperties() {
        return highscoreTableProperties;
    }

    public String[] getTrophyProperties() {
        return trophyProperties;
    }

    public String[] getServerTimeProperties() {
        return serverTimeProperties;
    }
    
    
}
