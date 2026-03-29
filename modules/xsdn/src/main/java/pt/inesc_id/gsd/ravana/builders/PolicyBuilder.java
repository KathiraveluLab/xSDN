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
import org.w3c.dom.*;
import pt.inesc_id.gsd.ravana.constants.XSDNConstants;
import pt.inesc_id.gsd.ravana.core.XSDNCore;
import pt.inesc_id.gsd.ravana.intents.XSDNPolicy;

import java.io.File;

/**
 * Building the policies from the policy configuration file.
 */
public class PolicyBuilder implements Parser {
    private static Logger logger = LogManager.getLogger(PolicyBuilder.class.getName());
    private static XSDNPolicy xsdnPolicy;

    @Override
    public void parse(String document) {
        parse(ParseUtil.parse(document));
    }

    @Override
    public void parse() {
        String fileName = XSDNConstants.CONF_FOLDER + File.separator + XSDNConstants.POLICY_XML;
        parse(ParseUtil.parse(fileName));
    }

    @Override
    public void parse(Document doc) {
        String intent;
        xsdnPolicy = new XSDNPolicy();

        NodeList nodeList = doc.getElementsByTagName("policy");
        for (int s = 0; s < nodeList.getLength(); s++) {
            Node node = nodeList.item(s);
            NamedNodeMap attr = node.getAttributes();

            Node nodeAttr = attr.getNamedItem("profile");
            intent = nodeAttr.getNodeValue();

            if (logger.isTraceEnabled()) {
                logger.trace(intent);
            }

            NodeList properties = node.getChildNodes();

            for (int p = 0; p < properties.getLength(); p++) {
                Node property = properties.item(p);

                String pKey = property.getNodeName();
                String pVal = property.getTextContent();
                xsdnPolicy.addPolicy(pKey, pVal);

                if (logger.isTraceEnabled()) {
                    logger.trace(pKey + ": " + pVal);
                }
            }
            XSDNCore.addXSDNPolicy(intent, xsdnPolicy);
        }
    }
}
