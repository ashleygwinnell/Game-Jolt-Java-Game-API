package org.gamejolt;

/**
 * <b>DataStore</b><br/>
 * This class is only instantiated from within the org.gamejolt.* package.
 *
 * @author Ashley Gwinnell
 * @since 0.95
 * @version 0.95
 */
public class DataStore {

    /**
     * This enum is used in the GameJoltAPI class to determine which datastores
     * to retrieve
     */
    public enum DataStoreType {
        USER, GAME
    };

    public enum DataStoreOperation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, APPEND, PREPEND
    };

    private String key;
    private String data;
    private DataStoreType type;

    /**
     * Creates a new DataStore using the given information.
     */
    DataStore(String key, String data, DataStoreType type) {
        this.key = key;
        this.data = data;
        this.type = type;
    }

    void setKey(String key) {
        this.key = key;
    }

    void setData(String data) {
        this.data = data;
    }

    void setType(DataStoreType type) {
        this.type = type;
    }

    /**
     * Retrieve the unique identifying 'key' of this Data Store.
     *
     * @return the unique identifying 'key' of this Data Store.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Retrieve the data String of this Data Store.
     *
     * @return the data String of this Data Store.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Retrieve the type of this Data Store. Does it belong to the user or the
     * game.
     *
     * @return the type of this Data Store.
     */
    public DataStoreType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "DataStore [key=" + key + ", data=" + data + ", type=" + type + "]";
    }
}
