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


import static com.twistral.tffn.TFFNException.*;
import java.util.HashMap;
import java.util.function.Supplier;


class Steps {

    // Actual objects used by the class
    private final HashMap<Integer, Supplier<String>> dynamicParts;
    private final HashMap<Integer, String> staticParts;
    private final StringBuilder sb;
    private int partCount;

    // Meaningless objects to speed up the class
    private final StringBuilder temp = new StringBuilder(128);


    Steps() {
        this.dynamicParts = new HashMap<>(32);
        this.staticParts = new HashMap<>(32);
        this.sb = new StringBuilder(32);
        this.partCount = 0;
    }


    public void addStaticCharStep(char step) {
        this.sb.append(step);
    }


    public void addStaticStringStep(String step) {
        this.sb.append(step);
    }


    public void addDynamicStep(Supplier<String> step) {
        flushBuffer();
        this.dynamicParts.put(partCount, step);
        partCount++;
    }


    public String process() {
        temp.setLength(0);

        for (int i = 0; i < partCount; i++) {
            if(staticParts.containsKey(i)) {
                temp.append(staticParts.get(i));
            }
            else if(dynamicParts.containsKey(i)) {
                temp.append(dynamicParts.get(i).get());
            }
            else throw new TFFNUnreachableException();
        }

        return temp.toString();
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
