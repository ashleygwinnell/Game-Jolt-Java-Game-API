package org.gamejolt;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import org.gamejolt.DataStore.DataStoreOperation;
import org.gamejolt.DataStore.DataStoreType;
import org.gamejolt.Trophy.Achieved;

/**
 * <b>GameJoltAPI</b><br/>
 * This is the main class that you use to implement the GameJolt Trophy, DataStore and HighScore systems.
 * 
 * @since 0.90
 * @version 0.99
 * @author Ashley Gwinnell
 */
public class GameJoltAPI 
{
	private final String protocol = new String("http://");
	//private final String api_root = new String("staging.gamejolt.com/api/game/");
	//private final String api_root = new String("staging.gamejolt.com/game-api/");
	private final String api_root = new String("gamejolt.com/api/game/");
	//private final String api_root = new String("gamejoltdevash.dyndns.org/api/game/");
	
        public enum Format {
            XML, 
            JSON, 
            KEYPAIR;
            
            public GameJoltResponseParser getParser(){
                if (this == XML) {
                    return new GameJoltXMLParser();
                } else if (this == JSON) {
                    return new GameJoltJSONParser();
                } 
                return new GameJoltKeypairsParser(); // default type
            }
            @Override
            public String toString() {
                if (this == XML) {
                    return "xml";
                } else if (this == JSON) {
                    return "json";
                } 
                return "keypair"; // default
            }
        };
        
        // the parser used for responses
        private GameJoltResponseParser parser;
        public Format format;
        
	private int gameId;
	private String privateKey;
	private String version = "1_1";
	//private double version = 0.95;
	
	private String username;
	private String usertoken;
	
	private String quickplay_username;
	private String quickplay_usertoken;
	
	private boolean verbose = false;
	private boolean verified = false;
        
	/**
	 * Create a new GameJoltAPI object without trying to verify the user.
	 * 
	 * This method will try to read the Game Jolt Quick Play file (gjapi-credentials.txt).
	 * If that file exists, then it will attempt to verify the user.
	 * 
	 * @param gameId Your Game's Unique ID.
	 * @param privateKey Your Game's Unique (Private) Key.
	 */
	public GameJoltAPI(int gameId, String privateKey) 
	{
		this.gameId = gameId;
		this.privateKey = privateKey;
		File f = new File("gjapi-credentials.txt");
		if (f.exists()) {
			try(Scanner sc = new Scanner(f)) {
				this.quickplay_username = sc.nextLine();
				this.quickplay_usertoken = sc.nextLine();
				//this.verifyUser(username, usertoken);		
			} catch(FileNotFoundException exc) {
			} catch(NoSuchElementException exc) {
				if(verbose)
					System.err.println(exc.getCause());
			}
		}
                // the initial parser
                format = Format.KEYPAIR;
                parser = format.getParser();
	}
		
	/**
	 * Create a new GameJoltAPI and tries to verify the user.
	 * @param gameId Your Game's Unique ID.
	 * @param privateKey Your Game's Unique (Private) Key.
	 */
	public GameJoltAPI(int gameId, String privateKey, String username, String userToken) 
	{
		this.gameId = gameId;
		this.privateKey = privateKey;
		this.verifyUser(username, userToken);
	}
	
	/**
	 * Set the version of the GameJolt API to use.
	 * @param version The version of the GameJolt API to be using.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * Get the version of the GameJolt API you are using. 
	 * @return The API version in use.
	 */
	public String getVersion() {
		return version;
	}	

	/**
	 * Sets whether the API should print out debug information to the Console.
	 * By default, this is set to true.
	 * @param b whether the API should print out debug informationto the Console.
	 */
	public void setVerbose(boolean b) {
		this.verbose = b;
	}
	
	/**
	 * Returns true if the GJ API is set to print out it's debug information.
	 * @return True if the GJ API is set to print out it's debug information.
	 */
	public boolean isVerbose() {
		return verbose;
	}
	
	/**
	 * Check whether the user/player has verified their credentials.
	 * @return whether the user/player has verified their credentials or not.
	 */
	public boolean isVerified() {
		return verified;
	}
	
