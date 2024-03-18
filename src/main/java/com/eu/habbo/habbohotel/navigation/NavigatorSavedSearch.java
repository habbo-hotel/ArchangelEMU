package com.eu.habbo.habbohotel.navigation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class NavigatorSavedSearch {
    private final String searchCode;
    private final String filter;
    @Setter
    private int id;

    public NavigatorSavedSearch(String searchCode, String filter) {
        this.searchCode = searchCode;
        this.filter = filter;
    }
}
