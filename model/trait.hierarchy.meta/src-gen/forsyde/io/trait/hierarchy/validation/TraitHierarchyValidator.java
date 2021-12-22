/**
 *
 * $Id$
 */
package forsyde.io.trait.hierarchy.validation;

import forsyde.io.trait.hierarchy.EdgeTraitSpec;
import forsyde.io.trait.hierarchy.TraitHierarchy;
import forsyde.io.trait.hierarchy.VertexTraitSpec;
import org.eclipse.emf.common.util.EList;


/**
 * A sample validator interface for {@link forsyde.io.trait.hierarchy.TraitHierarchy}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface TraitHierarchyValidator {
	boolean validate();

	boolean validateNamespace(String value);

	boolean validateScopedHierarchy(EList<TraitHierarchy> value);

	boolean validateVertexTraits(EList<VertexTraitSpec> value);

	boolean validateEdgeTraits(EList<EdgeTraitSpec> value);

}
