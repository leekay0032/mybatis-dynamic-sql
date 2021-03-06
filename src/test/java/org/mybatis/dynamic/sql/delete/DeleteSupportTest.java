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
package org.mybatis.dynamic.sql.delete;

import static org.mybatis.dynamic.sql.SqlBuilder.deleteFrom;
import static org.mybatis.dynamic.sql.SqlConditions.isEqualTo;
import static org.mybatis.dynamic.sql.SqlConditions.isLikeCaseInsensitive;

import java.sql.JDBCType;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public class DeleteSupportTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final SqlTable foo = SqlTable.of("foo").withAlias("A");
    private static final SqlColumn<Integer> id = SqlColumn.of("id", JDBCType.INTEGER);
    private static final SqlColumn<String> firstName = SqlColumn.of("first_name", JDBCType.VARCHAR);

    @Test
    public void testFullStatement() {
        DeleteSupport deleteSupport = deleteFrom(foo)
                .where(id, isEqualTo(3))
                .or(firstName, isLikeCaseInsensitive("%Fr%"))
                .build();

        String expectedWhereClause = "where id = {parameters.p1} or upper(first_name) like {parameters.p2}";
        softly.assertThat(deleteSupport.getWhereClause()).isEqualTo(expectedWhereClause);

        String expectedFullStatement = "delete from foo where id = {parameters.p1} or upper(first_name) like {parameters.p2}";
        softly.assertThat(deleteSupport.getFullDeleteStatement()).isEqualTo(expectedFullStatement);
        
        softly.assertThat(deleteSupport.getParameters().size()).isEqualTo(2);
        softly.assertThat(deleteSupport.getParameters().get("p1")).isEqualTo(3);
        softly.assertThat(deleteSupport.getParameters().get("p2")).isEqualTo("%FR%");
    }

    @Test
    public void testFullStatementWithoutWhere() {
        DeleteSupport deleteSupport = deleteFrom(foo)
                .build();

        String expectedWhereClause = "";
        softly.assertThat(deleteSupport.getWhereClause()).isEqualTo(expectedWhereClause);

        String expectedFullStatement = "delete from foo";
        softly.assertThat(deleteSupport.getFullDeleteStatement()).isEqualTo(expectedFullStatement);
        softly.assertThat(deleteSupport.getParameters().size()).isEqualTo(0);
    }
}
