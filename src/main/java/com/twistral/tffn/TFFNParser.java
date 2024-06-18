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
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static com.twistral.tffn.TFFNException.*;


public class TFFNParser {

    private final HashMap<String, Consumer<StringBuilder>> dynamicActions;    // actionText -> action
    private final HashMap<String, String> staticActions;                      // actionText -> action
    private final HashMap<String, LinkedList<Step>> formatCache;              // format -> steps


    public TFFNParser() {
        this.dynamicActions = new HashMap<>(64);
        this.staticActions = new HashMap<>(64);
        this.formatCache = new HashMap<>(64);
    }


    /////////////////////////////////////////////////////////////////////
    /////////////////////////////  METHODS  /////////////////////////////
    /////////////////////////////////////////////////////////////////////

    private final StringBuilder sbRes = new StringBuilder(64); // for speed


    /**
     * Defines a dynamic action to the parser.
     * @param actionText the text inside the brackets that will fire this action
     * @param dynamicAction a function that will manipulate the inner StringBuilder of this parser
     * @return this parser for method chaining
     */
    public TFFNParser defineDynamicAction(String actionText, Consumer<StringBuilder> dynamicAction) {
        if(staticActions.containsKey(actionText) || dynamicActions.containsKey(actionText)) {
            throw new TFFNException(ACTION_TEXT_ALREADY_EXISTS, actionText);
        }

        dynamicActions.put(actionText, dynamicAction);
        return this;
    }


    /**
     * Defines a static action to the parser.
     * @param actionText the text inside the brackets that will fire this action
     * @param staticAction the string that will replace the action
     * @return this parser for method chaining
     */
    public TFFNParser defineStaticAction(String actionText, String staticAction) {
        if(staticActions.containsKey(actionText) || dynamicActions.containsKey(actionText)) {
            throw new TFFNException(ACTION_TEXT_ALREADY_EXISTS, actionText);
        }

        staticActions.put(actionText, staticAction);
        return this;
    }


    /**
     * This function parses a TFFN formatted string and returns the produced string. <br>
     * It replaces all the actions according to the actions that were previously defined to the parser.
     * @param format any string in valid TFFN format
     * @return the produced string
     */
    public String parse(String format) {
        LinkedList<Step> steps = formatCache.containsKey(format) ? formatCache.get(format) : parseSteps(format);

        sbRes.setLength(0);
        steps.forEach(step -> {
            if(step.dynamicStep != null) {
                step.dynamicStep.accept(sbRes);
            }
            else {
                sbRes.append(step.staticStep);
            }
        });
        return sbRes.toString();
    }


    //////////////////////////////////////////////////////////////////////////////
    /////////////////////////////  HELPER FUNCTIONS  /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    private final StringBuilder sbPart = new StringBuilder(64); // for speed

    private LinkedList<Step> parseSteps(String format) {
        sbPart.setLength(0);

        final LinkedList<Step> steps = new LinkedList<>();
        final int formatLen = format.length();
        StringBuilder brackText = new StringBuilder(formatLen);
        boolean inBrack = false;

        int i = 0;
        while(i < formatLen) {
            final char c = format.charAt(i);

            switch (c) {
                case '[': {
                    if(inBrack) throw new TFFNException(NESTING_BRACKETS);

                    inBrack = true;
                    i++;
                } break;

                case ']': {
                    if(!inBrack) throw new TFFNException(DANGLING_CLOSE_BRACKET);

                    inBrack = false;

                    final String brackContent = brackText.toString();
                    brackText.setLength(0);

                    if(staticActions.containsKey(brackContent)) {
                        final String step = staticActions.get(brackContent);
                        sbPart.append(step);
                    }
                    else if(dynamicActions.containsKey(brackContent)) {
                        final Consumer<StringBuilder> step = dynamicActions.get(brackContent);
                        if(sbPart.length() > 0) {
                            steps.add(new Step(sbPart.toString()));
                            sbPart.setLength(0);
                        }
                        steps.add(new Step(step));
                    }
                    else throw new TFFNException(UNDEFINED_ACTION, brackContent);

                    i++;
                } break;

                case '!': {
                    if(inBrack) throw new TFFNException(IGNORE_TOKEN_INSIDE_BRACKET);
                    if(i == formatLen - 1) throw new TFFNException(DANGLING_IGNORE_TOKEN);

                    final char nextChar = format.charAt(i + 1);
                    sbPart.append(nextChar);
                    i += 2;
                } break;

                default: {
                    if(inBrack) brackText.append(c);
                    else sbPart.append(c);

                    i++;
                } break;
            }
        }

        // The format string ended but bracketText isnt empty so the last bracket was never closed
        if(brackText.length() != 0) {
            throw new TFFNException(UNCLOSED_BRACKET);
        }

        // Add the final static string part as a step
        if(sbPart.length() > 0) {
            steps.add(new Step(sbPart.toString()));
            sbPart.setLength(0);
        }

        this.formatCache.put(format, steps);
        return steps;
    }


}
