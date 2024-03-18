package com.eu.habbo.habbohotel.users.clothingvalidation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FiguredataSettypeSet {
    private int id;
    private String gender;
    private boolean club;
    private boolean colorable;
    private boolean selectable;
    private boolean preselectable;
    private boolean sellable;
}
