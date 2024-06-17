// Copyright 2024 Oğuzhan Topaloğlu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.



package com.twistral.tffn;

import org.junit.jupiter.api.*;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;


public class TFFNTest {

    private static int num;

    @Test
    @DisplayName("validTests")
    void validTests() {
        TFFNParser parser;

        // Static
        parser = new TFFNParser();
        parser.defineStaticAction("hello", "hello world")
                .defineStaticAction("author", "oziris78");
        assertEquals(parser.parse("[hello] from [author]"), "hello world from oziris78");

        // Static & Dynamic
        parser = new TFFNParser();
        parser.defineStaticAction("static", "Static Part")
                .defineDynamicAction("dynamic", () -> "Dynamic Part")
                .defineDynamicAction("greet", () -> "Hello, Dynamic World!");
        assertEquals("Static Part Dynamic Part", parser.parse("[static] [dynamic]"));
        assertEquals("Hello, Dynamic World!", parser.parse("[greet]"));

        // Complex Dynamic
        parser = new TFFNParser();
        num = 1;
        parser.defineDynamicAction("num", () -> {
            String str = String.format("Check out my counter: %d", num);
            num *= 2;
            return str;
        });
        assertEquals("Hey! Check out my counter: 1", parser.parse("Hey!! [num]"));
        assertEquals("Hey! Check out my counter: 2", parser.parse("Hey!! [num]"));
        assertEquals("Hey! Check out my counter: 4", parser.parse("Hey!! [num]"));
        assertEquals("Hey! Check out my counter: 8", parser.parse("Hey!! [num]"));
        assertEquals("Hey! Check out my counter: 16", parser.parse("Hey!! [num]"));
        assertEquals("Hey! Check out my counter: 32", parser.parse("Hey!! [num]"));

        // Escaping
        parser = new TFFNParser();
        parser.defineStaticAction("test", "in brackets!");
        parser.defineDynamicAction("this", () -> "this will be");
        assertEquals("this will be [in brackets!]", parser.parse("[this] ![[test]!]"));
        assertEquals("wow!!!", parser.parse("wow!!!!!!"));
        assertEquals("!!!!", parser.parse("!!!!!!!!"));

        // Multiple Dynamic in one statement
        parser = new TFFNParser();
        num = 1;
        parser.defineDynamicAction("inc", () -> {
            String s = String.valueOf(num);
            num++;
            return s;
        });
        assertEquals("1 2 3", parser.parse("[inc] [inc] [inc]"));

        // Parsing an empty format should return an empty string
        parser = new TFFNParser();
        assertEquals("", parser.parse(""));
    }


    @Test
    @DisplayName("invalidTests")
    void invalidTests() {
        final TFFNParser parser = new TFFNParser();
        parser.defineStaticAction("nested", "test345");
        parser.defineStaticAction("brackets", "testing123");

        // DANGLING_CLOSE_BRACKET
        assertThrows(TFFNException.class, () -> parser.parse("]"));
        assertThrows(TFFNException.class, () -> parser.parse("abc]"));
        assertThrows(TFFNException.class, () -> parser.parse("[]]"));
        assertThrows(TFFNException.class, () -> parser.parse("!!]"));
        assertThrows(TFFNException.class, () -> parser.parse("]!!"));
        assertThrows(TFFNException.class, () -> parser.parse("x]"));
        assertThrows(TFFNException.class, () -> parser.parse("]x"));

        // NESTING_BRACKETS
        assertThrows(TFFNException.class, () -> parser.parse("[nested[brackets]]"));
        assertThrows(TFFNException.class, () -> parser.parse("[nes[brackets]ted]"));
        assertThrows(TFFNException.class, () -> parser.parse("[[brackets]]"));

        // DANGLING_IGNORE_TOKEN
        assertThrows(TFFNException.class, () -> parser.parse("Hello World!"));
        assertThrows(TFFNException.class, () -> parser.parse("Hello!! World!"));

        // UNCLOSED_BRACKET
        assertThrows(TFFNException.class, () -> parser.parse("[unclosed"));
        assertThrows(TFFNException.class, () -> parser.parse("[nested][unclosed"));

        // IGNORE_TOKEN_INSIDE_BRACKET
        assertThrows(TFFNException.class, () -> parser.parse("[ignore!token]"));
        assertThrows(TFFNException.class, () -> parser.parse("[ignore!!token]"));
        assertThrows(TFFNException.class, () -> parser.parse("[!!token]"));
        assertThrows(TFFNException.class, () -> parser.parse("[token!!]"));
        assertThrows(TFFNException.class, () -> parser.parse("[!!]"));
        assertThrows(TFFNException.class, () -> parser.parse("[!]"));

        // UNDEFINED_ACTION
        assertThrows(TFFNException.class, () -> parser.parse("[]"));
        assertThrows(TFFNException.class, () -> parser.parse("[nester]")); // typo
        assertThrows(TFFNException.class, () -> parser.parse("[undefined]"));

        // ACTION_TEXT_ALREADY_EXISTS
        assertThrows(TFFNException.class, () -> parser.defineStaticAction("nested", "Static duplicate"));
        assertThrows(TFFNException.class, () -> parser.defineDynamicAction("nested", () -> "Dynamic duplicate"));
    }


    @Test
    @DisplayName("edgeCaseTests")
    void edgeCaseTests() {
        TFFNParser parser;

        // EXTREMELY LONG ACTION NAME
        parser = new TFFNParser();
        StringBuilder longActionNameBuilder = new StringBuilder(100000);
        for (int i = 0; i < 100000; i++) {
            longActionNameBuilder.append('a');
        }
        String longActionName = longActionNameBuilder.toString();

        parser.defineStaticAction(longActionName, "Long Action Name");
        assertEquals("Long Action Name", parser.parse("[" + longActionName + "]"));

        // EXTREMELY LONG ACTION CONTENT
        parser = new TFFNParser();

        longActionNameBuilder = new StringBuilder(100000);
        for (int i = 0; i < 100000; i++) {
            longActionNameBuilder.append('b');
        }
        String longActionContent = longActionNameBuilder.toString();
        parser.defineStaticAction("longContent", longActionContent);
        assertEquals(longActionContent, parser.parse("[longContent]"));

        // MULTIPLE CONSECUTIVE BRACKETS
        parser = new TFFNParser();
        parser.defineStaticAction("action1", "First");
        parser.defineStaticAction("action2", "Second");
        assertEquals("FirstSecond", parser.parse("[action1][action2]"), "Consecutive brackets should be parsed correctly.");

        // DEFINE AN ACTION WITH A VERY LARGE NUMBER OF DYNAMIC PARTS
        parser = new TFFNParser();
        final int ITER_COUNT = 1_000_000;

        for (int i = 0; i < ITER_COUNT; i++) { // dynamic0 => 0, dynamic1 => 1, ...
            int finalI = i;
            parser.defineDynamicAction("dynamic" + i, () -> String.valueOf(finalI));
        }

        StringBuilder formatBuilder = new StringBuilder(ITER_COUNT*6);
        for (int i = 0; i < ITER_COUNT; i++) {
            formatBuilder.append("[").append("dynamic").append(i).append("]");
        }
        String result = parser.parse(formatBuilder.toString());

        StringBuilder expectedBuilder = new StringBuilder(ITER_COUNT*6);
        IntStream.range(0, ITER_COUNT).forEach(i -> expectedBuilder.append(String.valueOf(i)));
        assertEquals(expectedBuilder.toString(), result);
    }




}
