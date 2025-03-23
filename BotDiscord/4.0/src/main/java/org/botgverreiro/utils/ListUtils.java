package org.botgverreiro.utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class ListUtils {
    public static <T> List<T> subList(List<T> list, int to) {
        return subList(list,0,to);
    }

    public static <T> List<T> subList(List<T> list, int from, int to) {
        return list.size() < to ? list : list.subList(from, to);
    }

    public static List<String> extractStrings(Elements elements, int removeFstChars) {
        return elements.stream().map(Element::wholeText).map(String::strip).map(s -> s.substring(removeFstChars)).toList();
    }
}
