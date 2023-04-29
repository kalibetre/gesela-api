package com.geselaapi.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Converter {
    public static <T, U> List<U> convertList(List<T> originalList, Function<T, U> converter) {
        List<U> newList = new ArrayList<>();
        for (T element : originalList) {
            newList.add(converter.apply(element));
        }
        return newList;
    }
}
