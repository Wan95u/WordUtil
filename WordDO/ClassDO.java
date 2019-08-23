package com.cmcc.cmii.ambulance.common.utils.WordDO;

import lombok.Data;

import java.util.List;

@Data
public class ClassDO {
    private String className;
    private List<FunctionDO> function;
}
