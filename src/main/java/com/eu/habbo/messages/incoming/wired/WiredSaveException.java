package com.eu.habbo.messages.incoming.wired;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WiredSaveException extends Exception {
    private final String message;

    @Override
    public String getMessage() {
        return this.message;
    }
}
