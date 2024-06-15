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


import java.util.HashMap;
import java.util.function.Supplier;

public class TFFNParser {

    // Both maps are in shape { actionText : action }
    private final HashMap<String, Supplier<String>> dynamicActions;
    private final HashMap<String, String> staticActions;

    private final HashMap<String, Steps> formatCache;


    public TFFNParser() {
        dynamicActions = new HashMap<>(64);
        staticActions = new HashMap<>(64);
        formatCache = new HashMap<>(512);
    }


    public boolean defineDynamicAction(String actionText, Supplier<String> dynamicAction) {
        if(staticActions.containsKey(actionText) || dynamicActions.containsKey(actionText)) {
            return false;
        }

        dynamicActions.put(actionText, dynamicAction);
        return true;
    }


    public boolean defineStaticAction(String actionText, String staticAction) {
        if(staticActions.containsKey(actionText) || dynamicActions.containsKey(actionText)) {
            return false;
        }

        staticActions.put(actionText, staticAction);
        return true;
    }



    public String parse(String format) {
        // Check the cache
        final Steps cachedSteps = this.formatCache.get(format);
        if(cachedSteps != null) return cachedSteps.process();

        // Doesnt exist in the cache, parse it
        Steps steps = new Steps();
        final int formatLen = format.length();
        StringBuilder brackText = new StringBuilder(formatLen);
        boolean inBrack = false;

        int i = 0;
        while(i < formatLen) {
            final char c = format.charAt(i);

            switch (c) {
                case '[': {
                    if(inBrack) {
                        throw new TFFNException("INVALID FORMAT: you forgot to close a bracket " +
                            "or tried to nest brackets. Nesting brackets is prohibited in TFFN.");
                    }

                    inBrack = true;
                    i++;
                } break;

                case ']': {
                    if(!inBrack) {
                        throw new TFFNException("INVALID FORMAT: you forgot to open a bracket");
                    }

                    inBrack = false;

                    final String brackContent = brackText.toString();
                    brackText.setLength(0);

                    if(staticActions.containsKey(brackContent)) {
                        final String step = staticActions.get(brackContent);
                        steps.addStaticStringStep(step);
                    }
                    else if(dynamicActions.containsKey(brackContent)) {
                        final Supplier<String> step = dynamicActions.get(brackContent);
                        steps.addDynamicStep(step);
                    }
                    else {
                        throw new TFFNException(
                            String.format("INVALID FORMAT: '%s' action was never defined to the parser", brackContent)
                        );
                    }

                    i++;
                } break;

                case '!': {
                    if(inBrack) {
                        throw new TFFNException("INVALID FORMAT: '!' token cant be used inside brackets");
                    }
                    if(i == formatLen - 1) {
                        throw new TFFNException("INVALID FORMAT: format string cant end with '!'");
                    }

                    final char nextChar = format.charAt(i + 1);
                    steps.addStaticCharStep(nextChar);
                    i += 2;
                } break;

                default: {
                    if(inBrack) brackText.append(c);
                    else steps.addStaticCharStep(c);

                    i++;
                } break;
            }
        }

        if(brackText.length() != 0) {
            throw new TFFNException("INVALID FORMAT: you forgot to close a bracket");
        }

        steps.flushBuffer();
        this.formatCache.put(format, steps);
        return steps.process();
    }

}
