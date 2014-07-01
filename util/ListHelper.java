package com.lm.util;

import java.util.List;

public class ListHelper {

    public static <T> T getLast(List<T> list) {
        if (list == null || list.size() <= 0) { return null; }
        return list.get(list.size() - 1);
    }

    public static <T> T getNext(List<T> list, T arg) {
        if (list == null || list.size() <= 0) { return null; }
        for (int i = 0; i < list.size() - 1; i++) {
            if (arg.equals(list.get(i))) { return list.get(i + 1); }
        }
        return null;
    }

    public static <T> T getPrev(List<T> list, T arg) {
        if (list == null || list.size() <= 0) { return null; }
        for (int i = 0; i < list.size(); i++) {
            if (arg.equals(list.get(i))) {
                if (i == 0) return null;
                else return list.get(i - 1);
            }
        }
        return null;
    }
}
