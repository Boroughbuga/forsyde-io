from typing import Dict
from typing import List
from typing import Sequence
from enum import Enum
from enum import auto

import forsyde.io.python.core as core


class VertexTrait(Enum):
{%- for type_name, type_data in vertexTraits.items() %}
    {{type_name}} = auto()
{%- endfor %}


_traits_vertex = [
{%- for type_name, type_data in vertexTraits.items() %}
{%- for trait_name in type_data['superTraits'] %}
    (VertexTrait.{{type_name}}, VertexTrait.{{trait_name}}),
{%- endfor %}
{%- endfor %}
]

def issupertrait_vertex(t1: VertexTrait, t2: VertexTrait) -> bool:
    if t1 is t2:
        return True
    else:
        return any(issupertrait_vertex(parent, t2) for (t, parent) in _traits_vertex if t is t1)


class EdgeTrait(Enum):
{%- for type_name, type_data in edgeTraits.items() %}
    {{type_name}} = auto()
{%- endfor %}


_traits_edges = [
{%- for type_name, type_data in edgeTraits.items() %}
{%- for trait_name in type_data['superTraits'] %}
    (EdgeTrait.{{type_name}}, EdgeTrait.{{trait_name}}),
{%- endfor %}
{%- endfor %}
]

def issupertrait_edge(t1: EdgeTrait, t2: EdgeTrait) -> bool:
    if t1 is t2:
        return True
    else:
        return any(issupertrait_edge(parent, t2) for (t, parent) in _traits_edges if t is t1)

{# {% for type_name, type_data in vertexTraits.items() %}
{% if type_data['superTraits'] %}
class {{type_name}}({{type_data['superTraits'] | join(', ') }}):
{% else %}
class {{type_name}}(core.Vertex):
{% endif %}
    """
    This class has been generated automatically from the ForSyDe IO types model.
    Any code in this file may be overwritten without notice, so it's best not to spend
    time modifying anything here, but on the true source which is a model file.
    
    {{type_data['doc'] if type_data and 'doc' in type_data else ''}}
    """

    def get_type_tag(self) -> str:
	        return "{{type_name}}"
            
    {% if type_data and 'required_ports' in type_data %}
    {% for req_port, req_port_data in type_data['required_ports'].items() %}
    def get_port_{{req_port}}(self) -> core.Port:
        return self.get_port("{{req_port}}")

    {% if 'multiple' in req_port_data and req_port_data['multiple'] %}
    def get_{{req_port}}(self, model) -> Sequence["{{req_port_data['class']}}"]:
        return self.get_neighs("{{req_port}}", model)
    {% else %}
    def get_{{req_port}}(self, model) -> "{{req_port_data['class']}}":
        return self.get_neigh("{{req_port}}", model)
    {% endif %}

    {% endfor %}
    {% endif -%}
    {% if type_data and 'required_properties' in type_data %}
    {% for req_property, req_property_data in type_data['required_properties'].items() -%}
    def get_{{req_property}}(self) -> {{req_property_data | pythonify }}:
    {%- if 'default' in req_property_data %}
        return self.properties["{{req_property}}"] if '{{req_property}}' in self.properties else {{req_property_data['default']}}
    {%- else %}
        try:
            return self.properties["{{req_property}}"]
        except KeyError:
            raise AttributeError(f"Vertex {self.identifier} has no required '{{req_property}}' property.")
    {%- endif %}

    {% endfor %}
    {% endif %}
{% endfor %}

{% for type_name, type_data in edgeTraits.items() %}
{% if type_data['superTraits'] %}
class {{type_name}}({{type_data['superTraits'] | join(', ') }}):
{% else %}
class {{type_name}}(core.Edge):
{% endif %}


    def get_type_tag(self) -> str:
        return "{{type_name}}"
{% endfor %}

class VertexFactory:
    """
    This class is auto generated.
    It enables import and export of ForSyDe-IO type models by stringification.
    """

    str_to_classes = {
        {%- for type_name, type_data in vertexTraits.items() %}
        "{{type_name}}": {{type_name}}{{',' if not loop.last}}
        {% endfor -%}
    }
    
    @classmethod
    def get_type_from_name(cls,
                    type_name: str
                    ) -> type:
        if type_name in cls.str_to_classes:
            return cls.str_to_classes[type_name]
        raise NotImplementedError(
            f"The type '{type_name}' is not recognized."
        )


    @classmethod
    def build(
        cls,
        identifier: str,
        type_name: str,
        ports = None,
        properties = None
        ) -> core.Vertex:
        try:
            vtype = cls.get_type_from_name(type_name)
            return vtype(
                identifier=identifier,
                ports=ports if ports else set(),
                properties=properties if properties else dict()
            )
        except KeyError: 
            raise NotImplementedError(
                f"The Vertex type '{type_name}' is not recognized."
            )

class EdgeFactory:
    """
    This class is auto generated.
    It enables import and export of ForSyDe-IO type models by stringification.
    """

    str_to_classes = {
        {%- for type_name, type_data in edgeTraits.items() %}
        "{{type_name}}": {{type_name}}{{',' if not loop.last}}
        {% endfor -%}
    }

    @classmethod
    def get_type_from_name(cls,
                    type_name: str
                    ) -> type:
        if type_name in cls.str_to_classes:
            return cls.str_to_classes[type_name]
        raise NotImplementedError(
            f"The type '{type_name}' is not recognized."
        )

    @classmethod
    def build(
        cls,
        source: core.Vertex,
        target: core.Vertex,
        type_name: str
    ) -> core.Edge:
        try:
            etype = cls.get_type_from_name(type_name)
            return etype(
                source_vertex = source,
                target_vertex = target
            )
        except KeyError:
            raise NotImplementedError(
                f"The Edge type '{type_name}' is not recognized."
            ) #}
