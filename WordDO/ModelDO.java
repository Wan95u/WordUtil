package com.cmcc.cmii.ambulance.common.utils.WordDO;

import lombok.Data;

import java.util.List;

@Data
public class ModelDO {
    private String modelName;
    private List<ClassDO> classes;
}
