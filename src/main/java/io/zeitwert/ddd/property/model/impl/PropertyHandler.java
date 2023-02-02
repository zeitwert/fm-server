package io.zeitwert.ddd.property.model.impl;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.property.model.EntityWithProperties;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyHandler implements MethodHandler {

	public static final PropertyHandler INSTANCE = new PropertyHandler();

	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
		String methodName = m.getName();
		try {
			if (this.isCollectionApi(methodName, args)) {
				String fieldName = this.getCollectionFieldName(methodName, args);
				Property<?> property = this.getProperty(self, fieldName);
				if (property instanceof PartListProperty) {
					if (args.length == 0) {
						if (methodName.startsWith("get") && methodName.endsWith("Count")) {
							return ((PartListProperty<?>) property).getPartCount();
						} else if (methodName.startsWith("get") && methodName.endsWith("List")) {
							return ((PartListProperty<?>) property).getParts();
						} else if (methodName.startsWith("clear") && methodName.endsWith("List")) {
							((PartListProperty<?>) property).clearParts();
							return null;
						} else if (methodName.startsWith("add")) {
							return ((PartListProperty<?>) property).addPart();
						}
					} else if (args.length == 1) {
						if (methodName.startsWith("get") && methodName.endsWith("ById")) {
							return ((PartListProperty<?>) property).getPartById((Integer) args[0]);
						} else if (methodName.startsWith("get")) {
							return ((PartListProperty<?>) property).getPart((Integer) args[0]);
						} else if (methodName.startsWith("remove")) {
							((PartListProperty<?>) property).removePart((Integer) args[0]);
							return null;
						}
					}
				} else if (property instanceof EnumSetProperty) {
					if (args.length == 0) {
						if (methodName.startsWith("get") && methodName.endsWith("Count")) {
							return ((EnumSetProperty<?>) property).getItems().size();
						} else if (methodName.startsWith("get") && methodName.endsWith("Set")) {
							return ((EnumSetProperty<?>) property).getItems();
						} else if (methodName.startsWith("clear") && methodName.endsWith("Set")) {
							((EnumSetProperty<?>) property).clearItems();
							return null;
						}
					} else if (args.length == 1) {
						if (methodName.startsWith("has")) {
							return ((EnumSetProperty<Enumerated>) property).hasItem((Enumerated) args[0]);
						} else if (methodName.startsWith("add")) {
							((EnumSetProperty<Enumerated>) property).addItem((Enumerated) args[0]);
							return null;
						} else if (methodName.startsWith("remove")) {
							((EnumSetProperty<Enumerated>) property).removeItem((Enumerated) args[0]);
							return null;
						}
					}
				} else if (property instanceof ReferenceSetProperty) {
					if (args.length == 0) {
						if (methodName.startsWith("get") && methodName.endsWith("Count")) {
							return ((ReferenceSetProperty<?>) property).getItems().size();
						} else if (methodName.startsWith("get") && methodName.endsWith("Set")) {
							return ((ReferenceSetProperty<?>) property).getItems();
						} else if (methodName.startsWith("clear") && methodName.endsWith("Set")) {
							((ReferenceSetProperty<?>) property).clearItems();
							return null;
						}
					} else if (args.length == 1) {
						if (methodName.startsWith("has")) {
							return ((ReferenceSetProperty<?>) property).hasItem((Integer) args[0]);
						} else if (methodName.startsWith("add")) {
							((ReferenceSetProperty<?>) property).addItem((Integer) args[0]);
							return null;
						} else if (methodName.startsWith("remove")) {
							((ReferenceSetProperty<?>) property).removeItem((Integer) args[0]);
							return null;
						}
					}
				}
			}
			Property<?> property = this.getProperty(self, this.getFieldName(methodName));
			if (this.isGetter(methodName, args)) {
				if (property instanceof EnumProperty) {
					return ((EnumProperty<Enumerated>) property).getValue();
				} else if (property instanceof ReferenceProperty) {
					if (m.getName().endsWith("Id")) {
						return ((ReferenceProperty<Aggregate>) property).getId();
					} else {
						return ((ReferenceProperty<Aggregate>) property).getValue();
					}
				} else if (property instanceof SimpleProperty) { // must be last
					return ((SimpleProperty<?>) property).getValue();
				}
			} else if (this.isSetter(m.getName(), args)) {
				if (property instanceof EnumProperty) {
					((EnumProperty<Enumerated>) property).setValue((Enumerated) args[0]);
				} else if (property instanceof ReferenceProperty) {
					if (m.getName().endsWith("Id")) {
						((ReferenceProperty<Aggregate>) property).setId((Integer) args[0]);
					} else {
						((ReferenceProperty<Aggregate>) property).setValue((Aggregate) args[0]);
					}
				} else if (property instanceof SimpleProperty) { // must be last
					((SimpleProperty<Object>) property).setValue(args[0]);
				}
			}
		} catch (NoSuchFieldException x) {
			throw new NoSuchMethodException(self.getClass().getSimpleName() + "." + methodName);
		}
		return null;
	}

	private Property<?> getProperty(Object obj, String fieldName)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		EntityWithProperties entity = (EntityWithProperties) obj;
		Property<?> property = entity.getProperty(fieldName);
		if (property != null) {
			return property;
		}
		for (Class<?> c = obj.getClass().getSuperclass(); c != null; c = c.getSuperclass()) {
			try {
				Field field = c.getDeclaredField(fieldName);
				field.setAccessible(true);
				return (Property<?>) field.get(obj);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}
		if (fieldName.endsWith("Id")) {
			// try enumeration / object via field
			return this.getProperty(obj, fieldName.substring(0, fieldName.length() - 2));
		} else if (fieldName.endsWith("List")) {
			// try Set instead of List
			return this.getProperty(obj, fieldName.replace("List", "Set"));
		} else if (!fieldName.endsWith("_")) {
			// try with _ suffix
			return this.getProperty(obj, fieldName + "_");
		}
		throw new NoSuchFieldException(fieldName);
	}

	private String getFieldName(String methodName) throws NoSuchFieldException {
		if (methodName.startsWith("get")) {
			return this.getName(methodName.substring(3));
		} else if (methodName.startsWith("set")) {
			return this.getName(methodName.substring(3));
		}
		throw new NoSuchFieldException(methodName);
	}

	private boolean isCollectionApi(String methodName, Object[] args) {
		if (args.length == 0) {
			if (methodName.startsWith("get") && methodName.endsWith("Count")) {
				return true;
			} else if (methodName.startsWith("get") && (methodName.endsWith("List") || methodName.endsWith("Set"))) {
				return true;
			} else if (methodName.startsWith("clear") && (methodName.endsWith("List") || methodName.endsWith("Set"))) {
				return true;
			} else if (methodName.startsWith("add")) {
				return true;
			}
		} else if (args.length == 1) {
			if (methodName.startsWith("get") && methodName.endsWith("ById")) {
				return true;
			} else if (methodName.startsWith("has")) {
				return true;
			} else if (methodName.startsWith("get")) {
				return true;
			} else if (methodName.startsWith("add")) {
				return true;
			} else if (methodName.startsWith("remove")) {
				return true;
			}
		}
		return false;
	}

	private String getCollectionFieldName(String methodName, Object[] args) throws NoSuchFieldException {
		if (args.length == 0) {
			if (methodName.startsWith("get") && methodName.endsWith("Count")) {
				return this.getName(methodName.substring(3, methodName.length() - 5)) + "List";
			} else if (methodName.startsWith("get") && methodName.endsWith("List")) {
				return this.getName(methodName.substring(3, methodName.length() - 4)) + "List";
			} else if (methodName.startsWith("get") && methodName.endsWith("Set")) {
				return this.getName(methodName.substring(3, methodName.length() - 3)) + "Set";
			} else if (methodName.startsWith("clear") && methodName.endsWith("List")) {
				return this.getName(methodName.substring(5, methodName.length() - 4)) + "List";
			} else if (methodName.startsWith("clear") && methodName.endsWith("Set")) {
				return this.getName(methodName.substring(5, methodName.length() - 3)) + "Set";
			} else if (methodName.startsWith("add")) {
				return this.getName(methodName.substring(3)) + "List";
			}
		} else if (args.length == 1) {
			if (methodName.startsWith("get") && methodName.endsWith("ById")) {
				return this.getName(methodName.substring(3, methodName.length() - 4)) + "List";
			} else if (methodName.startsWith("has")) {
				return this.getName(methodName.substring(3)) + "Set";
			} else if (methodName.startsWith("get")) {
				return this.getName(methodName.substring(3)) + "List";
			} else if (methodName.startsWith("add")) {
				return this.getName(methodName.substring(3)) + "Set";
			} else if (methodName.startsWith("remove")) {
				return this.getName(methodName.substring(6)) + "List";
			}
		}
		throw new NoSuchFieldException(methodName);
	}

	private String getName(String methodName) throws NoSuchFieldException {
		return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
	}

	private boolean isGetter(String methodName, Object[] args) {
		return methodName.startsWith("get") && args.length == 0;
	}

	private boolean isSetter(String methodName, Object[] args) {
		return methodName.startsWith("set") && args.length == 1;
	}

}
