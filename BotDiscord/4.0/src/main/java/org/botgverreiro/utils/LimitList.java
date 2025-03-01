package org.botgverreiro.utils;

import java.util.List;

public class LimitList {
    public static <T> List<T> subList(List<T> list, int to) {
        System.out.println(list);
        return list.size() < to ? list : list.subList(0, to);
    }
}
