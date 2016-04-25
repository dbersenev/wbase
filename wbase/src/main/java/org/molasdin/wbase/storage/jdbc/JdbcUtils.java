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

package org.molasdin.wbase.storage.jdbc;

import org.apache.commons.lang3.text.StrBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dbersenev on 25.04.2016.
 */
public class JdbcUtils {
    public static String charToString(ResultSet rs, String column) {
        String tmp = null;
        try {
            tmp = rs.getString(column);
            if(rs.wasNull()) {
                return null;
            }
            return tmp;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String inStatement(int count){
        StrBuilder builder = new StrBuilder();
        for(int i = 0; i < count; i = i + 1){
            builder.appendSeparator(',');
            builder.append('?');
        }
        return builder.toString();
    }
}
