package org.gamejolt;

import java.util.HashMap;

/**
 * <b>User</b><br/>
 * This class is created and populated using the GameJoltAPI class.
 * 
 * @author Ashley Gwinnell
 * @since 0.95
 * @version 0.97
 */
public class User 
{
	/** The different types of users that are available at Game Jolt. */
	public enum UserType {USER, DEVELOPER, MODERATOR, ADMIN};
	
	/** This enum type dictates whether the user is banned or not! */
	public enum UserStatus {ACTIVE, BANNED};
	
	/** The User properties map */
	private HashMap<String, String> properties;

	
	
	User() {
		properties = new HashMap<String, String>();
	}
	
	/**
	 * Adds a property to the User.
	 * @param key The key by which the property can be accessed.
	 * @param value The value for the key.
	 */
	void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Gets a property of the User that isn't specified by a specific method.
	 * This exists for forward compatibility.
	 * @param key The key of the User attribute you want to obtain.
	 * @return A property of the User that isn't specified by a specific method.
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}
	
	
	void setName(String s) {
		this.properties.put("username", s);
	}
	void setToken(String s) {
		this.properties.put("token", s);
	}
	void setType(UserType t) {
		this.properties.put("type", t.toString());
	}
	void setStatus(UserStatus s) {
		this.properties.put("status", s.toString());
	}
	
	public String getLastLoggedIn(){
		return properties.get("last_logged_in");
	}
	public String getSignedUp(){
		return properties.get("signed_up");
	}
	public int getId(){
		return Integer.parseInt(properties.get("id"));
	}
	public String getName() {
		return this.properties.get("username");
	}
	public String getToken() {
		return this.properties.get("token");
	}
	public UserType getType() {
		return UserType.valueOf(this.properties.get("type"));
	}
	public String getAvatarURL() {
		return this.properties.get("avatar_url");
	}
	public UserStatus getStatus() {
		return UserStatus.valueOf(this.properties.get("status"));
	}
	public String getDeveloperName() {
		return this.properties.get("developer_name");
	}
	public String getDeveloperWebsite() {
		return this.properties.get("developer_website");
	}
	public String getDeveloperDescription() {
		return this.properties.get("developer_description");
	}
	
	@Override
	public String toString() {
		return "User [name=" + getName() + ", token=" + getToken() + ", type=" + getType() + ", avatar_url=" + getAvatarURL() + "]";
	}
}
