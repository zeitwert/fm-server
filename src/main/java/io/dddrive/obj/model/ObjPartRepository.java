package io.dddrive.obj.model;

import io.dddrive.ddd.model.PartRepository;

public interface ObjPartRepository<O extends Obj, P extends ObjPart<O>> extends PartRepository<O, P> {

}