	/**
	 * Returns true if the API could find the gjapi-credentials.txt file.
	 * @return true if the API could find the gjapi-credentials.txt file.
	 */
	public boolean hasQuickplay() {
		if (this.quickplay_username == null && this.quickplay_usertoken == null) {
			return false;
		}
		return true;
	}
	/**
	 * reloads the quickplay-file. This will reset the verified-status.
	 * This should normally have no effect, since the quickplay-file is created when the user starts a quickplay-game, and not while the game is running.
	 */
	public void reloadQuickplay(){
		File f = new File("gjapi-credentials.txt");
		if (f.exists()) {
			try(Scanner sc = new Scanner(f)) {
				this.quickplay_username = sc.nextLine();
				this.quickplay_usertoken = sc.nextLine();
			} catch(FileNotFoundException exc) { }
		}else{
			this.quickplay_username = null;
			this.quickplay_usertoken = null;
		}
		verified=false;
	}
	/**
	 * Gets the user object of the quickplay user if the game has the gjapi-credentials.txt file.
	 * if you only want to get the name and token, use {@link #getQuickplayUserCredientals()}
	 * @return the User object if the game has the gjapi-credentials.txt file.
	 */
	public User getQuickplayUser(){
		if (!hasQuickplay())
			return null;
		User u = getUser(quickplay_username);
		u.setToken(quickplay_usertoken);
		return u;
	}
	/**
	 * Return the User object if the game has the gjapi-credentials.txt file.
	 * Note that the User object returned will only have a name and token set!
	 * @return the User object if the game has the gjapi-credentials.txt file.
	 */
	public User getQuickplayUserCredientals() {
		if (!hasQuickplay())return null;
		User u = new User();
		u.setName(this.quickplay_username);
		u.setToken(this.quickplay_usertoken);
		return u;
	}
	/**
	 * gets the User object of the user with a certain name
	 * This User will not have a token
	 * @param name the name of the user
	 * @return the Userobject of the user or null if no user with this name exists
	 */
	public User getUser(String name){
		HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", name);
		return getUserRequest(params);
	}
	/**
	 * gets the User object of the user with a certain id
	 * This User will not have a token
	 * @param id the id of the user
	 * @return the Userobject of the user or null if no user with this id exists
	 */
	public User getUser(int id){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", String.valueOf(id));
		return getUserRequest(params);
	}
	
        /**
         * Sends a user fetch request using the given parameters.
         * @param params The parameter to send for the user fetch request.
         * @return The user object that was returned if it succeeds, or null if it fails.
         */
        private User getUserRequest(HashMap<String, String> params) {
                String response = request("users/", params, false);
		if (verbose) { System.out.println(response); }
		
                try {
                    User u = parser.parseUserRequestResponse(response);
                    if (verbose && u == null) { 
                            System.err.println("GameJoltAPI: Could not get the Verified User with Username: " + this.username); 
                            System.err.println(response);
                    }
                    return u;
                } catch (Exception pe) {
                    pe.printStackTrace();
                    return null;
                }
        }
        
	/**
	 * Return the User object if the user is verified, otherwise return null.
	 * @return the User object if the user is verified, otherwise null.
	 */
	public User getVerifiedUser() {
		if (this.verified) {
			return getQuickplayUser();
		}  else {
			if (this.verbose) { System.err.println("GameJoltAPI: Could not get the (currently verified) user."); }
			return null;
		}
	}
	
	/**
	 * Retrieve the first 100 of the Highscores from GameJolt for the game in an array.
	 * @return all of the Highscores from GameJolt for the game in an array.
	 */
	public ArrayList<Highscore> getHighscores() {
		return this.getHighscores(true, 100);
	}
	
	/**
	 * Retrieve the first 100 Highscores from GameJolt for either a game or the verified user.
	 * 
	 * @param all If set to true, this will retrieve all highscores. Otherwise it will retrieve the currently verified user's highscores.
	 * @return An array of Highscore objects on success, an empty array or null on failure.
	 */
	public ArrayList<Highscore> getHighscores(boolean all) {
		return this.getHighscores(all, 100);
	}
	/**
	 * Retrieve all of the Highscores of the Highscoretable from GameJolt for the game in an array.
	 * @param id the id of the Highscoretable
	 * @return all of the Highscores from GameJolt for the game in an array.
	 */
	public ArrayList<Highscore> getHighscores(int id) {
		return this.getHighscores(id,true, 100);
	}
	
	/**
	 * Retrieve a list of Highscores from GameJolt for either a game or the verified user.
	 * 
	 * @param id the id of the Highscoretable. If 0 is given it will use
         * the primary high score table.
	 * @param all If set to true, this will retrieve all highscores. Otherwise it will retrieve the currently verified user's highscores.
	 * @return An array of Highscore objects on success, an empty array or null on failure.
	 */
	public ArrayList<Highscore> getHighscores(int id,boolean all) {
		return this.getHighscores(id, all, 100);
	}
	/**
	 * Retrieve a list of Highscores from GameJolt for either a game or the verified user.
         * THis will use the primary high score table.
	 * 
	 * @param all If set to true, this will retrieve all highscores. Otherwise it will retrieve the currently verified user's highscores.
	 * @param limit the maximum amount of highscores to receive
	 * @return An array of Highscore objects on success, an empty array or null on failure.
	 */
	public ArrayList<Highscore> getHighscores(boolean all, int limit) {
		return getHighscores(0,all,limit);
	}
	
