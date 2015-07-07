/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gamejolt;

import java.util.HashMap;

/**
 * A type of object that holds a list of properties
 * @author Kevin Prehn
 */
public class PropertyContainer 
{
        /** The properties */
	private HashMap<String, String> properties;

        public PropertyContainer(PropertyContainer other) {
            properties = new HashMap<>();
            properties.putAll(other.getProperties());
        }
        
	/**
	 * Create a new PropertyContainer.
	 */
	public PropertyContainer() {
		properties = new HashMap<String, String>();
	}
	
	/**
	 * Adds a property to the PropertyContainer.
	 * @param key The key by which the property can be accessed.
	 * @param value The value for the key.
	 */
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Gets a property of the PropertyContainer that isn't specified by a specific method.
	 * This exists for forward compatibility.
	 * @param key The key of the attribute you want to obtain.
	 * @return A property of the PropertyContainer that isn't specified by a specific method.
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}
        
        public HashMap<String, String> getProperties() {
            return properties;
        }

}
