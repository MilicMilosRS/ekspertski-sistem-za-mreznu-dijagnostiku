package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resolution {
    private String description;
    private boolean finalDecision;

    @Override
    public String toString(){
        return "[Resolution" + (finalDecision? "#FINAL" :"" ) + "]" + description;
    }
}
