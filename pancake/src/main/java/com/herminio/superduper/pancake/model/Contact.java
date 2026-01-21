package com.herminio.superduper.pancake.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class Contact {

    @Getter @Setter
    private String name;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String message;

}
