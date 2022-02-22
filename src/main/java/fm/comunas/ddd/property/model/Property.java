package fm.comunas.ddd.property.model;

public interface Property<T> {

	String getName();

	boolean isWritable();

	void setWritable(boolean isWritable);

}
