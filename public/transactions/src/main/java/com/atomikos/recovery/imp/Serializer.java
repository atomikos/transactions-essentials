package com.atomikos.recovery.imp;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.ParticipantLogEntry;

public class Serializer {

	private static final String QUOTE = "\"";
	private static final String END_ARRAY = "]";
	private static final String START_ARRAY = "[";
	private static final String START_OBJECT = "{";
	private static final String END_OBJECT = "}";

	public String toJSON(CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalAccessException {
		StringBuilder buffer = new StringBuilder(300);
		toJSON(buffer,coordinatorLogEntry);
		String str = buffer.toString();
		return str;
	}
	
	private void toJSON(StringBuilder strBuilder, CoordinatorLogEntry coordinatorLogEntry){
		strBuilder.append(START_OBJECT);
		strBuilder.append(QUOTE).append("coordinatorId").append(QUOTE).append(":").append(QUOTE).append(coordinatorLogEntry.coordinatorId).append(QUOTE);
		strBuilder.append(',');
		strBuilder.append(QUOTE).append("wasCommitted").append(QUOTE).append(":").append(coordinatorLogEntry.wasCommitted);
		strBuilder.append(',');
		String prefix = "";
		if(coordinatorLogEntry.participantDetails.length>0){
			strBuilder.append(QUOTE).append("participantDetails").append(QUOTE);
			strBuilder.append(":");
			strBuilder.append(START_ARRAY);
			for (ParticipantLogEntry participantLogEntry : coordinatorLogEntry.participantDetails) {
				strBuilder.append(prefix);
				prefix = ",";
				strBuilder.append(START_OBJECT);
				strBuilder.append(QUOTE).append("participantUri").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.participantUri).append(QUOTE);
				strBuilder.append(',');
				strBuilder.append(QUOTE).append("state").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.state).append(QUOTE);
				strBuilder.append(',');
				strBuilder.append(QUOTE).append("expires").append(QUOTE).append(":").append(participantLogEntry.expires);
				strBuilder.append(',');
				strBuilder.append(QUOTE).append("description").append(QUOTE).append(":").append(QUOTE).append(participantLogEntry.description).append(QUOTE);
				strBuilder.append(END_OBJECT);
			}
			strBuilder.append(END_ARRAY);
		}
		strBuilder.append(END_OBJECT);
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
