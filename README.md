


# TFFN Java Parser

<b>A Java library for parsing format strings written in TFFN syntax.</b>

<br>


# TFFN Syntax

TFFN is an extremely simple and flexible formatting language/syntax that only has three special characters:
1. Action Begin Token - `[`
2. Action End Token - `]`
3. Ignore Token - `!`

In a TFFN format string, previously defined bracket texts are "actions". Each action starts 
with `[` and ends with `]`.

There are two types of actions:
1. Static Actions: Always replaced with the same string.
2. Dynamic Actions: Can be replaced with different strings each time or literally do any manipulation to the formatting process.

You can use the ignore token `!` to escape `[` or `]`. This token can also escape itself. :)


For example, if "hello" is a static action that expands to "Hello World!" and if "rand" is a dynamic action
that adds a random digit to the string:
- format = `"[hello] Mr. [rand][rand][rand]"` can expand to `"Hello World! Mr. 216"`, `"Hello World! Mr. 937"`, ...
- format = `"![[rand][rand]]!"` can expand to `"[12]"`, `"[36]"`, `"[99]"`, `"[27]"`, ...
- format = `"[rand] Hello!!"` can expand to `"5 Hello!"`, `"6 Hello!"`, `"1 Hello!"`, `"0 Hello!"`, ...
- format = `"[hello]!!"` will always expand to `"Hello World!!"` because it has no dynamic actions

<br>


# Code Examples
```java
public class Demo {
    public static void main(String[] args) {
        TFFNParser parser = new TFFNParser();
        Random random = new Random();

        // Define static action (same string each time)
        parser.defineStaticAction("h", "Hello");

        // Define dynamic actions
        //    different string each time, notice that its just a Supplier<String>
        //    meaning you can do literally anything in that function as long as
        //    you return a string. Very flexible isnt it?
        parser.defineDynamicAction("hex", sb -> {
            final String alphabet = "0123456789ABCDEF";
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        });
        parser.defineDynamicAction("bin", sb -> {
            final String alphabet = "01";
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        });
        parser.defineDynamicAction("letter", sb -> {
            final String alphabet = "BCDFGHJKLMNPQRSTVWXYZ";
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        });

        for (int i = 0; i < 10; i++) {
            String message = parser.parse("[h], [bin][bin]-[hex][hex][hex]-[letter][letter]!!");
            // Prints "Hello, 01-2F0-BL!", "Hello, 10-D80-BM!", "Hello, 01-84A-XZ!", ... 
            System.out.println(message);
        }
    }
}
```

For more examples that a look at <a href="https://github.com/oziris78/tffn-java-parser/blob/main/src/test/java/com/twistral/tffn/TFFNTest.java">here</a>.

<br>


# Downloading

Add the following to your build.gradle file:

```gradle
allprojects {
    // ...
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.oziris78:tffn-java-parser:v1.0.0'
}
```

<br>

# TFFN Lore

TFFN stands for "Tephrium's FrommatFWG Format Notation", a name that might not be immediately clear.

<a href="https://github.com/oziris78/tephrium">Tephrium</a> is a general-purpose Java library 
I've been developing for years. FrommatFWG is a type of Fake Word Generator (FWG) that I came 
up with, that uses its own formatting language, now called TFFN, to generate all possible 
combinations of a given format.

So basically, TFFN is an improved version of this old formatting language, offering way more 
flexibility and compatibility with any programming language or framework.

<br>

# Licensing

<a href="https://github.com/oziris78/tffn-java-parser">This library</a> is licensed under the terms of the Apache-2.0 license.

<br>


# Other TFFN Libraries

| Programming Language |                                Library Name                                 |        Author         |
|:--------------------:|:---------------------------------------------------------------------------:|:---------------------:|
|         Java         | <a href="https://github.com/oziris78/tffn-java-parser">tffn-java-parser</a> |  Oğuzhan Topaloğlu   |
|          C           |    <a href="https://github.com/oziris78/tffn-c-parser">tffn-c-parser</a>    |  Oğuzhan Topaloğlu   |

