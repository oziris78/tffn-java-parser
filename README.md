


# TFFN Java Parser

<b>A Java library for parsing format strings written in TFFN syntax.</b>



# TFFN Syntax

TFFN is an extremely simple and flexible formatting language/syntax that only has three special characters:
1. Action Begin Token - `[`
2. Action End Token - `]`
3. Ignore Token - `!`

In a TFFN format string, parts that will get replaced by another string are called "actions". Each action 
starts with `[` and ends with `]`.

There are two types of actions:
1. Static Actions: Always replaced with the same string.
2. Dynamic Actions: Can be replaced with different strings each time.

You can use the ignore token `!` to escape `[` or `]`. This token can also escape itself. :)


For example, if "hello" is a static action that expands to "Hello World!" and if "rand" is a dynamic action
that expands to a random digit:
- format = `"[hello] Mr. [rand][rand][rand]"` can expand to `"Hello World! Mr. 216"`, `"Hello World! Mr. 937"`, ...
- format = `"![[rand][rand]]!"` can expand to `"[12]"`, `"[36]"`, `"[99]"`, `"[27]"`, ...
- format = `"[rand] Hello!!"` can expand to `"5 Hello!"`, `"6 Hello!"`, `"1 Hello!"`, `"0 Hello!"`, ...
- format = `"[hello]!!"` will always expand to `"Hello World!!"` because it has no dynamic actions



# Examples
```java
public class Main {
    public static void main(String[] args) {
        TFFNParser parser = new TFFNParser();
        Random random = new Random();
        
        // Define static action (same string each time)
        parser.defineStaticAction("h", "Hello");

        // Define dynamic actions
        //    different string each time, notice that its just a Supplier<String>
        //    meaning you can do literally anything in that function as long as
        //    you return a string. Very flexible isnt it?
        parser.defineDynamicAction("hex", () -> { 
            final int randIndex = random.nextInt(16);
            return String.valueOf("0123456789ABCDEF".charAt(randIndex));
        });
        parser.defineDynamicAction("bin", () -> String.valueOf("01".charAt(random.nextInt(2))));
        parser.defineDynamicAction("letter", () -> {
            final int randIndex = random.nextInt(21);
            return String.valueOf("BCDFGHJKLMNPQRSTVWXYZ".charAt(randIndex));
        });

        for (int i = 0; i < 10; i++) {
            String message = parser.parse("[h], [bin][bin]-[hex][hex][hex]-[letter][letter]!!");
            System.out.println(message); // Hello, 01-270-CH!, Hello, 10-A5E-NF!, ...
        }
    }
}
```



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


# Licensing

This library is licensed under the terms of the Apache-2.0 license.


# TFFN Lore

TFFN stands for "Tephrium's FrommatFWG Format Notation", a name that might not be immediately clear.

<a href="https://github.com/oziris78/tephrium">Tephrium</a> is a general-purpose Java library 
I've been developing for years. FrommatFWG is a type of Fake Word Generator (FWG) that I came 
up with, that uses its own formatting language, now called TFFN, to generate all possible 
combinations of a given format.

So basically, TFFN is an improved version of this old formatting language, offering way more 
flexibility and compatibility with any programming language or framework.


# Other TFFN Libraries

Here's a list of TFFN libraries that I or other people created:

| Programming Language |                                Library Name                                 |        Author         |
|:--------------------:|:---------------------------------------------------------------------------:|:---------------------:|
|         Java         | <a href="https://github.com/oziris78/tffn-java-parser">tffn-java-parser</a> |  Oğuzhan Topaloğlu   |

