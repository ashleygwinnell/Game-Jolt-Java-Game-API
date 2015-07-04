package org.gamejolt;

import java.util.HashMap;

public class ServerTime {
	/** The ServerTime properties */
	private HashMap<String, String> properties;

	/**
	 * Create a new ServerTime.
	 */
	ServerTime() {
		properties = new HashMap<String, String>();
	}
	
	/**
	 * Adds a property to the ServerTime.
	 * @param key The key by which the property can be accessed.
	 * @param value The value for the key.
	 */
	void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Gets a property of the ServerTime that isn't specified by a specific method.
	 * This exists for forward compatibility.
	 * @param key The key of the ServerTime attribute you want to obtain.
	 * @return A property of the ServerTime that isn't specified by a specific method.
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}
	/**
	 * @return The UNIX time stamp representing the server's time.
	 */
	public int getTimestamp(){
		return Integer.parseInt(properties.get("timestamp"));
	}
	/**
	 * @return The timezone the server is in.
	 */
	public String getTimezone(){
		return properties.get("timezone");
	}
	/**
	 * @return The year.
	 */
	public int getYear(){
		return Integer.parseInt(properties.get("year"));
	}
	/**
	 * @return The month.
	 */
	public int getMonth(){
		return Integer.parseInt(properties.get("month"));
	}
	/**
	 * @return The day.
	 */
	public int getDay(){
		return Integer.parseInt(properties.get("day"));
	}
	/**
	 * @return The hour.
	 */
	public int getHour(){
		return Integer.parseInt(properties.get("hour"));
	}
	/**
	 * @return The minute.
	 */
	public int getMinute(){
		return Integer.parseInt(properties.get("minute"));
	}
	/**
	 * @return The seconds.
	 */
	public int getSeconds(){
		return Integer.parseInt(properties.get("seconds"));
	}
        
       @Override
       public String toString() {
           return "ServerTime [year=" + getYear() + ", month=" + getMonth() + 
                   ", day=" + getDay() + ", hour=" + getHour() + ", minute=" + 
                   getMinute() + ", seconds=" + getSeconds() + "]";
       }
}
