package org.ag.test;

import java.util.ArrayList;

import org.gamejolt.DataStore.DataStoreOperation;
import org.gamejolt.DataStore.DataStoreType;
import org.gamejolt.GameJoltAPI;
import org.gamejolt.Highscore;
import org.gamejolt.Trophy;

public class GJAPITester {
	
	public static void main(String[] args) {
		
		// Define these variables yourself! 
		final int GAME_ID = 1;
		final String GAME_SECRET = "";
		
		final String USER_NAME = "";
		final String USER_TOKEN = "";
		
		final int table_id = 0;
		
		// Initialise Game Jolt API
		GameJoltAPI api = new GameJoltAPI(GAME_ID, GAME_SECRET);
		api.setVerbose(true);
		api.verifyUser(USER_NAME, USER_TOKEN);
		
		// This is a test, so make the library print everything it can.
		api.setVerbose(true);
		
		// Get Trophies
		ArrayList<Trophy> trophies = api.getTrophies();
		
		// Achieve a Trophy
		api.achieveTrophy(1);
		
		// Play Sessions
		api.sessionOpen();
		api.sessionUpdate();
		api.sessionClose();
		
		// Get Highscores
		ArrayList<Highscore> highscores = api.getHighscores();
		
		// Add a Highscore
		if (table_id!=0){
			api.addHighscore(table_id,"100 Coins Test", 100);
		}else{
			api.addHighscore("100 Coins Test", 100);
		}
		
		//get highscores
		if (table_id!=0){
			api.getHighscores(table_id);
		}else{
			api.getHighscores();
		}
		
		// Get Data Store Keys (User-Specific, requires verified User)
		ArrayList<String> userDataStoreKeys = api.getDataStoreKeys(DataStoreType.USER);
		
		// Set a value in a Data Store
		api.setDataStore(DataStoreType.USER, "a_test_key", "a_test_value");
		
		// Update a value in a Data Store
		api.updateDataStore(DataStoreType.USER, "a_test_key", DataStoreOperation.APPEND, "lol");
		
		
	}
	
}
