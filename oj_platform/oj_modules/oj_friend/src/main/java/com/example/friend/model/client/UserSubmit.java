package com.example.friend.model.client;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("oj_answer")
public class UserSubmit extends TimeBase {
    
	@TableId( type = IdType.ASSIGN_ID)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long submitId;

	private Long userId; //唯一确定用户

	private Long questionId;  //唯一确定题目

	private Long contestId;   //唯一确定竞赛      null表示刷题

	private Integer programLanguage;

	private String submitCode;

	private Integer pass;

	private Integer score;

	private String errorMessage;

	private String caseJudgeResult;
}