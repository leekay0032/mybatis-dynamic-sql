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
package org.mybatis.dynamic.sql.insert;

import static org.assertj.core.api.Assertions.*;
import static org.mybatis.dynamic.sql.SqlBuilder.insert;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public class InsertSupportTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final SqlTable foo = SqlTable.of("foo");
    private static final SqlColumn<Integer> id = SqlColumn.of("id", JDBCType.INTEGER);
    private static final SqlColumn<String> firstName = SqlColumn.of("first_name", JDBCType.VARCHAR);
    private static final SqlColumn<String> lastName = SqlColumn.of("last_name", JDBCType.VARCHAR);
    private static final SqlColumn<String> occupation = SqlColumn.of("occupation", JDBCType.VARCHAR);
    
    @Test
    public void testFullInsertSupportBuilder() {

        TestRecord record = new TestRecord();
        record.setLastName("jones");
        record.setOccupation("dino driver");
        
        InsertSupport<TestRecord> insertSupport = insert(record)
                .into(foo)
                .map(id).toProperty("id")
                .map(firstName).toProperty("firstName")
                .map(lastName).toProperty("lastName")
                .map(occupation).toProperty("occupation")
                .build();

        String expectedColumnsPhrase = "(id, first_name, last_name, occupation)";
        softly.assertThat(insertSupport.getColumnsPhrase()).isEqualTo(expectedColumnsPhrase);

        String expectedValuesPhrase = "values ({record.id}, {record.firstName}, {record.lastName}, {record.occupation})";
        softly.assertThat(insertSupport.getValuesPhrase()).isEqualTo(expectedValuesPhrase);
    }

    @Test
    public void testInsertSupportBuilderWithNulls() {

        TestRecord record = new TestRecord();
        
        InsertSupport<TestRecord> insertSupport = insert(record)
                .into(foo)
                .map(id).toProperty("id")
                .map(firstName).toProperty("firstName")
                .map(lastName).toProperty("lastName")
                .map(occupation).toNull()
                .build();

        String expectedColumnsPhrase = "(id, first_name, last_name, occupation)";
        softly.assertThat(insertSupport.getColumnsPhrase()).isEqualTo(expectedColumnsPhrase);

        String expectedValuesPhrase = "values ({record.id}, {record.firstName}, {record.lastName}, null)";
        softly.assertThat(insertSupport.getValuesPhrase()).isEqualTo(expectedValuesPhrase);
    }

    @Test
    public void testInsertSupportBuilderWithConstants() {

        TestRecord record = new TestRecord();
        
        InsertSupport<TestRecord> insertSupport = insert(record)
                .into(foo)
                .map(id).toProperty("id")
                .map(firstName).toProperty("firstName")
                .map(lastName).toProperty("lastName")
                .map(occupation).toConstant("'Y'")
                .build();

        String expectedColumnsPhrase = "(id, first_name, last_name, occupation)";
        softly.assertThat(insertSupport.getColumnsPhrase()).isEqualTo(expectedColumnsPhrase);

        String expectedValuesPhrase = "values ({record.id}, {record.firstName}, {record.lastName}, 'Y')";
        softly.assertThat(insertSupport.getValuesPhrase()).isEqualTo(expectedValuesPhrase);
    }
    
    @Test
    public void testSelectiveInsertSupportBuilder() {
        TestRecord record = new TestRecord();
        record.setLastName("jones");
        record.setOccupation("dino driver");
        
        InsertSupport<TestRecord> insertSupport = insert(record)
                .into(foo)
                .map(id).toPropertyWhenPresent("id")
                .map(firstName).toPropertyWhenPresent("firstName")
                .map(lastName).toPropertyWhenPresent("lastName")
                .map(occupation).toPropertyWhenPresent("occupation")
                .build();

        String expectedColumnsPhrase = "(last_name, occupation)";
        softly.assertThat(insertSupport.getColumnsPhrase()).isEqualTo(expectedColumnsPhrase);

        String expectedValuesPhrase = "values ({record.lastName}, {record.occupation})";
        softly.assertThat(insertSupport.getValuesPhrase()).isEqualTo(expectedValuesPhrase);
    }

    @Test
    public void testParallelStream() {

        TestRecord record = new TestRecord();
        record.setLastName("jones");
        record.setOccupation("dino driver");
        
        List<InsertColumnMapping> mappings = new ArrayList<>();
        
        mappings.add(InsertColumnMapping.ofPropertyMap(id, "id"));
        mappings.add(InsertColumnMapping.ofPropertyMap(firstName, "firstName"));
        mappings.add(InsertColumnMapping.ofPropertyMap(lastName, "lastName"));
        mappings.add(InsertColumnMapping.ofPropertyMap(occupation, "occupation"));
        
        InsertColumnMappingCollector<TestRecord> collector = 
                mappings.parallelStream().collect(Collector.of(
                        () -> new InsertColumnMappingCollector<>(record, foo),
                        InsertColumnMappingCollector::add,
                        InsertColumnMappingCollector::merge));
                
        String expectedColumnsPhrase = "(id, first_name, last_name, occupation)";
        softly.assertThat(collector.columnsPhrase()).isEqualTo(expectedColumnsPhrase);

        String expectedValuesPhrase = "values ({record.id}, {record.firstName}, {record.lastName}, {record.occupation})";
        softly.assertThat(collector.valuesPhrase()).isEqualTo(expectedValuesPhrase);
    }
    
    @Test
    public void testFullInsertStatementBuilder() {

        TestRecord record = new TestRecord();
        record.setLastName("jones");
        record.setOccupation("dino driver");
        
        InsertSupport<TestRecord> insertSupport = insert(record)
                .into(foo)
                .map(id).toProperty("id")
                .map(firstName).toProperty("firstName")
                .map(lastName).toProperty("lastName")
                .map(occupation).toProperty("occupation")
                .build();

        String expectedStatement = "insert into foo "
                + "(id, first_name, last_name, occupation) "
                + "values ({record.id}, {record.firstName}, {record.lastName}, {record.occupation})";
        
        assertThat(insertSupport.getFullInsertStatement()).isEqualTo(expectedStatement);
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
