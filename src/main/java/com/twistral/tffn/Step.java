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


import java.util.function.Supplier;


class Step {

    final Supplier<String> dynamicStep; // function to run
    final String staticStep; // already existing string to replace

    Step(Supplier<String> dynamicStep) {
        this.dynamicStep = dynamicStep;
        this.staticStep = null;
    }

    Step(String staticStep) {
        this.staticStep = staticStep;
        this.dynamicStep = null;
    }

    String get() {
        return (dynamicStep != null) ? dynamicStep.get() : staticStep;
    }

}
