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
package org.mybatis.dynamic.sql.mybatis3;

import static org.mybatis.dynamic.sql.SqlBuilder.insert;

import java.sql.JDBCType;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mybatis.dynamic.sql.MyBatis3Column;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.InsertSupport;

public class InsertSupportTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final SqlTable foo = SqlTable.of("foo");
    private static final MyBatis3Column<Integer> id = MyBatis3Column.of("id", JDBCType.INTEGER);
    private static final MyBatis3Column<String> firstName = MyBatis3Column.of("first_name", JDBCType.VARCHAR);
    private static final MyBatis3Column<String> lastName = MyBatis3Column.of("last_name", JDBCType.VARCHAR);
    private static final MyBatis3Column<String> occupation = MyBatis3Column.of("occupation", JDBCType.VARCHAR);

    @Test
    public void testFullInsertSupportBuilder() {
        TestRecord record = new TestRecord();
        record.setLastName("jones");
        record.setOccupation("dino driver");
        
        InsertSupport<?> insertSupport = insert(record)
                .into(foo)
                .map(id).toProperty("id")
                .map(firstName).toProperty("firstName")
                .map(lastName).toProperty("lastName")
                .map(occupation).toProperty("occupation")
                .build();

        String expectedColumnsPhrase = "(id, first_name, last_name, occupation)";
        softly.assertThat(insertSupport.getColumnsPhrase()).isEqualTo(expectedColumnsPhrase);

        String expectedValuesPhrase = "values (#{record.id,jdbcType=INTEGER}, "
                + "#{record.firstName,jdbcType=VARCHAR}, "
                + "#{record.lastName,jdbcType=VARCHAR}, "
                + "#{record.occupation,jdbcType=VARCHAR})";
        softly.assertThat(insertSupport.getValuesPhrase()).isEqualTo(expectedValuesPhrase);
    }

    @Test
    public void testSelectiveInsertSupportBuilder() {
        TestRecord record = new TestRecord();
        record.setLastName("jones");
        record.setOccupation("dino driver");
        
        InsertSupport<?> insertSupport = insert(record)
                .into(foo)
                .map(id).toPropertyWhenPresent("id")
                .map(firstName).toPropertyWhenPresent("firstName")
                .map(lastName).toPropertyWhenPresent("lastName")
                .map(occupation).toPropertyWhenPresent("occupation")
                .build();

        String expectedColumnsPhrase = "(last_name, occupation)";
        softly.assertThat(insertSupport.getColumnsPhrase()).isEqualTo(expectedColumnsPhrase);

        String expectedValuesPhrase = "values (#{record.lastName,jdbcType=VARCHAR}, "
                + "#{record.occupation,jdbcType=VARCHAR})";
        softly.assertThat(insertSupport.getValuesPhrase()).isEqualTo(expectedValuesPhrase);
    }

    public static class TestRecord {
        private Integer id;
        private String firstName;
        private String lastName;
        private String occupation;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }
    }
}
