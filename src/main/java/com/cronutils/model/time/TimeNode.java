package com.cronutils.model.time;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
 * Copyright 2015 jmrozanec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class TimeNode {
    protected List<Integer> values;

    public TimeNode(List<Integer> values){
        Validate.notEmpty(values, "Values must not be empty");
        this.values = values;
        Collections.sort(this.values);
    }

    public NearestValue getNextValue(int reference, int shifts){
        return getNearestValues(reference, shifts, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer+1;
            }
        });
    }

    public List<Integer> getValues(){
        return Collections.unmodifiableList(values);
    }

    public NearestValue getPreviousValue(int reference, int shifts){
        return getNearestValues(reference, shifts, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer-1;
            }
        });
    }

    //we return same reference value if matches or next one if does not match.
    //then we start applying shifts. This way we ensure same value is returned if no shift is requested
    private NearestValue getNearestValues(int reference, int shiftsToApply, Function<Integer, Integer> indexTransform){
        List<Integer> values = new ArrayList<Integer>(this.values);
        int index=0;
        if (!values.contains(reference)) {
            for(Integer value : values){
                if(value>reference){
                    index = values.indexOf(value);
                    break;
                }
            }
        }else{
            index = values.indexOf(reference);
        }
        int shift = 0;
        int value = 0;
        for(int j=0;j<shiftsToApply;j++){
            value = getValueFromList(values, indexTransform.apply(index), shift);
            index = values.indexOf(reference);
        }
        return new NearestValue(value, shift);
    }

    private int getValueFromList(List<Integer>values, int index, int shift){
        Validate.notEmpty(values, "List must not be empty");
        if(index<0){
            index=index+values.size();
            return getValueFromList(values, index, shift+1);
        }
        if(index>=values.size()){
            index=index-values.size();
            return getValueFromList(values, index, shift+1);
        }
        return values.get(index);
    }
}
