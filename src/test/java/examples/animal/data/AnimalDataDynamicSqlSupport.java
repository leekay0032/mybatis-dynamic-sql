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
package examples.animal.data;

import java.sql.JDBCType;

import org.mybatis.dynamic.sql.MyBatis3Column;
import org.mybatis.dynamic.sql.SqlTable;

public interface AnimalDataDynamicSqlSupport {
    SqlTable animalData = SqlTable.of("AnimalData").withAlias("a");
    MyBatis3Column<Integer> id = MyBatis3Column.of("id", JDBCType.INTEGER).inTable(animalData); 
    MyBatis3Column<String> animalName = MyBatis3Column.of("animal_name", JDBCType.VARCHAR).inTable(animalData);
    MyBatis3Column<Double> bodyWeight = MyBatis3Column.of("body_weight", JDBCType.DOUBLE).inTable(animalData);
    MyBatis3Column<Double> brainWeight = MyBatis3Column.of("brain_weight", JDBCType.DOUBLE).inTable(animalData);
}
