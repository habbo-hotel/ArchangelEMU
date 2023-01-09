package com.eu.habbo.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CryptoConfig {

    private final boolean enabled;
    private final String exponent;
    private final String modulus;
    private final String privateExponent;

}