	/**
	 * Retrieve a list of Highscores from GameJolt for either a game or the verified user.
	 * @param id the id of the table. If 0 is given it will use the primary
         * high score table
	 * 
	 * @param all If set to true, this will retrieve all highscores. Otherwise it will retrieve the currently verified user's highscores.
	 * @param limit the number of scores you want to receive (max. 100)
	 * @return An array of Highscore objects on success, an empty array or null on failure.
	 */
	public ArrayList<Highscore> getHighscores(int id, boolean all, int limit) {
		if (all == false && !this.verified) { 
			if (verbose) { System.err.println("GameJoltAPI: Could not get the Highscores for the verified user as the user is not verified."); }
			return null; 
		}
		try {
			HashMap<String, String> params = new HashMap<String, String>();
                        String response = null;
                        if (id!=0)
                                params.put("table_id", String.valueOf(id));
			if (all == true) { // all highscores
				params.put("limit", (""+limit));

				response = request("scores", params, false);			
			} else { // verified user's highscores.
				params.put("username", username);
				params.put("user_token", usertoken);  
				params.put("limit", ""+limit);

				response = request("scores", params, true);
			}
			
			if (verbose) {
				System.out.println(response);
			}
                        ArrayList<Highscore> highscores = parser.parseHighscoreResponse(response);
                        if (highscores == null) {
                            if (verbose) {
                                System.err.println("GameJoltAPI: Could not get the highscores "
                                        + "from the table with the id '" + id + "'");
                            }
                        }
                        return highscores;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
        
	/**
	 * retrieve the Rank with the score closest to the given score from 
         * the primary high score table.
	 * @param score the score for which the rank should be retrieved
	 * @return the closest rank to the score
	 */
	public int getHighscoreRank(int score){
		return getHighscoreRank(score, 0);
	}
	/**
	 * retrieve the Rank with the score closest to the given score.
	 * @param score the score for which the rank should be retrieved
	 * @param id the id of the HighscoreTable. If this is 0, it will 
         * get the primary high score table.
	 * @return the closest rank to the score, or -1 if there was an error
	 */
	public int getHighscoreRank(int score, int id){		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
                        // if no ID is supplied, it will get the primary score table.
			if (id!=0)
				params.put("table_id", String.valueOf(id));
			params.put("sort", String.valueOf(score));

			String response = request("scores/get-rank", params, false);
			
			if (verbose) {
				System.out.println(response);
			}
                        int rank = parser.parseHighscoreRankResponse(response);
                        if (rank == -1) {
                            if (verbose) {
                                System.err.println("GameJoltAPI: Could not get the highscore "
                                        + "rank for the score '" + score + "' and id '" + id + "'");
                            }
                        }
                        return rank;
                } catch (Exception pe) {
                    pe.printStackTrace();
                    return -1;
                }
	}
	/**
	 * gets a List of all Highscoretables available for this game
	 * @return a list of Highscoretables, or null if there is an error or it's not successful
	 */
	public ArrayList<HighscoreTable> getHighscoreTables(){
		String response = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			response = request("scores/tables", params, false);
			
			if (verbose) {
				System.out.println(response);
			}
			ArrayList<HighscoreTable> tables = parser.parseHighscoreTableResponse(response);
                        if (tables == null) {
                            if (verbose) {
                                System.err.println("GameJoltAPI: Could not get a list of highscore tables");
                            }
                        }
                        return tables;
                } catch (Exception pe) {
                    pe.printStackTrace();
                }
            return null;
	}
	
	/**
	 * Add a highscore for the currently verified Game Jolt user.
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @return true if successful, otherwise false.
	 */
	public boolean addHighscore(String score, int sort) {
		return this.addHighscore(0,score, sort, "");
	}
	/**
	 * Add a highscore for the currently verified Game Jolt user.
	 * @param id the id of the Highscoretable
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @return true if successful, otherwise false.
	 */
	public boolean addHighscore(int id, String score, int sort) {
		return this.addHighscore(id, score, sort, "");
	}
	
