/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gamejolt;

import java.util.ArrayList;

/**
 * Parses format=keypair responses
 * @author Kevin
 */
public class GameJoltKeypairsParser extends GameJoltResponseParser {

    @Override
    public PropertyContainer parsePropertiesFrom(String response, String[] properties) {
        PropertyContainer container = new PropertyContainer();
        String[] lines = response.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String key = lines[i].substring(0, lines[i].indexOf(':'));
            String value = lines[i].substring( lines[i].indexOf(':')+2, lines[i].lastIndexOf('"'));
            container.addProperty(key, value);
        }
        return container;
    }

    @Override
    public ArrayList<PropertyContainer> parsePropertiesFromArray(String response, String arrayName, String[] properties) {
        ArrayList<PropertyContainer> containers = new ArrayList<>();
        
        String startKey = ""; // the key that passes when we know a new element is starting
        String[] lines = response.split("\n");
        PropertyContainer container = null;
        for (int i = 1; i < lines.length; i++) {
            String key = lines[i].substring(0, lines[i].indexOf(':'));
            String value = lines[i].substring( lines[i].indexOf(':')+2, lines[i].lastIndexOf('"'));
            if (i == 1) {
                startKey = key;
            }
            if (key.equals(startKey)) {
                // new element
                if (container != null) {
                    containers.add(container); // add the container that is now done
                }
                container = new PropertyContainer();
            }

            if (container != null) {
                container.addProperty(key, value);
            }
        }
        // add the final container if there is any
        if (container != null) {
            containers.add(container); // add the container that is now done
        }
        
        return containers;
    }

    @Override
    public boolean isSuccessful(String response) {
        return (response.split("\n"))[0].trim().equals("success:\"true\"");
    }
    
    private boolean isSuccessful(String[] lines) {
        return lines[0].trim().equals("success:\"true\"");
    }

    @Override
    public ArrayList<String> parseDatastoresKeysResponse(String response) {
        String[] lines = response.split("\n");
        if (!isSuccessful(lines)) {
            return null;
        }
        ArrayList<String> keys_list = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
                keys_list.add(lines[i].substring(lines[i].indexOf('"')+1, lines[i].lastIndexOf('"')));
        }
        return keys_list;
    }

    @Override
    public int parseHighscoreRankResponse(String response) {
        String[] lines = response.split("\n");
        if (!isSuccessful(lines)) {
            return -1;
        }

        for (int i = 1; i < lines.length; i++) {
                if (lines[i].contains("scores")) { break; }
                String key = lines[i].substring(0, lines[i].indexOf(':'));
                String value = lines[i].substring( lines[i].indexOf(':')+2, lines[i].lastIndexOf('"'));
                if (key.equals("rank")) {
                        return Integer.parseInt(value);
                }
        }
        return -1;
    }

    @Override
    public User parseUserRequestResponse(String response) {
        String[] lines = response.split("\n");
        if (!isSuccessful(lines)) {
            return null;
        }
        User u = new User();
        for (int i = 1; i < lines.length; i++) {
            String key = lines[i].substring(0, lines[i].indexOf(':'));
            String value = lines[i].substring( lines[i].indexOf(':')+2, lines[i].lastIndexOf('"'));
            if (key.equals("type")) {
                    u.setType(User.UserType.valueOf(value.toUpperCase()));
            } else if (key.equals("status")) {
                    u.setStatus(User.UserStatus.valueOf(value.toUpperCase()));
            } else {
                    u.addProperty(key, value);
            }
        }
        return u;
    }
    
}
