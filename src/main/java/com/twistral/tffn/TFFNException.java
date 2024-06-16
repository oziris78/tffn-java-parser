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

    private TFFNException(String message) {
        super(message);
    }

    private TFFNException(String format, Object... args) {
        super(String.format(format, args));
    }

    static class TFFNNestingBracketsException extends TFFNException {
        public TFFNNestingBracketsException() {
            super("INVALID FORMAT: nesting brackets are prohibited in TFFN");
        }
    }
    
    static class TFFNDanglingCloseBracketException extends TFFNException {
        public TFFNDanglingCloseBracketException() {
            super("INVALID FORMAT: you forgot to open a bracket");
        }
    }

    static class TFFNUndefinedActionException extends TFFNException {
        public TFFNUndefinedActionException(String actionText) {
            super("INVALID FORMAT: '%s' action was never defined to the parser", actionText);
        }
    }

    static class TFFNDanglingIgnoreTokenException extends TFFNException {
        public TFFNDanglingIgnoreTokenException() {
            super("INVALID FORMAT: format string cant end with '!'");
        }
    }

    static class TFFNUnclosedBracketException extends TFFNException {
        public TFFNUnclosedBracketException() {
            super("INVALID FORMAT: you forgot to close a bracket");
        }
    }

    static class TFFNUnreachableException extends TFFNException {
        public TFFNUnreachableException() {
            super("This line should have been UNREACHABLE!");
        }
    }

    static class TFFNIgnoreTokenInsideBracketException extends TFFNException {
        public TFFNIgnoreTokenInsideBracketException() {
            super("INVALID FORMAT: '!' token cant be used inside brackets");
        }
    }

    static class TFFNActionTextAlreadyExistsException extends TFFNException {
        public TFFNActionTextAlreadyExistsException(String actionText) {
            super("An action with '%s' name already exists!", actionText);
        }
    }

}


