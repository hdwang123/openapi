package openapi.example.server.model;

import lombok.Data;

/**
 * 角色
 * @author wanghuidong
 * 时间： 2022/6/2 0:13
 */
@Data
public class Role {

    private Long id;
    private String name;

    public Role() {

    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
