/*
 * Copyright 2015 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.storage.jdbc;

import org.molasdin.wbase.Resource;
import org.molasdin.wbase.storage.cursor.AbstractBatchCursor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by molasdin on 12/16/15.
 */
public class JdbcBatchCursor<T> extends AbstractBatchCursor<T> {

    private Resource<ResultSet> rs;
    private RsTranslator<T> translator;

    public JdbcBatchCursor(Resource<ResultSet> rs, long total, long pageSize, RsTranslator<T> translator) {
        super(pageSize, total);
        this.rs = rs;
        this.translator = translator;
        setData(new ArrayList<>((int) pageSize));
    }

    @Override
    protected void load() {
        try {
            data().clear();
            long dataToGet = pageSize();
            while (dataToGet > 0 && rs.resource().next()) {
                data().add(translator.translate(rs.resource()));
                dataToGet = dataToGet - 1;
            }
            if (dataToGet > 0) {
                setExhausted(true);
                rs.close();
            }
        } catch (Exception ex) {
            close();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        rs.close();
    }
}
