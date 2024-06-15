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


class Steps {

    private final HashMap<Integer, Supplier<String>> dynamicParts = new HashMap<>(32);
    private final HashMap<Integer, String> staticParts = new HashMap<>(32);
    private final StringBuilder sb = new StringBuilder(32);
    private int partCount = 0;

    public Steps() {}

    public void addStaticCharStep(char step) {
        sb.append(step);
    }

    public void addStaticStringStep(String step) {
        sb.append(step);
    }

    public void addDynamicStep(Supplier<String> step) {
        flushBuffer();
        this.dynamicParts.put(partCount, step);
        partCount++;
    }

    public String process() {
        StringBuilder result = new StringBuilder(64);

        for (int i = 0; i < partCount; i++) {
            if(staticParts.containsKey(i)) {
                final String str = staticParts.get(i);
                System.out.printf("%d S: '%s'\n", i, str);
                result.append(str);
            }
            else if(dynamicParts.containsKey(i)) {
                final String str = dynamicParts.get(i).get();
                System.out.printf("%d D: '%s'\n", i, str);
                result.append(str);
            }
            else {
                throw new TFFNException(String.format("A step with %d integer ID does not exist.", i));
            }
        }

        return result.toString();
    }

    public void flushBuffer() {
        if(sb.length() > 0) {
            final String prevStaticPart = sb.toString();
            this.staticParts.put(partCount, prevStaticPart);
            partCount++;
            sb.setLength(0);
        }
    }

}
