/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.builders;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import pt.inesc_id.gsd.ravana.constants.XSDNConstants;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.network.XSDNNode;

/**
 * Reads the network.xml using a DOM parser
 */
public class NetworkBuilder implements Parser {
    private static Logger logger = LogManager.getLogger(NetworkBuilder.class.getName());


    @Override
    public void parse() {
        String fileName = XSDNConstants.CONF_FOLDER + File.separator + XSDNConstants.NETWORK_XML;
        parse(ParseUtil.parse(fileName));
    }

    @Override
    public void parse(String document) {
        parse(ParseUtil.parse(document));
    }

    @Override
    public void parse(Document doc) {
        Map<String, Double> tempPropertiesMap;
        XSDNNode xsdnNode;

        NodeList nodeLst = doc.getElementsByTagName("node");
        for (int s = 0; s < nodeLst.getLength(); s++) {
            Node node = nodeLst.item(s);
            NamedNodeMap attr = node.getAttributes();
            Node nodeAttr = attr.getNamedItem("id");
            String idVal = nodeAttr.getNodeValue();

            /**
             * Initialize a XSDN node
             */
            xsdnNode = new XSDNNode();

            if (logger.isTraceEnabled()) {
                logger.trace("---------------------------------\nNode: " + idVal +
                        "\n---------------------------------");
            }
            NodeList linkList = ((Element) node).getElementsByTagName("link");


            for (int l = 0; l < linkList.getLength(); l++) {
                Node link = linkList.item(l);
                NamedNodeMap attrLink = link.getAttributes();
                Node linkAttr = attrLink.getNamedItem("id");
                String linkId = linkAttr.getNodeValue();

                if (logger.isTraceEnabled()) {
                    logger.trace("Link: " + linkId);
                }
                NodeList properties = link.getChildNodes();
                tempPropertiesMap = new HashMap<>();

                for (int p = 0; p < properties.getLength(); p++) {
                    Node property = properties.item(p);

                    String pKey = property.getNodeName();
                    double pVal = Double.parseDouble(property.getTextContent());
                    tempPropertiesMap.put(pKey, pVal);

                    if (logger.isTraceEnabled()) {
                        logger.trace(pKey + ": " + pVal);
                    }
                }
                xsdnNode.addNextNode(linkId, tempPropertiesMap);
            }

            XSDNCore.addXSDNNode(idVal, xsdnNode);
        }
    }
}

