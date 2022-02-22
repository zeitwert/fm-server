package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.part.model.PartRepository;

public interface ObjPartRepository<O extends Obj, P extends ObjPart<O>> extends PartRepository<O, P> {

}
