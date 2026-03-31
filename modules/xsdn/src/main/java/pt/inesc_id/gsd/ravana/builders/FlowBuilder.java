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

import java.io.File;

/**
 * Building the flows from the flows configuration file.
 */
public class FlowBuilder implements Parser {
    private static Logger logger = LogManager.getLogger(FlowBuilder.class.getName());

    @Override
    public void parse() {
        String fileName = XSDNConstants.CONF_FOLDER + File.separator + XSDNConstants.FLOWS_XML;
        parse(ParseUtil.parse(fileName));
    }

    public void parse(String fileName) {
        parse(ParseUtil.parse(fileName));
    }

    @Override
    public void parse(Document doc) {
        NodeList flowList = doc.getElementsByTagName("flow");
        for (int s = 0; s < flowList.getLength(); s++) {
            Node flow = flowList.item(s);
            NamedNodeMap attr = flow.getAttributes();
            Node nodeAttr = attr.getNamedItem("id");
            String idVal = nodeAttr.getNodeValue();

            Node profileAttr = attr.getNamedItem("profile");
            String profileVal = profileAttr != null ? profileAttr.getNodeValue() : "time";

            if (logger.isTraceEnabled()) {
                logger.trace("---------------------------------\nFlow: " + idVal +
                        "\n---------------------------------");
            }


            double start = 0;
            String[] chunkArray = new String[0];
            if (!(ParseUtil.getFirstElementValue(flow, "start").trim().equalsIgnoreCase(""))) {
                start = Double.parseDouble(ParseUtil.getFirstElementValue(flow, "start"));
            }

            String chunks = ParseUtil.getFirstElementValue(flow, "chunks");
            String origin = ParseUtil.getFirstElementValue(flow, "origin");
            String destination = ParseUtil.getFirstElementValue(flow, "destination");

            if (!(ParseUtil.getFirstElementValue(flow, "chunks").trim().equalsIgnoreCase(""))) {
                chunkArray = ParseUtil.getFirstElementValueAsStringArray(flow, "chunks");
            }
            Node algorithmAttr = attr.getNamedItem("algorithm");
            String algorithmVal = algorithmAttr != null ? algorithmAttr.getNodeValue() : null;

            LanguageParser.buildxSDNFlows(idVal, start, chunks, origin, destination, chunkArray, profileVal, algorithmVal);


        }
    }

}
