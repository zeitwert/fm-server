package fm.comunas.ddd.doc.model;

import fm.comunas.ddd.part.model.PartRepository;

public interface DocPartRepository<D extends Doc, P extends DocPart<D>> extends PartRepository<D, P> {

}
