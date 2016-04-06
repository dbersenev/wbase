/*
 * Copyright 2016 Bersenev Dmitry molasdin@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.molasdin.wbase.util;

import java.util.concurrent.Callable;

/**
 * Created by molasdin on 2/9/16.
 */
public interface Exceptions {

    static void wrapped(Block code) {
        try{
            code.run();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    static <U> U wrapped(Callable<U> code){
        try{
            return code.call();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
