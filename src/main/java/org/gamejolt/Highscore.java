package org.gamejolt;

import java.net.URLDecoder;
import java.util.HashMap;

/**
 * <b>Highscore</b><br/>
 * This class is only instantiated from within the org.gamejolt.* package.
 * 
 * @author Ashley Gwinnell
 * @since 0.96
 * @version 0.96
 */
public class Highscore extends PropertyContainer 
{

        public Highscore(PropertyContainer other) {
            super(other);
        }
        
	/**
	 * Retrieve the string value associated with the highscore. e.g. "5 Grapefruits".
	 * @return the string value associated with the highscore. e.g. "5 Grapefruits".
	 */
	public String getScoreString() {
		try {
			return URLDecoder.decode(getProperty("score"), "UTF-8");
		} catch(Exception e) {
			return getProperty("score");
		}
	}
	
	/**
	 * Retrieve the integer value associated with the highscore. e.g. 5.
	 * @return the integer value associated with the highscore. e.g. 5.
	 */
	public int getScoreValue() {
		return Integer.parseInt(getProperty("sort"));
	}
	
	/**
	 * Retrieve the extra data associated with the highscore; usually JSON, XML or a serialised String.
	 * @return the extra data associated with the highscore.
	 */
	public String getExtraData() {
		try {
			return URLDecoder.decode(getProperty("extra_data"), "UTF-8");
		} catch(Exception e) {
			return getProperty("extra_data");
		}
	}
	
	/**
	 * Retrieve whether the Highscore was submitted by a Guest or not.
	 * @return true if the Highscore was submitted by a Guest, otherwise false.
	 */
	public boolean isGuestHighscore() {
		return (getProperty("guest").length() > 0);
	}
	
	/**
	 * Retrieve whether the Highscore was submitted by a User or not.
	 * @return true if the Highscore was submitted by a User, otherwise false.
	 */
	public boolean isUserHighscore() {
		return !this.isGuestHighscore();
	}
	
	/**
	 * Retrieve the time that the highscore was submitted.
	 * @return The time that the highscore was submitted.
	 */
	public String getTime() {
		return getProperty("stored");
	}
	
	/**
	 * Retrieve the User ID of the User that submitted the highscore. 
	 * @return The User ID of the User that submitted the highscore. 
	 */
	public int getUserId() {
		if (getProperty("user_id").length()==0) {
			return -1;
		}
		return Integer.parseInt(getProperty("user_id"));
	}
	/**
	 * If this is a user score, this returns the display name for the user.
	 * If this is a guest score, this returns the guest's submitted name.
	 * @return the name of the scorer
	 */
	public String getUsername() {
		if (getProperty("user")==null){
			return getProperty("guest");
		}else{
			return getProperty("user");
		}
	}
	@Override
	public String toString() {
		return new String("Highscore [user_id=" + this.getUserId() + ", score=" + this.getScoreString() + "]");
	}
}
