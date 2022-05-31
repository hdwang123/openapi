package openapi.example.client.model;

import lombok.Data;

/**
 * 用户信息
 * @author wanghuidong
 */
@Data
public class User {

    private Long id;
    private String name;
    private String phone;
    private String email;
}
