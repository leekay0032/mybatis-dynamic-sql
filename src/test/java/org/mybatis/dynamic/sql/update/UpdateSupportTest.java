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
package org.mybatis.dynamic.sql.update;

import static org.mybatis.dynamic.sql.SqlBuilder.update;
import static org.mybatis.dynamic.sql.SqlConditions.isEqualTo;

import java.sql.JDBCType;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public class UpdateSupportTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final SqlTable foo = SqlTable.of("foo");
    private static final SqlColumn<Integer> id = SqlColumn.of("id", JDBCType.INTEGER);
    private static final SqlColumn<String> firstName = SqlColumn.of("firstName", JDBCType.VARCHAR);
    private static final SqlColumn<String> lastName = SqlColumn.of("lastName", JDBCType.VARCHAR);
    private static final SqlColumn<String> occupation = SqlColumn.of("occupation", JDBCType.VARCHAR);

    @Test
    public void testUpdateParameter() {
        UpdateSupport updateSupport = update(foo)
                .set(firstName).equalTo("fred")
                .set(lastName).equalTo("jones")
                .set(occupation).equalToNull()
                .where(id, isEqualTo(3))
                .build();
        
        String expectedSetClause = "set firstName = {parameters.up1}, lastName = {parameters.up2}, occupation = null";
                
        softly.assertThat(updateSupport.getSetClause()).isEqualTo(expectedSetClause);
        
        String expectedWhereClauses = "where id = {parameters.p1}";
        softly.assertThat(updateSupport.getWhereClause()).isEqualTo(expectedWhereClauses);
        
        softly.assertThat(updateSupport.getParameters().size()).isEqualTo(3);
        softly.assertThat(updateSupport.getParameters().get("up1")).isEqualTo("fred");
        softly.assertThat(updateSupport.getParameters().get("up2")).isEqualTo("jones");
        softly.assertThat(updateSupport.getParameters().get("p1")).isEqualTo(3);
    }

    @Test
    public void testUpdateParameterStartWithNull() {
        UpdateSupport updateSupport = update(foo)
                .set(occupation).equalToNull()
                .set(firstName).equalTo("fred")
                .set(lastName).equalTo("jones")
                .where(id, isEqualTo(3))
                .and(firstName, isEqualTo("barney"))
                .build();
        
        String expectedSetClause = "set occupation = null, firstName = {parameters.up1}, lastName = {parameters.up2}";
                
        softly.assertThat(updateSupport.getSetClause()).isEqualTo(expectedSetClause);
        
        String expectedWhereClauses = "where id = {parameters.p1} and firstName = {parameters.p2}";
        softly.assertThat(updateSupport.getWhereClause()).isEqualTo(expectedWhereClauses);
        
        softly.assertThat(updateSupport.getParameters().size()).isEqualTo(4);
        softly.assertThat(updateSupport.getParameters().get("up1")).isEqualTo("fred");
        softly.assertThat(updateSupport.getParameters().get("up2")).isEqualTo("jones");
        softly.assertThat(updateSupport.getParameters().get("p1")).isEqualTo(3);
        softly.assertThat(updateSupport.getParameters().get("p2")).isEqualTo("barney");
    }
    
    @Test
    public void testUpdateParameterStartWithConstant() {
        UpdateSupport updateSupport = update(foo)
                .set(occupation).equalToConstant("'Y'")
                .set(firstName).equalTo("fred")
                .set(lastName).equalTo("jones")
                .where(id, isEqualTo(3))
                .and(firstName, isEqualTo("barney"))
                .build();
        
        String expectedSetClause = "set occupation = 'Y', firstName = {parameters.up1}, lastName = {parameters.up2}";
                
        softly.assertThat(updateSupport.getSetClause()).isEqualTo(expectedSetClause);
        
        String expectedWhereClauses = "where id = {parameters.p1} and firstName = {parameters.p2}";
        softly.assertThat(updateSupport.getWhereClause()).isEqualTo(expectedWhereClauses);
        
        softly.assertThat(updateSupport.getParameters().size()).isEqualTo(4);
        softly.assertThat(updateSupport.getParameters().get("up1")).isEqualTo("fred");
        softly.assertThat(updateSupport.getParameters().get("up2")).isEqualTo("jones");
        softly.assertThat(updateSupport.getParameters().get("p1")).isEqualTo(3);
        softly.assertThat(updateSupport.getParameters().get("p2")).isEqualTo("barney");
    }
    
    @Test
    public void testFullUpdateStatement() {
        UpdateSupport updateSupport = update(foo)
                .set(firstName).equalTo("fred")
                .set(lastName).equalTo("jones")
                .set(occupation).equalToNull()
                .where(id, isEqualTo(3))
                .build();
        
        String expectedStatement = "update foo " 
                + "set firstName = {parameters.up1}, lastName = {parameters.up2}, occupation = null "
                + "where id = {parameters.p1}";
                
        softly.assertThat(updateSupport.getFullUpdateStatement()).isEqualTo(expectedStatement);
        
        softly.assertThat(updateSupport.getParameters().size()).isEqualTo(3);
        softly.assertThat(updateSupport.getParameters().get("up1")).isEqualTo("fred");
        softly.assertThat(updateSupport.getParameters().get("up2")).isEqualTo("jones");
        softly.assertThat(updateSupport.getParameters().get("p1")).isEqualTo(3);
    }

    @Test
    public void testFullUpdateStatementNoWhere() {
        UpdateSupport updateSupport = update(foo)
                .set(firstName).equalTo("fred")
                .set(lastName).equalTo("jones")
                .set(occupation).equalToNull()
                .build();
        
        String expectedStatement = "update foo " 
                + "set firstName = {parameters.up1}, lastName = {parameters.up2}, occupation = null";
                
        softly.assertThat(updateSupport.getFullUpdateStatement()).isEqualTo(expectedStatement);
        
        softly.assertThat(updateSupport.getParameters().size()).isEqualTo(2);
        softly.assertThat(updateSupport.getParameters().get("up1")).isEqualTo("fred");
        softly.assertThat(updateSupport.getParameters().get("up2")).isEqualTo("jones");
    }
}
