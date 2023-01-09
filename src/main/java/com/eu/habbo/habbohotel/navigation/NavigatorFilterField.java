package com.eu.habbo.habbohotel.navigation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
public class NavigatorFilterField {
    private final String key;
    private final Method field;
    private  final String databaseQuery;
    private  final NavigatorFilterComparator comparator;
}