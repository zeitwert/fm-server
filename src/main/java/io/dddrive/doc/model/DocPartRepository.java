package io.dddrive.doc.model;

import io.dddrive.ddd.model.PartRepository;

public interface DocPartRepository<D extends Doc, P extends DocPart<D>> extends PartRepository<D, P> {

}
