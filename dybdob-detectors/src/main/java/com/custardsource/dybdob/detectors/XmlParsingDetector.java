package com.custardsource.dybdob.detectors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public abstract class XmlParsingDetector extends AbstractDetector {
    private final Map<String, String> metricXPathPatterns;

    protected XmlParsingDetector(String detectorName, DiffAlgorithm diffAlgorithm, Map<String, String> metricXPathPatterns) {
        super(detectorName, diffAlgorithm);
        this.metricXPathPatterns= metricXPathPatterns;
    }

    @Override
    protected Map<String, Integer> getResultsFrom(File log) {
        try {
            return getResultsFrom(new FileInputStream(log));
        } catch (FileNotFoundException e) {
            throw new CountException("Could not parse XML " + log, e);
        }
    }

    protected Map<String, Integer> getResultsFrom(InputStream log) {

        Map<String, Integer> results = Maps.newHashMap();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(log);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            for (Map.Entry<String, String> pattern : metricXPathPatterns.entrySet()) {
                results.put(pattern.getKey(), countMatches(pattern.getValue(), xpath, document));
            }

        } catch (ParserConfigurationException e) {
            throw new CountException("Could not parse XML " + log, e);
        } catch (SAXException e) {
            throw new CountException("Could not parse XML " + log, e);
        } catch (IOException e) {
            throw new CountException("Could not parse XML " + log, e);
        } catch (XPathExpressionException e) {
            throw new CountException("Could not parse XML " + log, e);
        }
        return results;
    }

    private int countMatches(String query, XPath xpath, Document document) throws XPathExpressionException {
        NodeList results = (NodeList) xpath.evaluate(query, document, XPathConstants.NODESET);
        return results == null ? 0 : results.getLength();
    }

}
