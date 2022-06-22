package openapi.example.server.model;

import lombok.Data;

import java.util.List;

/**
 * 用户信息
 *
 * @author wanghuidong
 */
@Data
public class User {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private Address address;
    private List<Role> role;

    public User() {

    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
