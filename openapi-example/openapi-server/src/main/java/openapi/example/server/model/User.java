package openapi.example.server.model;

import lombok.Data;
import openapi.server.sdk.doc.annotation.OpenApiDoc;

import java.util.List;

/**
 * 用户信息
 *
 * @author wanghuidong
 */
@OpenApiDoc(cnName = "用户", describe = "用户信息")
@Data
public class User {

    @OpenApiDoc(cnName = "用户ID", describe = "用户表主键ID")
    private Long id;

    @OpenApiDoc(cnName = "用户名称")
    private String name;

    @OpenApiDoc(cnName = "手机号")
    private String phone;
    private String email;
    private Address address;
    private List<Role> role;


    @OpenApiDoc(ignore = true)
    private String password;

    public User() {

    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
