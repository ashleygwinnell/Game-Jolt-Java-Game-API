package org.gamejolt;

import java.util.HashMap;

public class HighscoreTable {
	/** The HighscoreTable properties */
	private HashMap<String, String> properties;

	/**
	 * Create a new HighscoreTable with no properties.
	 */
	HighscoreTable() {
		properties = new HashMap<String, String>();
	}
	
	/**
	 * Adds a property to the HighscoreTable.
	 * @param key The key by which the property can be accessed.
	 * @param value The value for the key.
	 */
	void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Gets a property of the HighscoreTable that isn't specified by a specific method.
	 * This exists for forward compatibility.
	 * @param key The key of the HighscoreTable attribute you want to obtain.
	 * @return A property of the HighscoreTable that isn't specified by a specific method.
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}
	/**
	 * @return the id of the table
	 */
	public int getId(){
		return Integer.parseInt(properties.get("id"));
	}
	/**
	 * @return the name of the table
	 */
	public String getName(){
		return properties.get("name");
	}
	/**
	 * @return the description of the table
	 */
	public String getDescription(){
		return properties.get("description");
	}
	/**
	 * @return true if the table is the primary table
	 */
	public boolean isPrimary(){
		return properties.get("primary").equals("1");
	}
}
