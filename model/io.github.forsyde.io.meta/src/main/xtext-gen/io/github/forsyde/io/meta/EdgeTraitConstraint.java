/**
 * generated by Xtext 2.25.0
 */
package io.github.forsyde.io.meta;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * &lt;!-- begin-user-doc --&gt;
 * A representation of the model object '<em><b>Edge Trait Constraint</b>&lt;/em&gt;'.
 * &lt;!-- end-user-doc --&gt;
 *
 * &lt; &gt;
 * The following features are supported:
 * &lt;/p&gt;
 * &lt; &gt;
 *   <li>{@link io.github.forsyde.io.meta.EdgeTraitConstraint#isTarget <em>Target</em>}&lt;/li&gt;
 *   <li>{@link io.github.forsyde.io.meta.EdgeTraitConstraint#isSource <em>Source</em>}&lt;/li&gt;
 *   <li>{@link io.github.forsyde.io.meta.EdgeTraitConstraint#getFilter <em>Filter</em>}&lt;/li&gt;
 * &lt;/ul&gt;
 *
 * @see io.github.forsyde.io.meta.MetaPackage#getEdgeTraitConstraint()
 * @model
 * @generated
 */
public interface EdgeTraitConstraint extends EObject
{
  /**
   * Returns the value of the '<em><b>Target</b>&lt;/em&gt;' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @return the value of the '<em>Target&lt;/em&gt;' attribute.
   * @see #setTarget(boolean)
   * @see io.github.forsyde.io.meta.MetaPackage#getEdgeTraitConstraint_Target()
   * @model
   * @generated
   */
  boolean isTarget();

  /**
   * Sets the value of the '{@link io.github.forsyde.io.meta.EdgeTraitConstraint#isTarget <em>Target&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @param value the new value of the '<em>Target&lt;/em&gt;' attribute.
   * @see #isTarget()
   * @generated
   */
  void setTarget(boolean value);

  /**
   * Returns the value of the '<em><b>Source</b>&lt;/em&gt;' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @return the value of the '<em>Source&lt;/em&gt;' attribute.
   * @see #setSource(boolean)
   * @see io.github.forsyde.io.meta.MetaPackage#getEdgeTraitConstraint_Source()
   * @model
   * @generated
   */
  boolean isSource();

  /**
   * Sets the value of the '{@link io.github.forsyde.io.meta.EdgeTraitConstraint#isSource <em>Source&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @param value the new value of the '<em>Source&lt;/em&gt;' attribute.
   * @see #isSource()
   * @generated
   */
  void setSource(boolean value);

  /**
   * Returns the value of the '<em><b>Filter</b>&lt;/em&gt;' reference list.
   * The list contents are of type {@link io.github.forsyde.io.meta.VertexTrait}.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @return the value of the '<em>Filter&lt;/em&gt;' reference list.
   * @see io.github.forsyde.io.meta.MetaPackage#getEdgeTraitConstraint_Filter()
   * @model
   * @generated
   */
  EList<VertexTrait> getFilter();

} // EdgeTraitConstraint
