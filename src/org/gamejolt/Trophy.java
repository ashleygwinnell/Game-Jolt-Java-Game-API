package org.gamejolt;

import java.util.HashMap;

/**
 * <b>Trophy</b><br/>
 * A trophy is an achievement in the GameJolt API.
 * 
 * @author Ashley Gwinnell
 * @since 0.90
 * @version 0.90
 */
public class Trophy 
{
	/** The Difficulty level enumeration. */
	public enum Difficulty {BRONZE, SILVER, GOLD, PLATINUM};
	
	/** The Achieved almost-boolean type enumeration. */
	public enum Achieved {TRUE, FALSE, EMPTY};
	
	/** The Trophy properties */
	private HashMap<String, String> properties;
	
	/**
	 * Create a new Trophy.
	 */
	Trophy() {
		properties = new HashMap<String, String>();
	}
	
	/**
	 * Adds a property to the Trophy.
	 * @param key The key by which the property can be accessed.
	 * @param value The value for the key.
	 */
	void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Gets a property of the Trophy that isn't specified by a specific method.
	 * This exists for forward compatibility.
	 * @param key The key of the Trophy attribute you want to obtain.
	 * @return A property of the Trophy that isn't specified by a specific method.
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}
	
	/**
	 * Get the ID of the Trophy.
	 * @return The ID of the Trophy.
	 */
	public String getId() {
		return this.getProperty("id");
	}
	/**
	 * Get the name of the Trophy.
	 * @return The name of the Trophy.
	 */
	public String getTitle() {
		return this.getProperty("title");
	}
	
	/**
	 * Get the description of the Trophy.
	 * @return The description of the Trophy.
	 */
	public String getDescription() {
		return this.getProperty("description");
	}
	
	/**
	 * Get the difficulty of the Trophy. 
	 * i.e. Bronze, Silver, Gold, Platinum.
	 * @return The difficulty of the Trophy.
	 */
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(this.getProperty("difficulty"));
	}
	
	/**
	 * Determines whether the Trophy is achieved or not.
	 * @return True if the verified user has the Trophy.
	 */
	public boolean isAchieved() {
		return !this.getProperty("achieved").equals("false");
	}
	
	/**
	 * Gets the URL of the Trophy's image.
	 * @return The URL of the Trophy's image.
	 */
	public String getImageURL() {
		return this.getProperty("image_url");
	}
	
	@Override
	public String toString() {
		return new String("Trophy [id=" + this.getId() + ", title=" + this.getTitle() + "]"); //, description=" + this.description + ", difficulty=" + this.difficulty + ", achieved=" + this.achieved);
	}
}
