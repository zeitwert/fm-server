package fm.comunas.ddd.obj.model;

import fm.comunas.ddd.part.model.PartRepository;

public interface ObjPartRepository<O extends Obj, P extends ObjPart<O>> extends PartRepository<O, P> {

}
