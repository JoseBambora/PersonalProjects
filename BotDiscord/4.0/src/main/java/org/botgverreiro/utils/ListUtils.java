package org.botgverreiro.utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class ListUtils {
    public static <T> List<T> subList(List<T> list, int to) {
        System.out.println(list);
        return list.size() < to ? list : list.subList(0, to);
    }

    public static List<String> extractStrings(Elements elements, int removeFstChars) {
        return elements.stream().map(Element::wholeText).map(String::strip).map(s -> s.substring(removeFstChars)).toList();
    }
}
