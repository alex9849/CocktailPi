package net.alex9849.cocktailmaker.model.user;

public enum Language {
    en_US("English (US)", "English (US)"),
    de("German", "Deutsch");

    final String in_english;
    final String in_native;

    Language(String in_english, String in_native) {
        this.in_english = in_english;
        this.in_native = in_native;
    }
}
