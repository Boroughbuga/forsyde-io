package forsyde.io.java.types.vertex;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import forsyde.io.java.core.Port;
{% if not type_data or not type_data['superTypes'] %}
import forsyde.io.java.core.Vertex;
{% endif %}
{% if type_data and 'required_ports' in type_data %}
import forsyde.io.java.core.ForSyDeModel;
{% endif %}

{% if type_data and type_data['superTypes'] %}
public class {{type_name}} extends {{type_data['superTypes'][0]}} {
{% else %}
public class {{type_name}} extends Vertex {
{% endif %}

    /**
     * Automatically generated constructor for specific
     * Vertex class '{{type_name}}'
     *
	 * @param identifier the obligatory unique ID for this vertex.
     */
    public {{type_name}}(String identifier) {
         super(identifier);
    }
 
    /**
     * Automatically generated constructor for specific
     * Vertex class '{{type_name}}'
     *
	 * @param identifier The obligatory unique ID for this vertex.
	 * @param ports      The list (set-like, no duplicates) of ports for this
	 *                   vertex.
	 * @param properties The mapping (associative array) of properties for this
	 *                   vertex. Remember that it should be a tree of primitive
	 *                   types such as Integers, Floats, Strings etc.
     */
    public {{type_name}}(String identifier, List<Port> ports, Map<String, Object> properties) {
         super(identifier, ports, properties);
    }

    @Override
    public String getTypeName() {
        return "{{type_name}}";
    };

    {% if type_data and 'required_ports' in type_data %}
    {% for req_port, req_port_data in type_data['required_ports'].items() %}
    public Port getPort{{req_port | capitalize }}() {
        return getPort("{{req_port}}").get();
    }
    
    public {{req_port_data | javify}} get{{req_port | capitalize }}() {
        {% if req_port_data['default'] %}
        return {{req_port_data['default']}};
        {% else %}
        return null;
        {% endif %}
    }
    {% endfor %}
    {% endif %}

    {% if type_data and 'required_properties' in type_data %}
    {% for req_property, req_property_data in type_data['required_properties'].items() %}
    public {{req_property_data | javify}} get{{req_property | snake_to_pascal }}() 
    {%- if not 'default' in req_property_data -%}
    throws IllegalStateException
    {%- endif %} {
        if (properties.containsKey("{{req_property}}")) {
            return ({{req_property_data | javify}}) properties.get("{{req_property}}");
        } else {
            {%- if 'default' in req_property_data %}
            {{req_property_data['default'] | javify_value(req_property_data, 'value')}}
            return value;
            {%- else %}
            throw new IllegalStateException("Object of type '{{type_name}}' has no required property '{{req_property | snake_to_pascal }}'");
            {%- endif %}
        }
    }
    {% endfor %}
    {% endif %}
}
