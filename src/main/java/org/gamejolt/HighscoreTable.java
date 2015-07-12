package org.gamejolt;

public class HighscoreTable extends PropertyContainer 
{
    
        public HighscoreTable(PropertyContainer other) {
            super(other);
        }
	
	/**
	 * @return the id of the table
	 */
	public int getId(){
		return Integer.parseInt(getProperty("id"));
	}
	/**
	 * @return the name of the table
	 */
	public String getName(){
		return getProperty("name");
	}
	/**
	 * @return the description of the table
	 */
	public String getDescription(){
		return getProperty("description");
	}
	/**
	 * @return true if the table is the primary table
	 */
	public boolean isPrimary(){
		return getProperty("primary").equals("1");
	}
}
