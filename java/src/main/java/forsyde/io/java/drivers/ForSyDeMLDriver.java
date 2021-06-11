/**
 * 
 */
package forsyde.io.java.drivers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import forsyde.io.java.core.Edge;
import forsyde.io.java.core.EdgeTrait;
import forsyde.io.java.core.ForSyDeModel;
import forsyde.io.java.core.Port;
import forsyde.io.java.core.Vertex;
import forsyde.io.java.core.VertexTrait;

/**
 * @author rjordao
 *
 */
public class ForSyDeMLDriver extends ForSyDeModelDriver {

	/**
	 * Parses ForSyDe's graphML varatiation schema.
	 * 
	 * @param inStream the {@link InputStream} to fetch the model from.
	 * @return the parsed {@link ForSyDeModel} from whichever format was fed to the
	 *         function.
	 * @throws ParserConfigurationException TODO.
	 * @throws SAXException                 In case something goes wrong with any
	 *                                      XML input parsing.
	 * @throws IOException                  In case something goes wrong with the
	 *                                      input strema.
	 * @throws XPathExpressionException     The XPaths are statically compiled, so
	 *                                      this shound not normally happen.
	 */
	public ForSyDeModel loadModel(InputStream inStream)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		// Build the ForSyDeModel representation
		ForSyDeModel model = new ForSyDeModel();
		// prepare XML internal representation and build it
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document xmlDoc = builder.parse(inStream);
		// get the XPath object
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList vertexList = (NodeList) xPath.compile("/graphml//graph/node").evaluate(xmlDoc, XPathConstants.NODESET);
		for (int i = 0; i < vertexList.getLength(); i++) {
			Element vertexElem = (Element) vertexList.item(i);
			// TODO: the type creation could be safer or signal some exception
			Vertex vertex = new Vertex(vertexElem.getAttribute("id"));
			vertex.vertexTraits = Stream.of(vertexElem.getAttribute("traits").split(";"))
					.map(s -> VertexTrait.valueOf(s)).collect(Collectors.toSet());
			model.addVertex(vertex);
			// iterate through ports and add them
			NodeList portsList = (NodeList) xPath.compile("port").evaluate(vertexElem, XPathConstants.NODESET);
			for (int j = 0; j < portsList.getLength(); j++) {
				Element portElem = (Element) portsList.item(j);
				vertex.ports.add(portElem.getAttribute("name"));
			}
			// iterate through properties and add them
			NodeList propertyList = (NodeList) xPath.compile("data").evaluate(vertexElem, XPathConstants.NODESET);
			for (int j = 0; j < propertyList.getLength(); j++) {
				Element propertyElem = (Element) propertyList.item(j);
				// TODO: assure safety of this call
				Object property = readData(propertyElem);
				vertex.properties.put(propertyElem.getAttribute("attr.name"), property);
			}
		}
		NodeList edgeList = (NodeList) xPath.compile("/graphml//graph/edge").evaluate(xmlDoc, XPathConstants.NODESET);
		for (int i = 0; i < edgeList.getLength(); i++) {
			Element edgeElem = (Element) edgeList.item(i);
			String sid = edgeElem.getAttribute("source");
			String tid = edgeElem.getAttribute("target");
			// TODO: later put more safety here, even though for consistency should never
			// fail
			Vertex source = model.vertexSet().stream().filter(v -> v.identifier.equals(sid)).findFirst().get();
			Vertex target = model.vertexSet().stream().filter(v -> v.identifier.equals(tid)).findFirst().get();
			Edge edge = new Edge(source, target);
			edge.edgeTraits = Stream.of(edgeElem.getAttribute("traits").split(";")).map(s -> EdgeTrait.valueOf(s))
					.collect(Collectors.toSet());
			if (edgeElem.hasAttribute("sourceport")) {
				String sourcePort = source.ports.stream().filter(p -> p.equals(edgeElem.getAttribute("sourceport")))
						.findFirst().get();
				edge.sourcePort = Optional.of(sourcePort);
			}
			if (edgeElem.hasAttribute("targetport")) {
				String targetPort = target.ports.stream().filter(p -> p.equals(edgeElem.getAttribute("targetport")))
						.findFirst().get();
				edge.targetPort = Optional.of(targetPort);
			}
			model.addEdge(source, target, edge);
		}
		return model;
	}

	public void writeModel(ForSyDeModel model, OutputStream outStream)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("graphml");
		root.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		Element graph = doc.createElement("graph");
		graph.setAttribute("id", "model");
		graph.setAttribute("edgedefault", "directed");
		root.appendChild(graph);
		doc.appendChild(root);
		for (Vertex v : model.vertexSet()) {
			Element vElem = doc.createElement("node");
			vElem.setAttribute("id", v.identifier);
			vElem.setAttribute("traits",
					v.vertexTraits.stream().map(t -> t.getName()).reduce("", (s1, s2) -> s1 + ";" + s2));
			graph.appendChild(vElem);
			for (String p : v.ports) {
				Element pElem = doc.createElement("port");
				pElem.setAttribute("name", p);
				vElem.appendChild(pElem);
			}
			for (String key : v.properties.keySet()) {
				Element propElem = writeData(doc, v.properties.get(key));
				propElem.setAttribute("attr.name", key);
				vElem.appendChild(propElem);
			}
		}
		for (Edge e : model.edgeSet()) {
			Element eElem = doc.createElement("edge");
			eElem.setAttribute("source", e.source.identifier);
			eElem.setAttribute("target", e.target.identifier);
			eElem.setAttribute("traits",
					e.edgeTraits.stream().map(t -> t.getName()).reduce("", (s1, s2) -> s1 + ";" + s2));
			if (e.sourcePort.isPresent()) {
				eElem.setAttribute("sourceport", e.sourcePort.get());
			}
			if (e.targetPort.isPresent()) {
				eElem.setAttribute("targetport", e.targetPort.get());
			}
			graph.appendChild(eElem);
		}
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(new DOMSource(doc), new StreamResult(outStream));
	}

	/**
	 * Function to recursively build an data object for a {@link Vertex} object.
	 * 
	 * @param elem the XML element being parsed.
	 * @return the parsed object.
	 */
	static protected Object readData(Element elem) {
		// it is a collection
		if (elem.getAttribute("attr.type").equals("integer")) {
			return Integer.valueOf(elem.getTextContent());
		} else if (elem.getAttribute("attr.type").equals("int")) {
			return Integer.valueOf(elem.getTextContent());
		} else if (elem.getAttribute("attr.type").equals("float")) {
			return Float.valueOf(elem.getTextContent());
		} else if (elem.getAttribute("attr.type").equals("double")) {
			return Double.valueOf(elem.getTextContent());
		} else if (elem.getAttribute("attr.type").equals("boolean")) {
			return Boolean.valueOf(elem.getTextContent());
		} else if (elem.getAttribute("attr.type").equals("object")) {
			HashMap<String, Object> object = new HashMap<String, Object>();
			NodeList children = elem.getElementsByTagName("data");
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				object.put(child.getAttribute("attr.name"), readData(child));
			}
			return object;
		} else if (elem.getAttribute("attr.type").equals("array")) {
			NodeList children = elem.getElementsByTagName("data");
			ArrayList<Object> array = new ArrayList<Object>(children.getLength());
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				array.set(Integer.valueOf(child.getAttribute("attr.name")), readData(child));
			}
			return array;
		} else {
			return elem.getTextContent();
		}
	}

	static protected Element writeData(Document doc, Object value) {
		Element newElem = doc.createElement("data");
		if (value instanceof HashMap) {
			HashMap<String, Object> map = (HashMap<String, Object>) value;
			newElem.setAttribute("attr.type", "object");
			for (String key : map.keySet()) {
				Element child = writeData(doc, map.get(key));
				child.setAttribute("attr.name", key);
				newElem.appendChild(child);
			}
		} else if (value instanceof ArrayList) {
			ArrayList<Object> list = (ArrayList<Object>) value;
			newElem.setAttribute("attr.type", "array");
			for (int i = 0; i < list.size(); i++) {
				Element child = writeData(doc, list.get(i));
				child.setAttribute("attr.name", String.valueOf(i));
				newElem.appendChild(child);
			}
		} else if (value instanceof Integer) {
			newElem.setAttribute("attr.type", "int");
			newElem.setTextContent(value.toString());
		} else if (value instanceof Float) {
			newElem.setAttribute("attr.type", "float");
			newElem.setTextContent(value.toString());
		} else if (value instanceof Double) {
			newElem.setAttribute("attr.type", "double");
			newElem.setTextContent(value.toString());
		} else if (value instanceof Boolean) {
			newElem.setAttribute("attr.type", "boolean");
			newElem.setTextContent(value.toString());
		} else {
			newElem.setAttribute("attr.type", "string");
			newElem.setTextContent(value.toString());
		}
		return newElem;
	}

}
