/**
 * generated by Xtext 2.25.0
 */
package io.github.forsyde.io.meta.impl;

import io.github.forsyde.io.meta.MetaPackage;
import io.github.forsyde.io.meta.VertexTrait;
import io.github.forsyde.io.meta.VertexTraitPort;
import io.github.forsyde.io.meta.VertexTraitPortDirection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * &lt;!-- begin-user-doc --&gt;
 * An implementation of the model object '<em><b>Vertex Trait Port</b>&lt;/em&gt;'.
 * &lt;!-- end-user-doc --&gt;
 * &lt; &gt;
 * The following features are implemented:
 * &lt;/p&gt;
 * &lt; &gt;
 *   <li>{@link io.github.forsyde.io.meta.impl.VertexTraitPortImpl#isMultiple <em>Multiple</em>}&lt;/li&gt;
 *   <li>{@link io.github.forsyde.io.meta.impl.VertexTraitPortImpl#isOrdered <em>Ordered</em>}&lt;/li&gt;
 *   <li>{@link io.github.forsyde.io.meta.impl.VertexTraitPortImpl#getDirection <em>Direction</em>}&lt;/li&gt;
 *   <li>{@link io.github.forsyde.io.meta.impl.VertexTraitPortImpl#getName <em>Name</em>}&lt;/li&gt;
 *   <li>{@link io.github.forsyde.io.meta.impl.VertexTraitPortImpl#getTrait <em>Trait</em>}&lt;/li&gt;
 * &lt;/ul&gt;
 *
 * @generated
 */
public class VertexTraitPortImpl extends MinimalEObjectImpl.Container implements VertexTraitPort
{
  /**
   * The default value of the '{@link #isMultiple() <em>Multiple&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #isMultiple()
   * @generated
   * @ordered
   */
  protected static final boolean MULTIPLE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isMultiple() <em>Multiple&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #isMultiple()
   * @generated
   * @ordered
   */
  protected boolean multiple = MULTIPLE_EDEFAULT;

  /**
   * The default value of the '{@link #isOrdered() <em>Ordered&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #isOrdered()
   * @generated
   * @ordered
   */
  protected static final boolean ORDERED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isOrdered() <em>Ordered&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #isOrdered()
   * @generated
   * @ordered
   */
  protected boolean ordered = ORDERED_EDEFAULT;

  /**
   * The default value of the '{@link #getDirection() <em>Direction&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #getDirection()
   * @generated
   * @ordered
   */
  protected static final VertexTraitPortDirection DIRECTION_EDEFAULT = VertexTraitPortDirection.BIDIRECTIONAL;

  /**
   * The cached value of the '{@link #getDirection() <em>Direction&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #getDirection()
   * @generated
   * @ordered
   */
  protected VertexTraitPortDirection direction = DIRECTION_EDEFAULT;

  /**
   * The default value of the '{@link #getName() <em>Name&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name&lt;/em&gt;}' attribute.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getTrait() <em>Trait&lt;/em&gt;}' reference.
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @see #getTrait()
   * @generated
   * @ordered
   */
  protected VertexTrait trait;

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  protected VertexTraitPortImpl()
  {
    super();
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return MetaPackage.Literals.VERTEX_TRAIT_PORT;
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public boolean isMultiple()
  {
    return multiple;
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public void setMultiple(boolean newMultiple)
  {
    boolean oldMultiple = multiple;
    multiple = newMultiple;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaPackage.VERTEX_TRAIT_PORT__MULTIPLE, oldMultiple, multiple));
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public boolean isOrdered()
  {
    return ordered;
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public void setOrdered(boolean newOrdered)
  {
    boolean oldOrdered = ordered;
    ordered = newOrdered;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaPackage.VERTEX_TRAIT_PORT__ORDERED, oldOrdered, ordered));
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public VertexTraitPortDirection getDirection()
  {
    return direction;
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public void setDirection(VertexTraitPortDirection newDirection)
  {
    VertexTraitPortDirection oldDirection = direction;
    direction = newDirection == null ? DIRECTION_EDEFAULT : newDirection;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaPackage.VERTEX_TRAIT_PORT__DIRECTION, oldDirection, direction));
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaPackage.VERTEX_TRAIT_PORT__NAME, oldName, name));
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public VertexTrait getTrait()
  {
    if (trait != null && trait.eIsProxy())
    {
      InternalEObject oldTrait = (InternalEObject)trait;
      trait = (VertexTrait)eResolveProxy(oldTrait);
      if (trait != oldTrait)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, MetaPackage.VERTEX_TRAIT_PORT__TRAIT, oldTrait, trait));
      }
    }
    return trait;
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  public VertexTrait basicGetTrait()
  {
    return trait;
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public void setTrait(VertexTrait newTrait)
  {
    VertexTrait oldTrait = trait;
    trait = newTrait;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, MetaPackage.VERTEX_TRAIT_PORT__TRAIT, oldTrait, trait));
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case MetaPackage.VERTEX_TRAIT_PORT__MULTIPLE:
        return isMultiple();
      case MetaPackage.VERTEX_TRAIT_PORT__ORDERED:
        return isOrdered();
      case MetaPackage.VERTEX_TRAIT_PORT__DIRECTION:
        return getDirection();
      case MetaPackage.VERTEX_TRAIT_PORT__NAME:
        return getName();
      case MetaPackage.VERTEX_TRAIT_PORT__TRAIT:
        if (resolve) return getTrait();
        return basicGetTrait();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case MetaPackage.VERTEX_TRAIT_PORT__MULTIPLE:
        setMultiple((Boolean)newValue);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__ORDERED:
        setOrdered((Boolean)newValue);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__DIRECTION:
        setDirection((VertexTraitPortDirection)newValue);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__NAME:
        setName((String)newValue);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__TRAIT:
        setTrait((VertexTrait)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case MetaPackage.VERTEX_TRAIT_PORT__MULTIPLE:
        setMultiple(MULTIPLE_EDEFAULT);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__ORDERED:
        setOrdered(ORDERED_EDEFAULT);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__DIRECTION:
        setDirection(DIRECTION_EDEFAULT);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case MetaPackage.VERTEX_TRAIT_PORT__TRAIT:
        setTrait((VertexTrait)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case MetaPackage.VERTEX_TRAIT_PORT__MULTIPLE:
        return multiple != MULTIPLE_EDEFAULT;
      case MetaPackage.VERTEX_TRAIT_PORT__ORDERED:
        return ordered != ORDERED_EDEFAULT;
      case MetaPackage.VERTEX_TRAIT_PORT__DIRECTION:
        return direction != DIRECTION_EDEFAULT;
      case MetaPackage.VERTEX_TRAIT_PORT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case MetaPackage.VERTEX_TRAIT_PORT__TRAIT:
        return trait != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * &lt;!-- begin-user-doc --&gt;
   * &lt;!-- end-user-doc --&gt;
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (multiple: ");
    result.append(multiple);
    result.append(", ordered: ");
    result.append(ordered);
    result.append(", direction: ");
    result.append(direction);
    result.append(", name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //VertexTraitPortImpl
