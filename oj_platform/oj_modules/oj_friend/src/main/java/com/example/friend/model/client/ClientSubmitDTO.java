package com.example.friend.model.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientSubmitDTO {

    private Long questionId;
    private Long contestId;

    private String submitCode;
    private Integer programLanguage;  // 0 JAVA

}
