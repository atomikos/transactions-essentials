package com.atomikos.recovery.imp;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.atomikos.recovery.CoordinatorLogEntry;

public class Serializer {

	private static final String QUOTE = "\"";
	private static final String END_ARRAY = "]";
	private static final String START_ARRAY = "[";
	private static final String START_OBJECT = "{";
	private static final String END_OBJECT = "}";

	public String toJSON(CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalAccessException {
		StringBuilder buffer = new StringBuilder(300);
		buffer.append(START_OBJECT);
		introspect(buffer, coordinatorLogEntry);
		buffer.append(END_OBJECT);
		buffer.append("\n");
		String str = buffer.toString();
		return str;
	}

	private void introspect(StringBuilder strBuilder, Object obj)
			throws IllegalAccessException {
		String prefix = "";
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (Modifier.isPublic(field.getModifiers())) {
				// Simple type ?
				if (field.getType().isPrimitive()) {
					strBuilder.append(prefix);
					prefix = ",";
					strBuilder
					 	.append(QUOTE).append(field.getName()).append(QUOTE)
					 	.append(":").append(field.get(obj));
				} else if (field.getType().isArray()) {
					Object[] array = (Object[]) field.get(obj);
					strBuilder.append(prefix);
					prefix = ",";
					strBuilder.append(QUOTE).append(field.getName()).append(QUOTE)
						.append(":");
					strBuilder.append(START_ARRAY);
					prefix = "";
					for (Object object : array) {
						strBuilder.append(prefix);
						prefix = ",";
						strBuilder.append(START_OBJECT);
							introspect(strBuilder, object);
						strBuilder.append(END_OBJECT);
					}
					strBuilder.append(END_ARRAY);
				} else if (field.getType().isEnum()) {
					strBuilder.append(prefix);
					prefix = ",";
					strBuilder
						.append(QUOTE).append(field.getName()).append(QUOTE)
							.append(":")
						.append(QUOTE).append(field.get(obj)).append(QUOTE);

				} else {
					strBuilder.append(prefix);
					prefix = ",";
					strBuilder
						.append(QUOTE).append(field.getName()).append(QUOTE)
							.append(":")
						.append(QUOTE).append(field.get(obj)).append(QUOTE);
				}

			}
		}
	}
}
