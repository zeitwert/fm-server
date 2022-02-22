package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.property.model.EntityPartItem;

/**
 * This is a generic ObjPart, which contains a String value that can be used to
 * store a reference to another Aggregate, or an Enum, or anything else
 * stringified.
 */
public interface ObjPartItem extends ObjPart<Obj>, EntityPartItem {

}
