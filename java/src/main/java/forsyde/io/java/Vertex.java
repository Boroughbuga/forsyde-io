/**
 * 
 */
package forsyde.io.java;

import java.util.Objects;
import java.util.Set;

import lombok.Builder.Default;
import lombok.ToString;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rjordao
 *
 */
public class Vertex {
	
	public String identifier;
	public ModelType type;
	public Set<Port> ports = new HashSet<Port>();
	public Map<String, Object> properties = new HashMap<String, Object>();

	/**
	 * @param identifier
	 * @param type
	 */
	public Vertex(String identifier, ModelType type) {
		this.identifier = identifier;
		this.type = type;
	}

	/**
	 * @param identifier
	 * @param type
	 * @param ports
	 * @param properties
	 */
	public Vertex(String identifier, ModelType type, Set<Port> ports, Map<String, Object> properties) {
		this.identifier = identifier;
		this.type = type;
		this.ports = ports;
		this.properties = properties;
	}

	@Override
	public String toString() {
		final int maxLen = 5;
		StringBuilder builder = new StringBuilder();
		builder.append("Vertex [identifier=").append(identifier).append(", type=").append(type).append(", ports=")
				.append(ports != null ? toString(ports, maxLen) : null).append(", properties=")
				.append(properties != null ? toString(properties.entrySet(), maxLen) : null).append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Vertex)) {
			return false;
		}
		Vertex other = (Vertex) obj;
		return Objects.equals(identifier, other.identifier);
	}
	

}