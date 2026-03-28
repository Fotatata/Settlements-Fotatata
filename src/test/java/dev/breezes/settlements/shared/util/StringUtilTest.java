package dev.breezes.settlements.shared.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

    @Test
    void titleCase_singleWord() {
        assertEquals("Hello", StringUtil.titleCase("hello"));
    }

    @Test
    void titleCase_multipleWords() {
        assertEquals("Hello World", StringUtil.titleCase("hello world"));
    }

    @Test
    void titleCase_alreadyTitleCase() {
        assertEquals("Hello World", StringUtil.titleCase("Hello World"));
    }

    @Test
    void titleCase_allUpperCase() {
        assertEquals("Hello World", StringUtil.titleCase("HELLO WORLD"));
    }

    @Test
    void titleCase_emptyString() {
        assertEquals("", StringUtil.titleCase(""));
    }

    @Test
    void titleCase_singleCharacter() {
        assertEquals("A", StringUtil.titleCase("a"));
    }

    @Test
    void titleCase_multipleSpaces() {
        assertEquals("Hello  World", StringUtil.titleCase("hello  world"));
    }

    @Test
    void titleCase_underscoresNotTreatedAsSeparators() {
        assertEquals("Hello_world", StringUtil.titleCase("hello_world"));
    }

}
