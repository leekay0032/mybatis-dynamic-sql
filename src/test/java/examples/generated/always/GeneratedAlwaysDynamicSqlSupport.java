/**
 *    Copyright 2016-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package examples.generated.always;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.SqlConditions.*;

import java.sql.JDBCType;

import org.mybatis.dynamic.sql.MyBatis3Column;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.InsertSupport;
import org.mybatis.dynamic.sql.update.UpdateSupport;
import org.mybatis.dynamic.sql.update.UpdateSupportBuilder;

public interface GeneratedAlwaysDynamicSqlSupport {
    SqlTable generatedAlways = SqlTable.of("GeneratedAlways").withAlias("a");
    MyBatis3Column<Integer> id = MyBatis3Column.of("id", JDBCType.INTEGER).inTable(generatedAlways);
    MyBatis3Column<String> firstName = MyBatis3Column.of("first_name", JDBCType.VARCHAR).inTable(generatedAlways);
    MyBatis3Column<String> lastName = MyBatis3Column.of("last_name", JDBCType.VARCHAR).inTable(generatedAlways);
    MyBatis3Column<String> fullName = MyBatis3Column.of("full_name", JDBCType.VARCHAR).inTable(generatedAlways);
    
    static InsertSupport<GeneratedAlwaysRecord> buildInsertSupport(GeneratedAlwaysRecord record) {
        return insert(record)
                .into(generatedAlways)
                .map(id).toProperty("id")
                .map(firstName).toProperty("firstName")
                .map(lastName).toProperty("lastName")
                .build();
    }

    static InsertSupport<GeneratedAlwaysRecord> buildInsertSelectiveSupport(GeneratedAlwaysRecord record) {
        return insert(record)
                .into(generatedAlways)
                .map(id).toPropertyWhenPresent("id")
                .map(firstName).toPropertyWhenPresent("firstName")
                .map(lastName).toPropertyWhenPresent("lastName")
                .build();
    }
    
    static UpdateSupport buildUpdateByPrimaryKeySupport(GeneratedAlwaysRecord record) {
        return update(generatedAlways)
                .set(firstName).equalTo(record.getFirstName())
                .set(lastName).equalTo(record.getLastName())
                .where(id, isEqualTo(record.getId()))
                .build();
    }

    static UpdateSupport buildUpdateByPrimaryKeySelectiveSupport(GeneratedAlwaysRecord record) {
        return update(generatedAlways)
                .set(firstName).equalToWhenPresent(record.getFirstName())
                .set(lastName).equalToWhenPresent(record.getLastName())
                .where(id, isEqualTo(record.getId()))
                .build();
    }

    static UpdateSupportBuilder updateByExample(GeneratedAlwaysRecord record) {
        return update(generatedAlways)
                .set(id).equalTo(record.getId())
                .set(firstName).equalTo(record.getFirstName())
                .set(lastName).equalTo(record.getLastName());
    }

    static UpdateSupportBuilder updateByExampleSelective(GeneratedAlwaysRecord record) {
        return update(generatedAlways)
                .set(id).equalToWhenPresent(record.getId())
                .set(firstName).equalToWhenPresent(record.getFirstName())
                .set(lastName).equalToWhenPresent(record.getLastName());
    }
}
