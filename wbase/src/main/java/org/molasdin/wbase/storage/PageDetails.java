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

package org.molasdin.wbase.storage;

/**
 * Created by molasdin on 2/23/16.
 */
public class PageDetails {
    private long offset;
    private long size;

    public PageDetails(long offset, long size) {
        this.offset = offset;
        this.size = size;
    }

    public long offset(){
        return offset;
    }

    public long size(){
        return size;
    }
}
