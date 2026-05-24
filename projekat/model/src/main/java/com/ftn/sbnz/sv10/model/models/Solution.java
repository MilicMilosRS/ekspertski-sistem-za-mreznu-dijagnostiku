package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Solution {
    private String description;
    private int priority;

    @Override
    public String toString(){
        return "[Solution #" + Integer.toString(priority) + "] " + description;
    }
}
