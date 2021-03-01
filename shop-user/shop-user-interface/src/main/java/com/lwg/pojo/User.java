package com.lwg.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 2, max = 30, message = "用户名只能在2~30位之间")
    private String username;// 用户名

    @JsonIgnore   //json序列化时，就不会把password和salt返回。
    @Length(min = 4, max = 30, message = "密码只能在4~30位之间")
    private String password;// 密码

    private Date created;// 创建时间

    @JsonIgnore
    private String salt;// 密码的盐值
}