/*
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twosigma.webtau.expectation.equality.handlers;

import com.twosigma.webtau.expectation.ActualPath;
import com.twosigma.webtau.expectation.equality.EqualComparator;
import com.twosigma.webtau.expectation.equality.EqualComparatorHandler;
import com.twosigma.webtau.utils.JavaBeanUtils;

import java.util.Map;

public class MapAndBeanEqualHandler implements EqualComparatorHandler {
    @Override
    public boolean handle(Object actual, Object expected) {
        return isMapOfProps(expected) && isBean(actual);
    }

    @SuppressWarnings("unchecked")
    private boolean isMapOfProps(Object o) {
        if (!(o instanceof Map)) {
            return false;
        }

        return ((Map) o).keySet().stream().allMatch(k -> k instanceof String);
    }

    private boolean isBean(Object o) {
        if (o instanceof Iterable || o instanceof Map) {
            return false;
        }

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void compare(EqualComparator equalComparator, ActualPath actualPath, Object actual, Object expected) {
        Map<String, ?> expectedMap = (Map<String, ?>) expected;
        Map<String, ?> actualAsMap = JavaBeanUtils.convertBeanToMap(actual);

        expectedMap.keySet().forEach(p -> {
            ActualPath propertyPath = actualPath.property(p);

            if (! actualAsMap.containsKey(p)) {
                equalComparator.reportMissing(this, propertyPath, expectedMap.get(p));
            } else {
                equalComparator.compare(propertyPath, actualAsMap.get(p), expectedMap.get(p));
            }
        });
    }
}