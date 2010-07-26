package com.custardsource.dybdob.detectors;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.google.common.collect.Maps;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FindBugsDetector extends AbstractDetector {
    public FindBugsDetector() {
        super("findbugs", DiffAlgorithm.XML);
    }

    @Override
    protected Map<String, Integer> getResultsFrom(File log) {
        
        Map<String, Integer> results = Maps.newHashMap();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(log);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            results.put("all", countMatches("//BugInstance", xpath, document));
            results.put("high", countMatches("//BugInstance[@priority='High']", xpath, document));
            results.put("normal", countMatches("//BugInstance[@priority='Normal']", xpath, document));
            results.put("low", countMatches("//BugInstance[@priority='Low']", xpath, document));

        } catch (ParserConfigurationException e) {
            throw new CountException("Could not parse findbugs XML " + log, e);
        } catch (SAXException e) {
            throw new CountException("Could not parse findbugs XML " + log, e);
        } catch (IOException e) {
            throw new CountException("Could not parse findbugs XML " + log, e);
        } catch (XPathExpressionException e) {
            throw new CountException("Could not parse findbugs XML " + log, e);
        }
        return results;
    }

    private int countMatches(String query, XPath xpath, Document document) throws XPathExpressionException {
        NodeList results = (NodeList) xpath.evaluate(query, document, XPathConstants.NODESET);
        return results == null ? 0 : results.getLength();
    }
}