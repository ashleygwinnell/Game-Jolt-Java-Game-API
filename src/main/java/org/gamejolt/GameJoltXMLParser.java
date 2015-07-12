/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gamejolt;

import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



/**
 * parses format=xml responses
 * @author Kevin
 */
public class GameJoltXMLParser extends GameJoltResponseParser{

    @Override
    public PropertyContainer parsePropertiesFrom(String response, String[] properties) {
        NodeList respList = parseResponseString(response);
        if (!isSuccessful(respList)) {
            return null;
        }
        return parsePropertiesFromList(respList, properties);
    }

    @Override
    public ArrayList<PropertyContainer> parsePropertiesFromArray(String response, String arrayName, String[] properties) {
        NodeList respList = parseResponseString(response);
        if (!isSuccessful(respList)) {
            return null;
        }
        ArrayList<PropertyContainer> containers = new ArrayList<>();
        NodeList dataList = respList.item(1).getChildNodes(); // first after success attribute has data for arrays
        for (int i=0; i<dataList.getLength(); i++) {
            containers.add(parsePropertiesFromList(dataList.item(i).getChildNodes(), properties));
        }
        return containers;
    }

    @Override
    public boolean isSuccessful(String response) {
        System.out.println(response);
        NodeList list = parseResponseString(response);
        if (list.item(0) == null || list.item(0).getFirstChild() == null) {
            return false;
        }
        return list.item(0).getFirstChild().getNodeValue().equals("true");
    }

    @Override
    public ArrayList<String> parseDatastoresKeysResponse(String response) {
        System.out.println(response);
        NodeList list = parseResponseString(response);
        if (!isSuccessful(list)) {
            return null;
        }
        NodeList keysArray = list.item(1).getChildNodes();
        ArrayList<String> keys = new ArrayList<>();
        for (int i=0; i<keysArray.getLength(); i++) {
            Node dataNode = keysArray.item(i).getFirstChild().getFirstChild();
            keys.add(dataNode.getNodeValue());
        }
        return keys;
    }

    @Override
    public int parseHighscoreRankResponse(String response) {
        System.out.println(response);
        NodeList list = parseResponseString(response);
        if (!isSuccessful(list)) {
            return -1;
        }
        Node rankData = list.item(1); // rank is the second element
        return Integer.parseInt(rankData.getFirstChild().getNodeValue());
    }

    @Override
    public User parseUserRequestResponse(String response) {
        NodeList list = parseResponseString(response);
        if (!isSuccessful(list)) {
            return null;
        }
        System.out.println("USER REQUEST RESPONSE" + response);
        Node userNode = list.item(1).getFirstChild(); // the user
        
        PropertyContainer container = parsePropertiesFromList(userNode.getChildNodes(), getUserProperties());
        User u = new User(container);
        NodeList children = userNode.getChildNodes();
        boolean haveType = false;
        boolean haveStatus = false;
        for (int i=0; i<children.getLength(); i++) {
            Node data = children.item(i);//.getFirstChild();
            String value = data.getFirstChild().getNodeValue();
            String key = data.getNodeName();
            if (key.equals("type")) {
                haveType = true;
                u.setType(User.UserType.valueOf(value.toUpperCase()));
            } else if (key.equals("status")) {
                haveStatus = true;
                u.setStatus(User.UserStatus.valueOf(value.toUpperCase()));
            }
            if (haveType && haveStatus) {
                break; // already have everything
            }
        }
        
        return u;
    }
  
    private NodeList parseResponseString(String response) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));
        
            Document doc = builder.parse(is);
            return doc.getDocumentElement().getChildNodes();
        } catch(Exception e) {
            System.err.println("Error while parsing the xml string");
        }
        return null;
    }
    
    public boolean isSuccessful(NodeList responseNodeList) {
        return responseNodeList.item(0).getFirstChild().getNodeValue().equals("true");
    }
    
    public PropertyContainer parsePropertiesFromList(NodeList dataList, String[] properties) {
        PropertyContainer container = new PropertyContainer();
        for (int i=0; i<dataList.getLength(); i++) {
            Node n = dataList.item(i);
            String key = n.getNodeName();
            boolean isInProperties = false;
            for (int j=0; j<properties.length; j++) {
                if (properties[j].equals(key)) {
                    isInProperties = true;
                    break;
                }
            }
            if (!isInProperties) {
                // only add elements that are in the list of properties
                continue;
            }
            
            Node child = n.getFirstChild();
            String value = "";
            if (child != null) {
                value = child.getNodeValue();
            }
            
            //System.out.println(" child node(?) value: " + value);
            container.addProperty(key, value);
        }
        return container;
    }
}
