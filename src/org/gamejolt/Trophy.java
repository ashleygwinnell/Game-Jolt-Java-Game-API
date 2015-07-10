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
public class Trophy extends PropertyContainer
{
	/** The Difficulty level enumeration. */
	public enum Difficulty {BRONZE, SILVER, GOLD, PLATINUM};
	
	/** The Achieved almost-boolean type enumeration. */
	public enum Achieved {TRUE, FALSE, EMPTY};

        public Trophy(PropertyContainer container) {
            super(container);
        }
        
	/**
	 * Get the ID of the Trophy.
	 * @return The ID of the Trophy.
	 */
	public String getId() {
		return getProperty("id");
	}
	/**
	 * Get the name of the Trophy.
	 * @return The name of the Trophy.
	 */
	public String getTitle() {
		return getProperty("title");
	}
	
	/**
	 * Get the description of the Trophy.
	 * @return The description of the Trophy.
	 */
	public String getDescription() {
		return getProperty("description");
	}
	
	/**
	 * Get the difficulty of the Trophy. 
	 * i.e. Bronze, Silver, Gold, Platinum.
	 * @return The difficulty of the Trophy.
	 */
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(getProperty("difficulty"));
	}
	
	/**
	 * Determines whether the Trophy is achieved or not.
	 * @return True if the verified user has the Trophy.
	 */
	public boolean isAchieved() {
		return !getProperty("achieved").equals("false");
	}
	
	/**
	 * Gets the URL of the Trophy's image.
	 * @return The URL of the Trophy's image.
	 */
	public String getImageURL() {
		return getProperty("image_url");
	}
	
	@Override
	public String toString() {
		return new String("Trophy [id=" + this.getId() + ", title=" + this.getTitle() + "]"); //, description=" + this.description + ", difficulty=" + this.difficulty + ", achieved=" + this.achieved);
	}
}
