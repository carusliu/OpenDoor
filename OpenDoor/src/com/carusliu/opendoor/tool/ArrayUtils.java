package com.carusliu.opendoor.tool;

public class ArrayUtils {

	public static <V> boolean isEmpty(V[] sourceArray) {
		return (sourceArray == null || sourceArray.length == 0);
	}

	public static <V> V getLast(V[] sourceArray, V value, V defaultValue,
			boolean isCircle) {
		if (isEmpty(sourceArray)) {
			return defaultValue;
		}

		int currentPosition = -1;
		for (int i = 0; i < sourceArray.length; i++) {
			if (mEquals(value, sourceArray[i])) {
				currentPosition = i;
				break;
			}
		}
		if (currentPosition == -1) {
			return defaultValue;
		}

		if (currentPosition == 0) {
			return isCircle ? sourceArray[sourceArray.length - 1]
					: defaultValue;
		}
		return sourceArray[currentPosition - 1];
	}

	public static <V> V getNext(V[] sourceArray, V value, V defaultValue,
			boolean isCircle) {
		if (isEmpty(sourceArray)) {
			return defaultValue;
		}

		int currentPosition = -1;
		for (int i = 0; i < sourceArray.length; i++) {
			if (mEquals(value, sourceArray[i])) {
				currentPosition = i;
				break;
			}
		}
		if (currentPosition == -1) {
			return defaultValue;
		}

		if (currentPosition == sourceArray.length - 1) {
			return isCircle ? sourceArray[0] : defaultValue;
		}
		return sourceArray[currentPosition + 1];
	}

	public static <V> V getLast(V[] sourceArray, V value, boolean isCircle) {
		return getLast(sourceArray, value, null, isCircle);
	}

	public static <V> V getNext(V[] sourceArray, V value, boolean isCircle) {
		return getNext(sourceArray, value, null, isCircle);
	}

	private static boolean mEquals(Object actual, Object expected) {
		return actual == expected
				|| (actual == null ? expected == null : actual.equals(expected));
	}
}
