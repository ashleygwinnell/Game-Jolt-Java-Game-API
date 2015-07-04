package org.ag.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.gamejolt.DataStore;
import org.gamejolt.DataStore.DataStoreOperation;
import org.gamejolt.DataStore.DataStoreType;
import org.gamejolt.GameJoltAPI;
import org.gamejolt.Trophy;
import org.gamejolt.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class QuickTester {
	
	private static final int gameId=1;
	private static final String privateKey = "";
	
	private static final int trophyId = 0;
	private static final int highscoreId = 0;
	
	private static final String username = "";
	private static final String token = "";
	
	
	static GameJoltAPI api;
	
	@BeforeClass
	public static void beforeClass() {
		api = new GameJoltAPI(gameId, privateKey);
	}
	@Before
	public void before(){
		if (!api.verifyUser(username, token))
			fail("unable to verify user. Can't start testing");
	}
	@AfterClass
	public static void afterClass() {
		File f = new File("gjapi-credentials.txt");
		if (f.exists()) {
			f.delete();
		}
	}
	@Test
	public void testAchieveTrophy(){
		assertTrue(api.achieveTrophy(trophyId));
		assertFalse(api.achieveTrophy(-1));
	}
	@Test
	public void testGetTrophy(){
		Trophy t = api.getTrophy(trophyId);
		assertNotNull(t);
		assertNull(api.getTrophy(-1));
	}
	@Test
	public void testGetTrophies(){
		assertNotNull(api.getTrophies());
	}
	@Test
	public void testGetServerTime(){
		assertNotNull(api.getServerTime());
	}
	@Test
	public void testGetUser(){
		User u = api.getUser(1);
		assertNotNull(u);
		u = api.getUser(u.getName());
		assertNotNull(u);
		assertEquals(1,u.getId());
	}
	@Test
	public void testQuickPlay_missing(){
		File f = new File("gjapi-credentials.txt");
		if (f.exists()) {
			f.delete();
			api.reloadQuickplay();
		}
		assertFalse(api.hasQuickplay());
		assertNull(api.getQuickplayUser());
	}
	@Test
	public void testQuickPlay(){
		File f = new File("gjapi-credentials.txt");
		if (!f.exists()) {
			try(BufferedWriter out = new BufferedWriter(new FileWriter(f))) {
				out.write(username+"\n");
				out.write(token);
			} catch (IOException e) {
				fail(e.getCause().toString());
			}
		}
		api.reloadQuickplay();
		assertTrue(api.hasQuickplay());
		assertNotNull(api.getQuickplayUser());
	}
	@Test
	public void testGetHighscores(){
		assertNotNull(api.getHighscores(highscoreId));
	}
	@Test
	public void testGetHighscoreRank(){
		//assertNotEquals(-1, api.getHighscoreRank(100));
	}
	@Test
	public void testAddHighscore(){
		assertTrue(api.addHighscore("100", 100));
	}
	@Test
	public void testDataStore(){
		DataStore s;
		assertNotNull(s=api.setDataStore(DataStoreType.GAME, "testkey", "testdata"));
		assertNotNull(s=api.getDataStore(DataStoreType.GAME, "testkey"));
		assertEquals("testdata",s.getData());
		assertNotNull(s=api.updateDataStore(DataStoreType.GAME, "testkey", DataStoreOperation.APPEND, "lol"));
		assertEquals("testdatalol",s.getData());
	}
	@Test
	public void testSession(){
		assertTrue(api.sessionOpen());
		assertTrue(api.sessionCheck());
		assertTrue(api.sessionUpdate());
		assertTrue(api.sessionClose());
	}
}
