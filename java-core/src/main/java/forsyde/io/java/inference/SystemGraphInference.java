package forsyde.io.java.inference;

import forsyde.io.java.core.ForSyDeSystemGraph;

/**
 * This interface is very much in line with a validation, except the semantics are different.
 * An inference is simply a way to add deducible information in the model in case it does not exits.
 * It does not imply any validation or equivalent.
 */
public interface SystemGraphInference {

    void infer(ForSyDeSystemGraph forSyDeSystemGraph);

    String getName();
}