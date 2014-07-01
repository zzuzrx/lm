package com.lm.util;

import java.lang.reflect.Array;

public class Utils {
	public static Boolean isDebug = false; 

	public static void echo (String str) { 
		if (isDebug){ 
			System.out.println(str); 
		} 
	} 

	/** 字符串转换成数组 */
	public static int[] stringToArray(String str) {
		int length = str.length();
		int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = Integer.parseInt(str.substring(i, i + 1));
		}
		return arr;
	}

	/** 打印多个横线 */
	public static void printDashLine(int num) {
		for (int i = 0; i < num; i++) {
			System.out.print("-");
		}
		System.out.println();
	}
	
	public static <T> T[] initializedArray(int numElements,
			Class<T> componentType) {
		try {
			@SuppressWarnings("unchecked")
			T[] res = (T[]) Array.newInstance(componentType, numElements);
			for (int i = 0; i < numElements; i++) {
				res[i] = componentType.newInstance();
			}
			return res;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
