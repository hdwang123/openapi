package openapi.example.server.model;

import lombok.Data;
import openapi.sdk.common.annotation.OpenApiDoc;

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

    @OpenApiDoc(cnName = "性别", describe = "枚举类型（值：MALE,FEMALE）")
    private Gender gender;

    @OpenApiDoc(cnName = "手机号")
    private String phone;

    private String email;
    private Address address;
    private List<Role> roleList;


//    @OpenApiDoc(cnName = "测试DOC功能")
//    private Role[] roleArray;
//
//    @OpenApiDoc(cnName = "测试DOC功能")
//    private List<List<Role>> roleLists;
//
//    @OpenApiDoc(cnName = "测试DOC功能")
//    private Role[][] roleArrays;

//    @OpenApiDoc(cnName = "测试DOC功能")
//    private List<Role>[] roleListArray;

    @OpenApiDoc(ignore = true)
    private String password;

    public User() {

    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
