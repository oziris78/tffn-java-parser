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
import static com.twistral.tffn.TFFNException.*;


public class TFFNParser {

    private final HashMap<String, Supplier<String>> dynamicActions;    // actionText -> action
    private final HashMap<String, String> staticActions;               // actionText -> action
    private final HashMap<String, Steps> formatCache;                  // format -> steps


    public TFFNParser() {
        dynamicActions = new HashMap<>(64);
        staticActions = new HashMap<>(64);
        formatCache = new HashMap<>(512);
    }


    public void defineDynamicAction(String actionText, Supplier<String> dynamicAction) {
        if(staticActions.containsKey(actionText) || dynamicActions.containsKey(actionText)) {
            throw new TFFNActionTextAlreadyExistsException(actionText);
        }

        dynamicActions.put(actionText, dynamicAction);
    }


    public void defineStaticAction(String actionText, String staticAction) {
        if(staticActions.containsKey(actionText) || dynamicActions.containsKey(actionText)) {
            throw new TFFNActionTextAlreadyExistsException(actionText);
        }

        staticActions.put(actionText, staticAction);
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
                    if(inBrack) throw new TFFNNestingBracketsException();

                    inBrack = true;
                    i++;
                } break;

                case ']': {
                    if(!inBrack) throw new TFFNDanglingCloseBracketException();

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
                    else throw new TFFNUndefinedActionException(brackContent);

                    i++;
                } break;

                case '!': {
                    if(inBrack) throw new TFFNIgnoreTokenInsideBracketException();
                    if(i == formatLen - 1) throw new TFFNDanglingIgnoreTokenException();

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
            throw new TFFNUnclosedBracketException();
        }

        steps.flushBuffer();
        this.formatCache.put(format, steps);
        return steps.process();
    }


}
