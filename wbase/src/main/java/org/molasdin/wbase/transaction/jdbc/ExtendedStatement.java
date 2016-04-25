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

package org.molasdin.wbase.transaction.jdbc;

import org.molasdin.wbase.storage.jdbc.RsTranslator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbersenev on 19.02.2016.
 */
public class ExtendedStatement {
    private PreparedStatement stmt;
    private ResultSet rs = null;
    private int index = 1;

    public ExtendedStatement(PreparedStatement stmt) {
        this.stmt = stmt;
    }

    public ExtendedStatement setInt(int pos, int item){
        try {
            stmt.setInt(pos, item);
            index = pos;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }
    public ExtendedStatement addInt(int item){
        return setInt(index, item);
    }
    public ExtendedStatement setInts(int from, int ...items) {
        for(int item: items) {
            setInt(from, item);
            from = from + 1;
        }
        return this;
    }

    public ExtendedStatement setLong(int pos, long item){
        try {
            stmt.setLong(pos, item);
            index = pos;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }
    public ExtendedStatement addLong(long item) {
        return setLong(index, item);
    }
    public ExtendedStatement setLongs(int from, long ...items) {
        for(long item: items){
            setLong(from, item);
            from = from + 1;
        }
        return this;
    }

    public ExtendedStatement setString(int pos, String item) {
        try {
            stmt.setString(pos, item);
            index = pos;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }
    public ExtendedStatement addString(String item) {
        return setString(index, item);
    }
    public ExtendedStatement setStrings(int from ,String ...items){
        for(String item: items){
            setString(from, item);
            from = from + 1;
        }
        return this;
    }

    public ExtendedStatement setObject(int pos, Object item) {
        try {
            stmt.setObject(pos, item);
            index = pos;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }
    public ExtendedStatement addObject(Object arg){
        return setObjects(index, arg);
    }
    public ExtendedStatement setObjects(int from, Object... args) {
        for(Object entry: args) {
            setObject(from, entry);
            from = from + 1;
        }
        return this;
    }

    public ExtendedStatement reset(){
        index = 1;
        return this;
    }

    public String stringResult() {
        try {
            return execute(true).getString(1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeRs();
        }
    }

    public long longResult() {
        try {
            return execute(true).getLong(1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeRs();
        }
    }

    public int intResult() {
        try {
            return execute(true).getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeRs();
        }
    }

    public <U> List<U> objectsResult(RsTranslator<U> tr){
        ResultSet rs = execute(true);
        List<U> result = new ArrayList<>();
        try {
            do {
                U item = tr.translate(rs);
                result.add(item);
            } while (rs.next());
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
        return result;
    }

    public ResultSet result() {
        return execute(false);
    }

    private ResultSet execute(boolean move){
        try {
            rs =  stmt.executeQuery();
            if(move) {
                rs.next();
            }
            return rs;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void closeRs() {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
