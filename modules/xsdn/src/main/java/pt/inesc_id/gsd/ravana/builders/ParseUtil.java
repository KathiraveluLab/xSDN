/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.builders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pt.inesc_id.gsd.ravana.constants.XSDNConstants;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The utility methods assisting parsing the documents of xSDN.
 */
public class ParseUtil {
    private static Logger logger = LogManager.getLogger(ParseUtil.class.getName());

    /**
     * Parsing the xml files to generate a Document object.
     */
    public static Document parse(String fileName) {
        Document doc = null;
        try {
            File file = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
        } catch (Exception e) {
            logger.error("Exception occurred when parsing " + fileName, e);
        }
        return doc;
    }

    /**
     * Converts a comma separated list into a string list
     * @param str, comma separated list
     * @return List<String>
     */
    public static List<String> convertCommaSeparatedListIntoStringList(String str) {
        return Arrays.asList(str.split("\\s*,\\s*"));
    }

    /**
     * Converts a comma separated list of numbers into an integer list
     * @param str, comma separated list
     * @return List<Integer>
     */
    public static List<Integer> convertCommaSeparatedListIntoIntegerList(String str) {
        List<String> items = convertCommaSeparatedListIntoStringList(str);
        List<Integer> intList = new ArrayList<>();
        for(String s : items) intList.add(Integer.valueOf(s));
        return intList;
    }

    /**
     * Converts a comma separated list into a string array
     * @param str, comma separated list
     * @return String[]
     */
    public static String[] convertCommaSeparatedListIntoStringArray(String str) {
        return str.split("\\s*,\\s*");
    }

    /**
     * Gets the value of the first element as a string array
     * @param element DOM Node
     * @param tag tag of whose value to be found
     * @return the value String array
     */
    public static String[] getFirstElementValueAsStringArray(Node element, String tag) {
        String str = getFirstElementValue(element, tag);
        return convertCommaSeparatedListIntoStringArray(str);
    }

    /**
     * Gets the value of the first element
     * @param element DOM Node
     * @param tag tag of whose value to be found
     * @return the value String
     */
    public static String getFirstElementValue(Node element, String tag) {
        NodeList list = ((Element) element).getElementsByTagName(tag);
        if (list.item(0)!= null) {
            return list.item(0).getTextContent();
        } else {
            return "";
        }
    }

    /**
     * Gets the last character from a string
     * @param str the string
     * @return the last string
     */
    public static char getLastCharacter(String str) {
        return str.charAt(str.length() - 1);
    }

    /**
     * Truncates the last character off from a string, and returns the sub string
     * @param str the original string
     * @return the truncated string
     */
    public static String getStringLastCharTruncated(String str) {
        return str.substring(0, str.length() - 1);
    }
}
