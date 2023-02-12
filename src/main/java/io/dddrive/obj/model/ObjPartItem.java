package io.dddrive.obj.model;

import io.dddrive.property.model.AggregatePartItem;

/**
 * This is a generic ObjPart, which contains a String value that can be used to
 * store a reference to another Aggregate, or an Enum, or anything else
 * stringified.
 */
public interface ObjPartItem extends ObjPart<Obj>, AggregatePartItem<Obj> {

}
