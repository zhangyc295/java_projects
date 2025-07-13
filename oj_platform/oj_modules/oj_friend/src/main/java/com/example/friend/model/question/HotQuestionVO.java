package com.example.friend.model.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotQuestionVO {
    @TableId(type = IdType.ASSIGN_ID)

    private Long questionId;

    private String title;
}
