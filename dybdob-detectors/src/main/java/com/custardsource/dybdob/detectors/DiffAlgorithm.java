package com.custardsource.dybdob.detectors;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import difflib.DiffUtils;
import difflib.Patch;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class DiffAlgorithm {
    public static class TextAlgorithm extends DiffAlgorithm {
        @Override
        public String diff(String oldContent, String newContent) {
            Splitter splitter = Splitter.on(CharMatcher.anyOf("\r\n"));

            List<String> oldLines = Lists.newArrayList(splitter.split(oldContent));
            List<String> newLines = Lists.newArrayList(splitter.split(newContent));
            Patch diffs = DiffUtils.diff(oldLines, newLines);
            return Joiner.on("\n").join(DiffUtils.generateUnifiedDiff(DIFF_OLD_FILE_NAME, DIFF_NEW_FILE_NAME, oldLines,
                    diffs, DIFF_CONTEXT_LINES));
        }
    }

    public static class XmlDiffAlgorithm extends DiffAlgorithm {
        private final List<String> warningNodeNames;
        private final List<String> ignorableAttributeNames = ImmutableList.of("lineNumber");
        private static final Logger LOG = Logger.getLogger(XmlDiffAlgorithm.class.getName());

        public XmlDiffAlgorithm(String... warningNodeNames) {
            this.warningNodeNames = ImmutableList.copyOf(warningNodeNames);
        }

        @Override
        public String diff(String oldContent, String newContent) {
            Diff diff = null;
            try {
                diff = new Diff(oldContent, newContent);
            } catch (SAXException e) {
                throw new RuntimeException("Unable to parse XML", e);
            } catch (IOException e) {
                throw new RuntimeException("Unable to parse XML", e);
            }
            DetailedDiff detailed = new DetailedDiff(diff);
            List<Difference> differences =  detailed.getAllDifferences();
            StringBuilder result = new StringBuilder();
            Set<String> debuggedXPaths = Sets.newHashSet();
            for (Difference difference : differences) {
                if (isApparentNewWarning(difference, debuggedXPaths)) {
                    Node relevantNode = getElementNode(difference.getTestNodeDetail().getNode());
                    try {
                        if (difference.getControlNodeDetail().getNode() == null) {
                            result.append("Apparent new error: " + difference + "\n  ");
                        } else {
                            result.append("Modified error " + difference + ": \n");
                        }
                        debugNode(result, relevantNode);
                        if (difference.getControlNodeDetail().getNode() != null) {
                            result.append("was : \n");
                            debugNode(result, getElementNode(difference.getControlNodeDetail().getNode()));
                        }
                        result.append("\n");
                    } catch (TransformerException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    LOG.severe("Ignoring diff: " + difference.getId() + ": " + difference);
                }
            }
            return result.toString();
        }

        private void debugNode(StringBuilder result, Node relevantNode) throws TransformerException {
            result.append(nodeToText(relevantNode, false))
                    .append("\n");
            Node parent = relevantNode.getParentNode();
            while (parent != null && !(parent instanceof Document)) {
                result.append("  in: ").append(nodeToText(parent, true)).append("\n");
                parent = parent.getParentNode();
            }
        }

        private boolean isApparentNewWarning(Difference difference, Set<String> debuggedXPaths) {
            if (difference.getTestNodeDetail().getNode() == null) {
                return false;
            }
            boolean isWarningNode = warningNodeNames.contains(getElementNode(difference.getTestNodeDetail().getNode()).getNodeName());
            if (!isWarningNode) {
                return false;
            }
            if (ignorableAttributeNames.contains(difference.getTestNodeDetail().getNode().getNodeName())) {
                return false;
            }
            if (!isRelevantWarningType(difference)) {
                return false;
            }

            String relevantXPath = getRelevantXPath(difference.getTestNodeDetail().getXpathLocation());
            System.err.println(relevantXPath);
            return debuggedXPaths.add(relevantXPath);
        }

        private boolean isRelevantWarningType(Difference difference) {
            if (difference.getId() == DifferenceConstants.CHILD_NODE_NOT_FOUND_ID && difference.getControlNodeDetail().getNode() == null) {
                return true;
            }
            if (difference.getId() == DifferenceConstants.ATTR_VALUE_ID) {
                return true;
            }
            return false;
        }

        private String getRelevantXPath(String xpathLocation) {
            return xpathLocation.replaceAll("/@[^/@]+$", "");
        }

        private Node getElementNode(Node node) {
            if (node instanceof Attr) {
                return ((Attr)node).getOwnerElement();
            }
            return node;
        }

        private String nodeToText(Node node, boolean omitChildren) throws TransformerException {
            Node cloned = node.cloneNode(true);
            if (omitChildren && cloned.hasChildNodes()) {
                NodeList n = cloned.getChildNodes();
                for (int i = n.getLength() - 1; i >= 0; i--) {
                    cloned.removeChild(n.item(i));
                }
            }
            
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter sw = new StringWriter();
            t.transform(new DOMSource(cloned), new StreamResult(sw));
            return sw.toString();
        }
    };

    private static final int DIFF_CONTEXT_LINES = 5;
    private static final String DIFF_NEW_FILE_NAME = "new";
    private static final String DIFF_OLD_FILE_NAME = "old";

    public abstract String diff(String oldContent, String newContent);

}
