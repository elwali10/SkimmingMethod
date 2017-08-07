package com.ibm.rules.metering.methods;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class SkimmingMethod {
	private static final int LONG_SIZE = 8;
	private static final int INT_SIZE = 4;
	private static final int BYTE_SIZE = 1;
	private static final int BOOLEAN_SIZE = 1;
	private static final int CHAR_SIZE = 2;
	private static final int SHORT_SIZE = 2;
	private static final int FLOAT_SIZE = 4;
	private static final int DOUBLE_SIZE = 8;
	private static final int ALIGNMENT = 8;
	private static int headerSize = 16;
	private static int referenceSize = 8;

	public static long skimmingFields_Method(Object o)
			throws IllegalArgumentException, IllegalAccessException {

		if (o == null) {
			return 0;
		}
		
		
		Class<?> clazz = o.getClass();
		long countSize = headerSize;

		if (clazz.isArray()) {
			if (clazz == long[].class) {
				long[] objs = (long[]) o;
				countSize += objs.length * LONG_SIZE;
			} else if (clazz == int[].class) {
				int[] objs = (int[]) o;
				countSize += objs.length * INT_SIZE;
			} else if (clazz == boolean[].class) {
				boolean[] objs = (boolean[]) o;
				countSize += objs.length * BOOLEAN_SIZE;
			} else if (clazz == byte[].class) {
				byte[] objs = (byte[]) o;
				countSize += objs.length * BYTE_SIZE;
			} else if (clazz == char[].class) {
				char[] objs = (char[]) o;
				countSize += objs.length * CHAR_SIZE;
			} else if (clazz == short[].class) {
				short[] objs = (short[]) o;
				countSize += objs.length * SHORT_SIZE;
			} else if (clazz == float[].class) {
				float[] objs = (float[]) o;
				countSize += objs.length * FLOAT_SIZE;
			} else if (clazz == double[].class) {
				double[] objs = (double[]) o;
				countSize += objs.length * DOUBLE_SIZE;
			} else {
				Object[] objs = (Object[]) o;
				for (Object obj : objs) {
					countSize += skimmingFields_Method(obj) + referenceSize;
				}
			}

		} else {

			List<Field> fields = new ArrayList<Field>();
			do {
				Field[] classFields = clazz.getDeclaredFields();
				for (Field field : classFields) {
					if (!Modifier.isStatic(field.getModifiers())) {
						fields.add(field);
					}
				}
				clazz = clazz.getSuperclass();
			} while (clazz != null);
			for (Field field : fields) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				String fieldType = field.getGenericType().toString();
				if (fieldType.equals("long")) {
					countSize += LONG_SIZE;
				} else if (fieldType.equals("int")) {
					countSize += INT_SIZE;
				} else if (fieldType.equals("byte")) {
					countSize += BYTE_SIZE;
				} else if (fieldType.equals("boolean")) {
					countSize += BOOLEAN_SIZE;
				} else if (fieldType.equals("char")) {
					countSize += CHAR_SIZE;
				} else if (fieldType.equals("short")) {
					countSize += SHORT_SIZE;
				} else if (fieldType.equals("float")) {
					countSize += FLOAT_SIZE;
				} else if (fieldType.equals("double")) {
					countSize += DOUBLE_SIZE;
				} else {
					countSize += skimmingFields_Method(field.get(o))
							+ referenceSize;
				}
			}

		}
		// the size of object has to be multiplied by 8
		if ((countSize % ALIGNMENT != 0) && (clazz == null)) {
			countSize = ALIGNMENT * (countSize / ALIGNMENT + 1);
		}

		return countSize;
}
}
