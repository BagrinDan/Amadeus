package org.example.amadeus.enums;

public enum Emotions {
    ANGRY,
    CLOSED_EYES,
    DEFAULT,
    EAGER_TALK,
    EMBARRASSED,
    GROOMY,
    HAPPY,
    INTERESTED,
    LOOKING_AWAY,
    SMIRK,
    UNHAPPY,
    UPSET,
    VERY_ANGRY;

    public static Emotions fromString(String name) {
        try {
            return Emotions.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return DEFAULT;
        }
    }
}
