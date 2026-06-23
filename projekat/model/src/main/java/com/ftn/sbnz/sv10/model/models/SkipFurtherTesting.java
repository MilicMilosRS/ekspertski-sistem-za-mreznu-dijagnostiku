package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkipFurtherTesting {
    private boolean skip;

    @Override
    public String toString() {
        return "[SkipFurtherTesting] " + skip;
    }
}
