package forsyde.io.java.typed.prototypes;

import forsyde.io.java.core.Vertex;
import forsyde.io.java.core.VertexTrait;
import forsyde.io.java.typed.interfaces.RoundRobinSchedulerPrototype;
import java.lang.Boolean;

public final class RoundRobinScheduler extends Vertex implements RoundRobinSchedulerPrototype {
  public static Boolean conforms(Vertex vertex) {
    return vertex.vertexTraits.contains(VertexTrait.RoundRobinSchedulerTrait);
  }
}
