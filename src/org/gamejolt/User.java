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
public class User extends PropertyContainer
{
    
        public User() {
            super();
        }
        
        public User(PropertyContainer other) {
            super(other);
        }
        
	/** The different types of users that are available at Game Jolt. */
	public enum UserType {USER, DEVELOPER, MODERATOR, ADMIN};
	
	/** This enum type dictates whether the user is banned or not! */
	public enum UserStatus {ACTIVE, BANNED};
	
	/** The User properties map */
	void setName(String s) {
		this.addProperty("username", s);
	}
	void setToken(String s) {
		this.addProperty("token", s);
	}
	void setType(UserType t) {
		this.addProperty("type", t.toString());
	}
	void setStatus(UserStatus s) {
		this.addProperty("status", s.toString());
	}
	
	public String getLastLoggedIn(){
		return getProperty("last_logged_in");
	}
	public String getSignedUp(){
		return getProperty("signed_up");
	}
	public int getId(){
		return Integer.parseInt(getProperty("id"));
	}
	public String getName() {
		return this.getProperty("username");
	}
	public String getToken() {
		return this.getProperty("token");
	}
	public UserType getType() {
		return UserType.valueOf(this.getProperty("type"));
	}
	public String getAvatarURL() {
		return this.getProperty("avatar_url");
	}
	public UserStatus getStatus() {
		return UserStatus.valueOf(this.getProperty("status"));
	}
	public String getDeveloperName() {
		return this.getProperty("developer_name");
	}
	public String getDeveloperWebsite() {
		return this.getProperty("developer_website");
	}
	public String getDeveloperDescription() {
		return this.getProperty("developer_description");
	}
	
	@Override
	public String toString() {
		return "User [name=" + getName() + ", token=" + getToken() + ", type=" + getType() + ", avatar_url=" + getAvatarURL() + "]";
	}
}
