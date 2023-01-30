package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.property.model.AggregatePartItem;

/**
 * This is a generic DocPart, which contains a String value that can be used to
 * store a reference to another Aggregate, or an Enum, or anything else
 * stringified.
 */
public interface DocPartItem extends DocPart<Doc>, AggregatePartItem<Doc> {

}
