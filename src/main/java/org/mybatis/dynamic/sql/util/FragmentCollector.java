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
package org.mybatis.dynamic.sql.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class FragmentCollector {
    List<String> fragments = new ArrayList<>();
    Map<String, Object> parameters = new HashMap<>();
    
    public FragmentCollector() {
        super();
    }
    
    public void add(FragmentAndParameters fragmentAndParameters) {
        fragments.add(fragmentAndParameters.fragment());
        parameters.putAll(fragmentAndParameters.parameters());
    }
    
    public void add(Triple triple) {
        fragments.add(triple.jdbcPlaceholder());
        parameters.put(triple.mapKey(), triple.value());
    }
    
    public FragmentCollector merge(FragmentCollector other) {
        fragments.addAll(other.fragments);
        parameters.putAll(other.parameters);
        return this;
    }
    
    public Stream<String> fragments() {
        return fragments.stream();
    }
    
    public Map<String, Object> parameters() {
        return parameters;
    }
    
    public static Collector<Triple, FragmentCollector, FragmentCollector> tripleCollector() {
        return Collector.of(FragmentCollector::new, FragmentCollector::add, FragmentCollector::merge);
    }
    
    public static Collector<FragmentAndParameters, FragmentCollector, FragmentCollector> fragmentAndParameterCollector() {
        return Collector.of(FragmentCollector::new, FragmentCollector::add, FragmentCollector::merge);
    }
    
    public static class Triple {
        private String mapKey;
        private String jdbcPlaceholder;
        private Object value;
        
        private Triple() {
            super();
        }
        
        public String mapKey() {
            return mapKey;
        }
        
        public String jdbcPlaceholder() {
            return jdbcPlaceholder;
        }
        
        public Object value() {
            return value;
        }
        
        public static Triple of(String mapKey, String jdbcPlaceholder, Object value) {
            Triple triple = new Triple();
            triple.mapKey = mapKey;
            triple.jdbcPlaceholder = jdbcPlaceholder;
            triple.value = value;
            return triple;
        }
    }
}