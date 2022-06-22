package openapi.example.server.model;

import lombok.Data;

/**
 * 地址
 *
 * @author wanghuidong
 * 时间： 2022/6/22 22:31
 */
@Data
public class Address {

    private String country;

    private String province;

    private String city;

    private String detailAddress;
}
