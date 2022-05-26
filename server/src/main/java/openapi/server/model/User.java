package openapi.server.model;

import lombok.Data;

/**
 * 用户信息
 * @author wanghuidong
 * @date 2022/5/26 19:44
 */
@Data
public class User {

    private Long id;
    private String name;

    public User(){

    }

    public User(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
