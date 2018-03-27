package com.custardsource.dybdob.detectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FindBugsDetector extends AbstractDetector {
    public FindBugsDetector(String id) {
        super(id);
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

            Node summaryNode = getSummaryNode(xpath, document);
            results.put("all", getIntAttribute(summaryNode, "total_bugs"));
            results.put("high", getIntAttribute(summaryNode, "priority_1"));
            results.put("normal", getIntAttribute(summaryNode, "priority_2"));
            results.put("low", getIntAttribute(summaryNode, "priority_3"));

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

    private Integer getIntAttribute(Node node, String attributeName) {
        final Node attribute = node.getAttributes().getNamedItem(attributeName);
        return attribute != null ? Integer.valueOf(attribute.getTextContent()) : 0;
    }

    private Node getSummaryNode(XPath xpath, Document document) throws XPathExpressionException {
        NodeList summaryNodes = (NodeList) xpath.evaluate("//FindBugsSummary", document, XPathConstants.NODESET);
        Preconditions.checkArgument(summaryNodes.getLength() == 1, "Invalid length for summary node list %d", summaryNodes.getLength());
        return summaryNodes.item(0);
    }

    @Override
    protected String readOutputFrom(File log) {
        return super.readOutputFrom(log).replaceAll("<", "\n<");
    }
}