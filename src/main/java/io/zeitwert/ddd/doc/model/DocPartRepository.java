package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.part.model.PartRepository;

public interface DocPartRepository<D extends Doc, P extends DocPart<D>> extends PartRepository<D, P> {

}
