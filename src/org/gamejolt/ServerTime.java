package org.gamejolt;

public class ServerTime extends PropertyContainer 
{

        public ServerTime(PropertyContainer other) {
            super(other);
        }
	/**
	 * @return The UNIX time stamp representing the server's time.
	 */
	public int getTimestamp(){
		return Integer.parseInt(getProperty("timestamp"));
	}
	/**
	 * @return The timezone the server is in.
	 */
	public String getTimezone(){
		return getProperty("timezone");
	}
	/**
	 * @return The year.
	 */
	public int getYear(){
		return Integer.parseInt(getProperty("year"));
	}
	/**
	 * @return The month.
	 */
	public int getMonth(){
		return Integer.parseInt(getProperty("month"));
	}
	/**
	 * @return The day.
	 */
	public int getDay(){
		return Integer.parseInt(getProperty("day"));
	}
	/**
	 * @return The hour.
	 */
	public int getHour(){
		return Integer.parseInt(getProperty("hour"));
	}
	/**
	 * @return The minute.
	 */
	public int getMinute(){
		return Integer.parseInt(getProperty("minute"));
	}
	/**
	 * @return The seconds.
	 */
	public int getSeconds(){
		return Integer.parseInt(getProperty("seconds"));
	}
        
       @Override
       public String toString() {
           return "ServerTime [year=" + getYear() + ", month=" + getMonth() + 
                   ", day=" + getDay() + ", hour=" + getHour() + ", minute=" + 
                   getMinute() + ", seconds=" + getSeconds() + "]";
       }
}
