package com.example.system.model.user;

import com.example.common.entity.model.PageBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShowDTO extends PageBase {

    private Long userId;
    private String userName;
}
