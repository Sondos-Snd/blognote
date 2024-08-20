package com.opensource.blognote.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate-account.html");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
