package forsyde.io.java.drivers;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import forsyde.io.java.core.*;
import org.jgrapht.nio.graphml.GraphMLExporter;

public class ForSyDeGraphMLDriver implements ForSyDeModelDriver {

	GraphMLExporter<Vertex, EdgeInfo> graphMLExporter;

	ForSyDeGraphMLDriver() {
		graphMLExporter = new GraphMLExporter<>(Vertex::getIdentifier);
		graphMLExporter.setEdgeIdProvider(EdgeInfo::toIDString);
	}

	@Override
	public List<String> inputExtensions() {
		return List.of();
	}

	@Override
	public List<String> outputExtensions() {
		return List.of("graphml");
	}

	@Override
	@Deprecated
	public ForSyDeSystemGraph loadModel(InputStream in) throws Exception {
		throw new Exception("GraphML reading is not supported.");
	}

	@Override
	public void writeModel(ForSyDeSystemGraph model, OutputStream out) throws Exception {
		graphMLExporter.exportGraph(model, out);
//		List<String> vertexDataNames = new ArrayList<>();
//		List<String> vertexDataTypes = new ArrayList<>();
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document doc = builder.newDocument();
//		Element root = doc.createElement("graphml");
//		root.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
//		Element graph = doc.createElement("graph");
//		graph.setAttribute("id", "model");
//		graph.setAttribute("edgedefault", "directed");
//		root.appendChild(graph);
//		doc.appendChild(root);
//		for (Vertex v : model.vertexSet()) {
//			Element vElem = doc.createElement("node");
//			vElem.setAttribute("id", v.getIdentifier());
//			// vElem.setAttribute("traits", v.vertexTraits.stream().map(t ->
//			// t.getName()).reduce("", (s1, s2) -> s1 + ";" + s2));
//			graph.appendChild(vElem);
//			for (String p : v.getPorts()) {
//				Element pElem = doc.createElement("port");
//				pElem.setAttribute("name", p);
//				vElem.appendChild(pElem);
//			}
//			for (String key : v.getProperties().keySet()) {
//				for (Integer i : writeData(vertexDataNames, vertexDataTypes, key, v.getProperties().get(key))) {
//					Element propElem = doc.createElement("data");
//					propElem.setAttribute("key", "d" + String.valueOf(i));
//					vElem.appendChild(propElem);
//				}
//			}
//		}
//		for (Edge e : model.edgeSet()) {
//			Element eElem = doc.createElement("edge");
//			eElem.setAttribute("source", e.getSource().getIdentifier());
//			eElem.setAttribute("target", e.getTarget().getIdentifier());
//			// eElem.setAttribute("traits", e.edgeTraits.stream().map(t ->
//			// t.getName()).reduce("", (s1, s2) -> s1 + ";" + s2));
//			if (e.getSourcePort().isPresent()) {
//				eElem.setAttribute("sourceport", e.getSourcePort().get());
//			}
//			if (e.getTargetPort().isPresent()) {
//				eElem.setAttribute("targetport", e.getTargetPort().get());
//			}
//			graph.appendChild(eElem);
//		}
//		for (int i = 0; i < vertexDataNames.size(); i++) {
//			Element dataElem = doc.createElement("key");
//			dataElem.setAttribute("for", "node");
//			dataElem.setAttribute("id", "d" + String.valueOf(i));
//			dataElem.setAttribute("attr.type", vertexDataTypes.get(i));
//			dataElem.setAttribute("attr.name", vertexDataNames.get(i));
//			root.appendChild(dataElem);
//		}
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//		transformer.transform(new DOMSource(doc), new StreamResult(out));
	}


}