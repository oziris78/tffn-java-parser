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


class TFFNException extends RuntimeException {

    static final String DANGLING_CLOSE_BRACKET = "INVALID FORMAT: you forgot to open a bracket";
    static final String NESTING_BRACKETS = "INVALID FORMAT: nesting brackets are prohibited in TFFN";
    static final String DANGLING_IGNORE_TOKEN = "INVALID FORMAT: format string cant end with '!'";
    static final String UNCLOSED_BRACKET = "INVALID FORMAT: you forgot to close a bracket";
    static final String IGNORE_TOKEN_INSIDE_BRACKET = "INVALID FORMAT: '!' token cant be used inside brackets";
    static final String UNDEFINED_ACTION = "INVALID FORMAT: '%s' action was never defined to the parser";
    static final String ACTION_TEXT_ALREADY_EXISTS = "An action with '%s' name already exists!";

    public TFFNException(String exceptionFormat, Object... args) {
        super(String.format(exceptionFormat, args));
    }

}

