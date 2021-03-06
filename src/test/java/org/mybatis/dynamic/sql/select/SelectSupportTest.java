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
package org.mybatis.dynamic.sql.select;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.SqlConditions.*;

import java.sql.JDBCType;
import java.util.Date;
import java.util.Map;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.select.SelectSupport;

public class SelectSupportTest {
    
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    public static final SqlTable table = SqlTable.of("foo").withAlias("a");
    public static final SqlColumn<Date> column1 = SqlColumn.of("column1", JDBCType.DATE).inTable(table).withAlias("A_COLUMN1");
    public static final SqlColumn<Integer> column2 = SqlColumn.of("column2", JDBCType.INTEGER).inTable(table);

    @Test
    public void testSimpleCriteria() {
        Date d = new Date();

        SelectSupport selectSupport = select(column1, column2)
                .from(table)
                .where(column1, isEqualTo(d))
                .or(column2, isEqualTo(4))
                .and(column2, isLessThan(3))
                .build();

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("a.column1 as A_COLUMN1, a.column2");
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo("where a.column1 = {parameters.p1} or a.column2 = {parameters.p2} and a.column2 < {parameters.p3}");
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("");
        
        String expectedFullStatement = "select "
                + selectSupport.getColumnList()
                + " from foo a "
                + selectSupport.getWhereClause();
        
        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.get("p1")).isEqualTo(d);
        softly.assertThat(parameters.get("p2")).isEqualTo(4);
        softly.assertThat(parameters.get("p3")).isEqualTo(3);
    }

    @Test
    public void testComplexCriteria() {
        Date d = new Date();

        SelectSupport selectSupport = select(column1, column2)
                .from(table)
                .where(column1, isEqualTo(d))
                .or(column2, isEqualTo(4))
                .and(column2, isLessThan(3))
                .or(column2, isEqualTo(4), and(column2, isEqualTo(6)))
                .and(column2, isLessThan(3), or(column1, isEqualTo(d)))
                .build();
        

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("a.column1 as A_COLUMN1, a.column2");
        
        String expectedWhereClause = "where a.column1 = {parameters.p1}" +
                " or a.column2 = {parameters.p2}" +
                " and a.column2 < {parameters.p3}" +
                " or (a.column2 = {parameters.p4} and a.column2 = {parameters.p5})" +
                " and (a.column2 < {parameters.p6} or a.column1 = {parameters.p7})";
        
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo(expectedWhereClause);
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("");
        
        String expectedFullStatement = "select "
                + selectSupport.getColumnList()
                + " from foo a "
                + selectSupport.getWhereClause();

        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.get("p1")).isEqualTo(d);
        softly.assertThat(parameters.get("p2")).isEqualTo(4);
        softly.assertThat(parameters.get("p3")).isEqualTo(3);
        softly.assertThat(parameters.get("p4")).isEqualTo(4);
        softly.assertThat(parameters.get("p5")).isEqualTo(6);
        softly.assertThat(parameters.get("p6")).isEqualTo(3);
        softly.assertThat(parameters.get("p7")).isEqualTo(d);
    }

    @Test
    public void testOrderBySingleColumnAscending() {
        Date d = new Date();

        SelectSupport selectSupport = select(column1, column2)
                .from(table)
                .where(column1, isEqualTo(d))
                .orderBy(column1)
                .build();

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("a.column1 as A_COLUMN1, a.column2");
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo("where a.column1 = {parameters.p1}");
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("order by A_COLUMN1 ASC");
        
        String expectedFullStatement = "select "
                + selectSupport.getColumnList()
                + " from foo a "
                + selectSupport.getWhereClause()
                + " "
                + selectSupport.getOrderByClause();
        
        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.get("p1")).isEqualTo(d);
    }

    @Test
    public void testOrderBySingleColumnDescending() {
        Date d = new Date();

        SelectSupport selectSupport = select(column1, column2)
                .from(table)
                .where(column1, isEqualTo(d))
                .orderBy(column2.descending())
                .build();

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("a.column1 as A_COLUMN1, a.column2");
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo("where a.column1 = {parameters.p1}");
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("order by column2 DESC");
        
        String expectedFullStatement = "select "
                + selectSupport.getColumnList()
                + " from foo a "
                + selectSupport.getWhereClause()
                + " "
                + selectSupport.getOrderByClause();

        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.get("p1")).isEqualTo(d);
    }

    @Test
    public void testOrderByMultipleColumns() {
        Date d = new Date();

        SelectSupport selectSupport = select(column1, column2)
                .from(table)
                .where(column1, isEqualTo(d))
                .orderBy(column2.descending(), column1)
                .build();

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("a.column1 as A_COLUMN1, a.column2");
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo("where a.column1 = {parameters.p1}");
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("order by column2 DESC, A_COLUMN1 ASC");
        
        String expectedFullStatement = "select "
                + selectSupport.getColumnList()
                + " from foo a "
                + selectSupport.getWhereClause()
                + " "
                + selectSupport.getOrderByClause();

        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.get("p1")).isEqualTo(d);
    }

    @Test
    public void testDistinct() {
        Date d = new Date();

        SelectSupport selectSupport = select().distinct(column1, column2)
                .from(table)
                .where(column1, isEqualTo(d))
                .orderBy(column2.descending(), column1)
                .build();

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("distinct");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("a.column1 as A_COLUMN1, a.column2");
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo("where a.column1 = {parameters.p1}");
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("order by column2 DESC, A_COLUMN1 ASC");
        
        String expectedFullStatement = "select distinct "
                + selectSupport.getColumnList()
                + " from foo a "
                + selectSupport.getWhereClause()
                + " "
                + selectSupport.getOrderByClause();

        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.get("p1")).isEqualTo(d);
    }

    @Test
    public void testCount() {
        Date d = new Date();

        SelectSupport selectSupport = select().count()
                .from(table)
                .where(column1, isEqualTo(d))
                .build();

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("count(*)");
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo("where a.column1 = {parameters.p1}");
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("");
        
        String expectedFullStatement = "select "
                + selectSupport.getColumnList()
                + " from foo a "
                + selectSupport.getWhereClause();

        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.get("p1")).isEqualTo(d);
    }

    @Test
    public void testNoWhere() {
        SelectSupport selectSupport = select().count()
                .from(table)
                .build();

        softly.assertThat(selectSupport.getDistinct()).isEqualTo("");
        softly.assertThat(selectSupport.getColumnList()).isEqualTo("count(*)");
        softly.assertThat(selectSupport.getWhereClause()).isEqualTo("");
        softly.assertThat(selectSupport.getOrderByClause()).isEqualTo("");
        
        String expectedFullStatement = "select "
                + selectSupport.getColumnList()
                + " from foo a";

        softly.assertThat(selectSupport.getFullSelectStatement()).isEqualTo(expectedFullStatement);
        
        Map<String, Object> parameters = selectSupport.getParameters();
        softly.assertThat(parameters.size()).isEqualTo(0);
    }
}