	/**
	 * Add a highscore for the currently verified Game Jolt user.
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @param extra Extra information to be stored about this score as a String, such as how the score was made, or time taken. This is not shown on the site.
	 * @return true if successful, otherwise false.
	 */
	public boolean addHighscore(String score, int sort, String extra) {
		return addHighscore(0, score, sort,"");
	}
	/**
	 * Add a highscore for the currently verified Game Jolt user.
	 * @param id the id of the Highscoretable
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @param extra Extra information to be stored about this score as a String, such as how the score was made, or time taken. This is not shown on the site.
	 * @return true if successful, otherwise false.
	 */
	public boolean addHighscore(int id, String score, int sort, String extra) {
		if (!this.verified) {
			if (verbose) { System.err.println("GameJoltAPI: Could not add the High Score because the user is not verified."); }
			return false;
		}
		String response = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("username", username);
			params.put("user_token", usertoken);  
			params.put("score", ""+score);
			params.put("extra_data", ""+extra);
			params.put("sort", ""+sort);
			if (id!=0)
				params.put("table_id", String.valueOf(id));
			
			response = request("scores/add",params,true);
			if (verbose) { System.out.println(response); }
			
			if (!parser.isSuccessful(response)  || response.equals("REQUEST_FAILED")) {
				if (verbose) { System.err.println("GameJoltAPI: Could not add the High Score."); }
				if (verbose) { System.out.println(response); }
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Adds a HighScore for a Guest!
	 * @param guest_username The desired name of the guest you want to add the highscore for.
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @return true if successful, false otherwise.
	 */
	public boolean addHighscore(String guest_username, String score, int sort) {
		return this.addHighscore(0,guest_username, score, sort, "");
	}
	/**
	 * Adds a HighScore for a Guest!
	 * @param if the id of the HighscoreTable
	 * @param guest_username The desired name of the guest you want to add the highscore for.
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @return true if successful, false otherwise.
	 */
	public boolean addHighscore(int id,String guest_username, String score, int sort) {
		return this.addHighscore(id,guest_username, score, sort, "");
	}
	/**
	 * Adds a HighScore for a Guest with additional data!
	 * @param guest_username The desired name of the guest you want to add the highscore for.
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @param extra Extra information to be stored about this score as a String, such as how the score was made, or time taken. This is not shown on the site.
	 * @return true if successful, false otherwise.
	 */
	public boolean addHighscore(String guest_username, String score, int sort, String extra) {
		return addHighscore(0, guest_username,score, sort);
	}
	/**
	 * Adds a HighScore for a Guest!
	 * @param id the id of the highscoretable
	 * @param guest_username The desired name of the guest you want to add the highscore for.
	 * @param score The String of the score, e.g. "5 Grapefruits". This is shown on the site.
	 * @param sort The sortable value of the score, e.g. 5. This is shown on the site.
	 * @param extra Extra information to be stored about this score as a String, such as how the score was made, or time taken. This is not shown on the site.
	 * @return true if successful, false otherwise.
	 */
	public boolean addHighscore(int id,String guest_username, String score, int sort, String extra) {
		String response = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("guest", guest_username);
			params.put("score", ""+score);
			params.put("sort", ""+sort);
			params.put("extra_data", (""+extra));
			if (id!=0)
				params.put("table_id", String.valueOf(id));
			
			response = request("scores/add", params, false);
			if (verbose) { System.out.println(response); }
			if (!parser.isSuccessful(response) || response.equals("REQUEST_FAILED")) {
				if (verbose) { System.err.println("GameJoltAPI: Could not add the Guest High Score."); }
				if (verbose && response.contains("Guests are not allowed to enter scores for this game.")) { // TODO: optimisation.
					System.err.println("Guests are not allowed to enter scores for this game.");
				}
				if (verbose) { System.out.println(response); }
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/*
	 * Data Storage
	 * 
	 * /api/game/data-store/ 
	 * 	?game_id 
	 * 	?username [empty|username] 
	 * 	?user_token [empty|user_token] 
	 * 	?key 
	 * 
	 * /api/game/data-store/set/ 
	 * 	?game_id 
	 * 	?username [empty|username] 
	 * 	?user_token [empty|user_token] 
	 * 	?key 
	 * 	?data (serialized string) 
	 * 
	 * /api/game/data-store/remove/ 
	 * 	?game_id 
	 * 	?username [empty|username] 
	 * 	?user_token [empty|user_token] 
	 * 	?key 
	 */ 
	/**
	 * updates the data of an existing entry on the gamejolts servers by performing a {@link DataStoreOperation} between the data on the Server and the values
	 * @param type the Type of the Data Store. Should be either DataTypeStore.USER or DataTypeStore.GAME.
	 * @param key key for which to store the data. You use this key to retrieve the DataStore.
	 * @param operation the operation to perform on the entry
	 * @param value
	 * @return
	 */
	public DataStore updateDataStore(DataStoreType type, String key, DataStoreOperation operation, int value)
	{
		return updateDataStore(type, key, operation, ""+value);
	}
	/**
	 * updates the data of an existing entry on the gamejolts servers by performing a {@link DataStoreOperation} between the data on the Server and the values
	 * @param type the Type of the Data Store. Should be either DataTypeStore.USER or DataTypeStore.GAME.
	 * @param key key for which to store the data. You use this key to retrieve the DataStore.
	 * @param operation the operation to perform on the entry
	 * @param value
	 * @return
	 */
	public DataStore updateDataStore(DataStoreType type, String key, DataStoreOperation operation, String value) 
	{
		String response=null;
		try {
			if (type == DataStoreType.GAME) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("operation", operation.toString().toLowerCase());
				params.put("value", value);
				params.put("key", ""+key);
				params.put("format", "dump");
				
				response = request("data-store/update/", params, false);
				if (verbose) { System.out.println(response); }
			
			} else if (type == DataStoreType.USER) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("operation", operation.toString().toLowerCase());
				params.put("value", value);
				params.put("key", ""+key);
				params.put("format", "dump");
				response = request("data-store/update/", params, true);
//				response = this.request("data-store/update/", "key=" + key + "&operation=" + operation.toString().toLowerCase() + "&value=" + value);
				if (verbose) { 
					System.out.println(response); 
				}
			}
		} catch(Exception e) {
			System.err.println("urg");
			return null;
		}
		if (!response.substring(0, 7).equals("SUCCESS")) {
			if (verbose) { System.err.println("could not update DataStore");}
			return null;
		} 
		DataStore ds = new DataStore();
		ds.setKey(key);
		ds.setData(response.substring(9));
		ds.setType(type);
		return ds;
	}
	
	/**
	 * Adds a piece of data to Game Jolt's servers replacing 
	 * any that already exists with the key and type given.
	 * 
	 * @param type The type of the Data Store. Should be either DataTypeStore.USER or DataTypeStore.GAME.
	 * @param key The key for which to store the data. You use this key to retrieve the DataStore.
	 * @param data Data to keep on Game Jolt servers. This data is normally xml, json, or serialised memory.
	 * @return A DataStore Object for the data you passed in.
	 */
	public DataStore setDataStore(DataStoreType type, String key, String data) {
		String response = null;
		if (type == DataStoreType.GAME) {
			HashMap<String, String> params = new HashMap<String, String>();
			HashMap<String,String> postParams = new HashMap<String,String>();
			params.put("key", ""+key);
			postParams.put("data", ""+data);
			
			response=requestAsPost("data-store/set", params,postParams, false);
			
		} else {
			HashMap<String, String> params = new HashMap<String, String>();
			HashMap<String,String> postParams = new HashMap<String,String>();
			params.put("key", ""+key);
			postParams.put("data", ""+data);
			
			response = this.requestAsPost("data-store/set", params,postParams,true);
			if (verbose) { System.out.println(response); }
			
		}
		if (!parser.isSuccessful(response)) {
			if (verbose) { System.err.println("GameJoltAPI: Could not add " + type + " DataStore with Key \"" + key + "\"."); }
		//	if (verbose) { System.out.println(response); }
			return null;
		}
		//if (verbose) { System.out.println(response); }
		DataStore ds = new DataStore();
		ds.setKey(key);
		ds.setData(data);
		ds.setType(type);
		return ds;
	}
	
	/**
	 * Remove a piece of data from Game Jolt's servers
	 * as specified by the type and key.
	 * 
	 * @param type The type of the Data Store. Should be either DataTypeStore.USER or DataTypeStore.GAME.
	 * @param key The key for which to remove the data.
	 * @return True if the DataStore was removed successfully. Note this will return false if no item exists with the key given.
	 */
	public boolean removeDataStore(DataStoreType type, String key) {
		String response = null;
		if (type == DataStoreType.GAME) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key",(""+key));
			response = request("data-store/remove", params, false);
			if (verbose) { System.out.println(response); }
			
		}
		else {
			response = this.request("data-store/remove", "key=" + key);
			if (verbose) { System.out.println(response); }
			
		}
		if (!parser.isSuccessful(response)) {
			if (verbose) { System.err.println("GameJoltAPI: Could not get " + type + " DataStore with Key \"" + key + "\"."); }
			if (verbose) { System.out.println(response); }
			
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieve a list of DataStore Objects for the type specified by the parameter.
	 * This method loops through all of the keys and creates all objects using a new http request for each key.
	 * For this reason, it is slow, and it is advised that you use GameJoltAPI.getDataStoreKeys();
	 * In Big O notation, this method takes O(n + 1)  time.
	 * 
	 * @param type The Type of keys to get, either DataStoreType.USER or DataStoreType.GAME.
	 * @return a list of Data Store keys for the type specified by the parameters.
	 */
	public ArrayList<DataStore> getDataStoreObjects(DataStoreType type) {
		ArrayList<String> keys = this.getDataStoreKeys(type);
		ArrayList<DataStore> datastores = new ArrayList<DataStore>();
		for (int i = 0; i < keys.size(); i++) {
			datastores.add(this.getDataStore(type, keys.get(i)));
		}
		return datastores;
	}
	
	/**
	 * Retrieve a list of Data Store keys for the type specified by the parameter.
	 * 
	 * @param type The Type of keys to get, either DataStoreType.USER or DataStoreType.GAME.
	 * @return a list of Data Store keys for the type specified by the parameters. 
         * If there was an error, it returns null.
	 */
	public ArrayList<String> getDataStoreKeys(DataStoreType type) {
		try {
			String response;
			if (type == DataStoreType.GAME) {
				response = request("data-store/get-keys",new HashMap<>(),false);
			} else {
				response = this.request("data-store/get-keys", new HashMap<>(),true);
			}
			if (verbose) {
				System.out.println(response);
			}
			ArrayList<String> keys_list = parser.parseDatastoresKeysResponse(response);
			if (keys_list == null) {
				if (verbose) {
					System.err.println("GameJoltAPI: Could not get the ServerTime.");
				}
			}
			return keys_list;
		} catch (Exception e) {
			if (verbose) {
				System.err.println("GameJoltAPI: Error while getting Datastore keys");
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Retrieve a piece of data from Game Jolt's servers
	 * as specified by type and key.
	 * 
	 * @param type The type of the Data Store. Should be either DataTypeStore.USER or DataTypeStore.GAME.
	 * @param key The key for which the data was stored.
	 * @return The DataStore Object for the key passed in.
	 */
	public DataStore getDataStore(DataStoreType type, String key){
		String response = null;
		if (type == DataStoreType.GAME) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("format", "dump");
			params.put("key", ""+key);
			response = request("data-store/", params, false);
			if (verbose) { System.out.println(response); }
		} else {
			response = this.request("data-store/", "key=" + key + "&format=dump");
			if (verbose) { System.out.println(response); }
			
		}
		if (!response.substring(0, 7).equals("SUCCESS")) {
			if (verbose) { System.err.println("GameJoltAPI: " + response.substring(9)); }
			if (verbose) { System.out.println(response); }
			return null;
		} 
		DataStore ds = new DataStore();
		ds.setKey(key);
		ds.setData(response.substring(9));
		ds.setType(type);
		return ds;
	}
	
	/* 
	 * Status
	 *
	 * /api/game/status/open/
	 *  ?game_id
	 *	?username
	 *	?user_token
	 *
	 * /api/game/status/ping/
	 *	?game_id
	 *	?username
	 *	?user_token
	 *	?status=[active|idle](optional)
	 *	
	 * /api/game/status/close/
	 *	?game_id
	 *	?username
	 *	?user_token
	 */

	/**
	 * Open a new play session with Game Jolt.
	 * This requires a user to be verified (logged in).
	 * You should do this before you ping or update. This will close the previously open session.
	 * 
	 * @return true if a new play session was opened successfully. false if request fails or if the user is not verified.
	 */
	public boolean sessionOpen() {
		if (!this.verified) { 
			if (verbose) {
				System.err.println("GameJoltAPI: Could not open Play Session: User must be verified.\n");
			}
			return false; 
		}
		String response = "";
		response = this.request("sessions/open/", "");
		if (this.verbose) { System.out.println(response); } 
		if (parser.isSuccessful(response)) {
			return true;
		} else {
			if (verbose) {
				System.err.println("GameJoltAPI: Could not open Play Session.\n");
				System.err.println(response);
			}
			return false;
		}
	}
	/**
	 * Check if a session exists for the player
	 * @return true if the session exists, false if no session exists or the player is not verified.
	 */
	public boolean sessionCheck(){
		if (!this.verified) { 
			if (verbose) {
				System.err.println("GameJoltAPI: Could not check Play Session: User must be verified.\n");
			}
			return false; 
		}
		String response = this.request("sessions/check/", "");
		if (this.verbose) { System.out.println(response); }
		if (parser.isSuccessful(response)) {
			return true;
		}else{
			if (verbose) {
				System.err.println("GameJoltAPI: Could not update (ping) Play Session.\n");
				System.err.println(response);
			}
			return false;
		}
	}
	/**
	 * Update the current play session with Game Jolt. 
	 * You should call this every 60 seconds as Game Jolt closes play sessions after 120 seconds of inactivity.
	 * This method will set the user as ACTIVE. Use the other method to set the user as IDLE.
	 * This requires a user to be verified (logged in).
	 * This method will return false if there is no session to update.
	 * 
	 * @return true if the play session was updated (pinged) successfully, false if request fails (no current session) or if the user is not verified.
	 */
	public boolean sessionUpdate() {
		return this.sessionUpdate(true);
	}
	
	/**
	 * Update the current play session with Game Jolt.
	 * You should call this every 60 seconds as Game Jolt closes play sessions after 120 seconds of inactivity.
	 * This requires a user to be verified (logged in).
	 * This method will return false if there is no session to update.
	 * 
	 * @param active You can set the game player as ACTIVE with true or IDLE with false. A good example of this would be sending an idle message if the user is on a menu or pause screen.
	 * @return true if the play session was updated (pinged) successfully, false if request fails (no current session) or if the user is not verified.
	 */
	public boolean sessionUpdate(boolean active) {
		if (!this.verified) { 
			if (verbose) {
				System.err.println("GameJoltAPI: Could not update (ping) Play Session: User must be verified.\n");
			}
			return false; 
		}
		HashMap<String,String> params = new HashMap<String,String>();
		if (active){
			params.put("status", "active");
		}else{
			params.put("status", "idle");
		}
		String response = this.request("sessions/ping/", params);
		if (this.verbose) { System.out.println(response); }
		if (parser.isSuccessful(response)) {
			return true;
		} else {
			if (verbose) {
				System.err.println("GameJoltAPI: Could not update (ping) Play Session.\n");
				System.err.println(response);
			}
			return false;
		}
	}
	
	/**
	 * Close the current play session at Game Jolt.
	 * This requires a user to be verified (logged in).
	 * You should do this when closing or exiting your game.
	 * This method will return false if there is no session to close.
	 * 
	 * @return true if the play session was closed successfully, false if request fails (no current session) or if the user is not verified.
	 */
	public boolean sessionClose() {
		if (!this.verified) { 
			if (verbose) {
				System.err.println("GameJoltAPI: Could not close Play Session: User must be verified.\n");
			}
			return false; 
		}
		String response = this.request("sessions/close/", "");
		if (this.verbose) { System.out.println(response); }
		if (parser.isSuccessful(response)) {
			return true;
		} else {
			if (verbose) {
				System.err.println("GameJoltAPI: Could not close Play Session.\n");
				System.err.println(response);
			}
			return false;
		}
	}
	
	/**
	 * Give the currently verified user a trophy specified by Trophy object.
	 * This method uses the trophy's ID.
	 * @param t The Trophy to give.
	 * @return true on successfully given trophy.
	 */
	public boolean achieveTrophy(Trophy t) {
		return achieveTrophy(Integer.parseInt(t.getId()));
	}
	
	/**
	 * Give the currently verified user a trophy specified by Id.
	 * @param trophyId The ID of the Trophy to give.
	 * @return true on successfully given trophy.
	 */
	public boolean achieveTrophy(int trophyId) {
		String response = this.request("trophies/add-achieved", "trophy_id=" + trophyId);
		if (parser.isSuccessful(response)) {
			return true;
		} else {
			if (verbose) {
				System.err.println("GameJoltAPI: Could not give Trophy to user.\n");
				System.err.println(response);
			}
			return false;
		}
	}
	
	
	/**
	 * Get a list of all trophies.
	 * @return A list of trophy objects.
	 */
	public ArrayList<Trophy> getTrophies() {
		return this.getTrophies(Achieved.EMPTY);
	}

	/**
	 * Get a list of trophies filtered with the Achieved parameter.
	 * The parameter can be Achieved.TRUE for achieved trophies, Achieved.FALSE for 
	 * unachieved trophies or Achieved.EMPTY for all trophies.
	 * @param a The type of trophies to get.
	 * @return A list of trophy objects.
	 */
	public ArrayList<Trophy> getTrophies(Achieved a) {
		String response = this.request("trophies/", "achieved=" + a.toString().toLowerCase());
		
                try {
                    ArrayList<Trophy> trophies = parser.parseTrophyResponse(response);
                    if (trophies == null) {
                        if (verbose) {
                            System.err.println("GameJoltAPI: Error while getting trophies"); 
                        }
                    }
                    return trophies;
                } catch(Exception e) {
                    if (verbose) { 
                            System.err.println("GameJoltAPI: Error while getting trophies"); 
                            e.printStackTrace();
                    }
                }
                return null;
	}
	
	/**
	 * Gets a single trophy from GameJolt as specified by trophyId
	 * @param trophyId The ID of the Trophy you want to get.
	 * @return The Trophy Object with the ID passed, or null if there is an error.
	 */
	public Trophy getTrophy(int trophyId) {
		String response = this.request("trophies/", "trophy_id=" + trophyId);
                try {
                    ArrayList<Trophy> trophies = parser.parseTrophyResponse(response);
                    if (trophies == null) {
                        if (verbose) { 
                                System.err.println("GameJoltAPI: No such trophies with the ID " + trophyId + " exists"); 
                        }
                        return null;
                    } else {
                        return trophies.get(0);
                    }
                } catch(Exception e) {
                    if (verbose) { 
                            System.err.println("GameJoltAPI: Error while getting trophies"); 
                            e.printStackTrace();
                    }
                }
                return null;
	}
	

	public ServerTime getServerTime(){
		String response = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			response = request("get-time", params, false);
			
			if (verbose) {
				System.out.println(response);
			}
                        ServerTime time = parser.parseServerTimeResponse(response);
                        if (time == null) {
                            if (verbose) {
                                System.err.println("GameJoltAPI: Could not get the ServerTime."); 
                            }
                        }
                        return time;
                } catch(Exception e) {
                    if (verbose) { 
                            System.err.println("GameJoltAPI: Could not get the ServerTime."); 
                            System.err.println(response);
                    }

                }
                return null;
	}
	
	/**
	 * Calculates an MD5 hash.
	 * @param input The String you want the hash of.
	 * @return The MD5 Hash of the String passed.
	 */
	public String MD5(String input)
	{
		String res = "";
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(input.getBytes());
			byte[] md5 = algorithm.digest();
			String tmp = "";
			for (int i = 0; i < md5.length; i++) {
				tmp = (Integer.toHexString(0xFF & md5[i]));
				if (tmp.length() == 1) {
					res += "0" + tmp;
				} else {
					res += tmp;
				}
			}
		} catch (NoSuchAlgorithmException ex) {}
		return res;
	}

	
	/**
	 * Attempt to verify the Players Credentials.
	 * @param username The Player's Username.
	 * @param userToken The Player's User Token.
	 * @return true if the User was successfully verified, false otherwise.
	 */
	public boolean verifyUser(String username, String userToken)
	{
		this.verified = false;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("user_token", userToken);
                String response = this.request("users/auth/", params, false);
                if (verbose) { System.out.println(response); }

                if (parser.isSuccessful(response)) {
                    this.username = username;
                    this.usertoken = userToken;
                    this.verified = true;
                    return true;
                } else {
                    if (verbose) {
                        System.err.println("GameJoltAPI: Could not verify user");
                    }
                }
                return false;
	}
	
	/**
	 * Make a request to the GameJolt API.
	 * @param method The GameJolt API method, such as "game-api/add-trophy", without the "game-api/" part.
	 * @param params A map of the parameters you want to include. 
	 * 				 Note that if the user is verified you do not have to include the username/user_token/game_id.
	 * @return The response of the request.
	 */
	public String request(String method, HashMap<String, String> params) {
		return this.request(method, params, true);
	}
	
	/**
	 * Perform a GameJolt API request.
	 * Use this one if you know your HTTP requests.
	 * @param method The API method to call. Note that gamejolt.com/api/game/ is already prepended.
	 * @param paramsLine The GET request params, such as "trophy_id=23&achieved=empty".
	 * @return The response, default is keypair.
	 */
	public String request(String method, String paramsLine) 
	{
		return this.request(method, paramsLine, true);
	}
	 
	public String request(String method, String paramsLine, boolean requireVerified){
		HashMap<String, String> ps = new HashMap<String, String>();
		String[] params = paramsLine.split("&");
		for (int i = 0; i < params.length; i++) {
			if (params[i].length() == 0) {
				continue;
			}
			
			String[] s = params[i].split("=");
			
			String key = s[0];
			String value = (s.length==1)?"":s[1];
			ps.put(key, value);
		}
		return this.request(method, ps, requireVerified);
	}
	
	/**
	 * Make a request to the GameJolt API.
	 * @param method The GameJolt API method, such as "add-trophy", without the "game-api/" part.
	 * @param params A map of the parameters you want to include. 
	 * 				 Note that if the user is verified you do not have to include the username/user_token/game_id.
	 * @param requireVerified This is only set to true when checking if the user is verified.
	 * @return
	 */
	private String request(String method, HashMap<String, String> params, boolean requireVerified)
	{
		try {
			if (requireVerified){
				if (!verified){
					return "REQUIRES_AUTHENTICATION";
				}
				params.put("user_token", this.usertoken);
				params.put("username", this.username);
			}
			String urlString = this.getRequestURL(method, params);
			String signature = this.MD5(urlString.concat(privateKey));
			
			urlString = urlString.concat("&signature=").concat(signature);
			if (verbose) { System.out.println(urlString); }
			return this.openURLAndGetResponse(urlString);
		} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		return null;
	}
	/**
	 * Make a request to the GameJoltAPI using the RequestMethod POST
	 * @param method The GameJolt API method, such as "add-trophy", without the "game-api/" part.
	 * @param urlParams A map of the parameters you want to include in the url
	 * 				 Note that if the user is verified you do not have to include the username/user_token/game_id.
	 * @param postParams A map of the parameters you want to include in the body of the POST-request
	 * @param requireVerifiedThis is only set to true when checking if the user is verified.
	 * @return
	 */
	private String requestAsPost(String method, HashMap<String, String> urlParams,HashMap<String, String> postParams, boolean requireVerified)
	{
		try {
			if (requireVerified){
				if (!verified){
					return "REQUIRES_AUTHENTICATION";
				}
				urlParams.put("user_token", this.usertoken);
				urlParams.put("username", this.username);
			}
			String urlString = this.getRequestURL(method, urlParams);
			String signature = this.MD5(urlString.concat(privateKey));
			
			urlString = urlString.concat("&signature=").concat(signature);
			if (verbose) { System.out.println(urlString); }
			return this.openURLAndGetResponseUsingPost(urlString,postParams);
		
		} catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		return null;
	}
	/**
	 * Performs the HTTP Request.
	 * @param urlString The URL to HTTP Request.
	 * @return The HTTP Response.
	 */
	public String openURLAndGetResponse(String urlString)
	{
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStream stream = connection.getInputStream();
			BufferedInputStream buff = new BufferedInputStream(stream);
			int character = -1;
			StringBuilder response = new StringBuilder();
			while ((character = buff.read()) != -1) {
				response.append((char)character);
			}
			return response.toString();
		} catch (IOException e) {
			//e.printStackTrace();
			if (this.verbose) { System.err.println("GameJoltAPI: " + e.getMessage()); }
			return "REQUEST_FAILED";
		}
	}
	/**
	 * Performs the HTTP Request.
	 * @param urlString The URL to HTTP Request.
	 * @return The HTTP Response.
	 */
	public String openURLAndGetResponseUsingPost(String urlString,HashMap<String,String> postParams)
	{
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
			        new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQuery(postParams));
			writer.flush();
			writer.close();
			os.close();
			connection.connect();
			InputStream stream = connection.getInputStream();
			BufferedInputStream buff = new BufferedInputStream(stream);
			int character = -1;
			StringBuilder response = new StringBuilder();
			while ((character = buff.read()) != -1) {
				response.append((char) character);
			}
			System.out.println(response);
			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
			if (this.verbose) { System.err.println("GameJoltAPI: " + e.getMessage()); }
			return "REQUEST_FAILED";
		}
	}
	/**
	 * converts a HashMap into the html Parameter-format<br>key1=value1&key2=value2&...&keyN=valueN
	 * @param params the HashMap that should be converted
	 * @return a String of the format key1=value1&key2=value2&...&keyN=valueN
	 * @throws UnsupportedEncodingException if UTF-8 is not supported
	 */
	private String getQuery(HashMap<String,String> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (Entry<String, String> pair : params.entrySet())
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
	
	/**
	 * Get the full request url from the parameters given.
	 * @param method The GameJolt API method, such as "game-api/add-trophy".
	 * @param params A map of the parameters you want to include. 
	 * @return The full request url.
	 */
	private String getRequestURL(String method, HashMap<String, String> params) throws UnsupportedEncodingException {
		String urlString = protocol + api_root + "v" + this.version + "/" + method + "?game_id=" + this.gameId;
		//String urlString = protocol + api_root + method + "?game_id=" + this.gameId;
		if (!params.containsKey("format"))
			params.put("format", format.toString());
		Set<String> keyset = params.keySet();
		Iterator<String> keys = keyset.iterator();
		String user_token = null;
		while (keys.hasNext()) {
			String key = keys.next();
			String value = params.get(key);
			if (key.equals("user_token")) {
				user_token = value;
				continue;
			}
			urlString += "&" + key + "=" + URLEncoder.encode(value, "UTF-8");
		}
		if (user_token!=null) {
			urlString += "&user_token=" + user_token;
		}
		
		return urlString;
	}

        /**
         * Set the format Game Jolt's responses will be.
         * @param format The format to make the responses.
         */
        public void setFormat(Format format) {
            this.format = format;
            this.parser = format.getParser();
        }

}