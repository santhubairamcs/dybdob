package com.custardsource.dybdob.detectors;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
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
import com.google.common.collect.Lists;
import difflib.DiffUtils;
import difflib.Patch;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
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
        private static final Logger LOG = Logger.getLogger(XmlDiffAlgorithm.class.getName());

        public XmlDiffAlgorithm() {
            this.warningNodeNames = Collections.emptyList();
        }

        public XmlDiffAlgorithm(List<String> warningNodeNames) {
            this.warningNodeNames = warningNodeNames;
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
            for (Difference difference : differences) {
                if (isApparentNewWarning(difference)) {
                    try {
                        System.err.println("* Apparent new error: " + nodeToText(difference.getTestNodeDetail().getNode(), false));
                        Node parent = difference.getTestNodeDetail().getNode().getParentNode();
                        while (parent != null && !(parent instanceof Document)) {
                            System.err.println("  in: " + nodeToText(parent, true));
                            parent = parent.getParentNode();
                        }
                    } catch (TransformerException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    LOG.severe("Ignoring diff: " + difference);
                }
            }
            return "";
        }

        private boolean isApparentNewWarning(Difference difference) {
            return (difference.getId() == DifferenceConstants.CHILD_NODE_NOT_FOUND_ID) && difference.getControlNodeDetail().getNode() == null
                    && warningNodeNames.contains(difference.getTestNodeDetail().getNode().getNodeName());
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


    public static void main(String[] args) {
        String oldText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><BugCollection version='1.3.9' threshold='low' effort='min'><file classname='Access'><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Normal' category='STYLE' message='Dead store to foos in Access.fooTest()' lineNumber='5'/><BugInstance type='NP_UNWRITTEN_FIELD' priority='Normal' category='CORRECTNESS' message='Read of unwritten field foo in Access.fooTest()' lineNumber='5'/><BugInstance type='UWF_UNWRITTEN_FIELD' priority='Normal' category='CORRECTNESS' message='Unwritten field: Access.foo' lineNumber='5'/></file><file classname='Thing'><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='High' category='STYLE' message='Dead store to list in Thing.thing2()' lineNumber='63'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='High' category='STYLE' message='Dead store to list2 in Thing.thing2()' lineNumber='64'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Normal' category='STYLE' message='Dead store to list in Thing.thing()' lineNumber='56'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Low' category='STYLE' message='Dead store to list in Thing.thing()' lineNumber='3'/></file><file classname='ThingA'><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='High' category='STYLE' message='Dead store to list in ThingA.thing()' lineNumber='4'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Normal' category='STYLE' message='Dead store to list in ThingA.thing()' lineNumber='3'/></file><Error></Error><Project><SrcDir>/home/pcowan/intellij-workspace/maventest/left/src/main/java</SrcDir></Project></BugCollection>";
        String newText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><BugCollection version='1.3.9' threshold='low' effort='min'><file classname='Access'><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Normal' category='STYLE' message='Dead store to foos in Access.fooTest()' lineNumber='5'/><BugInstance type='NP_UNWRITTEN_FIELD' priority='Normal' category='CORRECTNESS' message='Read of unwritten field foo in Access.fooTest()' lineNumber='5'/><BugInstance type='UWF_UNWRITTEN_FIELD' priority='Normal' category='CORRECTNESS' message='Unwritten field: Access.foo' lineNumber='5'/></file><file classname='Thing'><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='High' category='STYLE' message='Dead store to list in Thing.thing2()' lineNumber='64'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='High' category='STYLE' message='Dead store to list2 in Thing.thing2()' lineNumber='65'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Normal' category='STYLE' message='Dead store to list in Thing.thing()' lineNumber='57'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Low' category='STYLE' message='Dead store to list in Thing.thing()' lineNumber='4'/></file><file classname='ThingA'><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='High' category='STYLE' message='Dead store to list in ThingA.thing()' lineNumber='4'/><BugInstance type='DLS_DEAD_LOCAL_STORE' priority='Normal' category='STYLE' message='Dead store to list in ThingA.thing()' lineNumber='3'/><BugInstance type='DM_STRING_TOSTRING' priority='Low' category='PERFORMANCE' message='Method ThingA.thing() invokes toString() method on a String' lineNumber='6'/><BugInstance type='NP_ALWAYS_NULL' priority='High' category='CORRECTNESS' message='Null pointer dereference of foo in ThingA.thing()' lineNumber='6'/><BugInstance type='RV_RETURN_VALUE_IGNORED' priority='Low' category='CORRECTNESS' message='ThingA.thing() ignores return value of String.toString()' lineNumber='6'/></file><file classname='Test' /><Error></Error><Project><SrcDir>/home/pcowan/intellij-workspace/maventest/left/src/main/java</SrcDir></Project></BugCollection>";
        new XmlDiffAlgorithm(Collections.singletonList("BugInstance")).diff(oldText, newText);
    }
}
