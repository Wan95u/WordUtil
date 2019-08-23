package com.cmcc.cmii.ambulance.common.utils.WordDO;

import lombok.Data;

import java.util.List;

@Data
public class FunctionDO {
    private String functionName;
    private List<ParamDO> param;
    private String returnType;
}
