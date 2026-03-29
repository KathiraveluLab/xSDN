/*
 * Title:        xSDN
 * Description:  An Extended Platform for Software-Defined Networking.
 * Licence:      Eclipse Public License - v 1.0 - https://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */
package pt.inesc_id.gsd.ravana.builders;

import org.w3c.dom.Document;

/**
 * The Builder Interface for xSDN.
 */
public interface Parser {
    /**
     * Parses the xml document
     */
    public void parse();

    /**
     * Parses the xml document
     */
    public void parse(String document);

    /**
     * Parses the given Document object
     * @param doc, xml document
     */
    public void parse(Document doc);
}
