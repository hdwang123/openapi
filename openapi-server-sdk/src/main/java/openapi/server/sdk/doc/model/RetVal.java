package openapi.server.sdk.doc.model;

import lombok.Data;

import java.util.List;

/**
 * API方法返回值
 *
 * @author wanghuidong
 * 时间： 2022/6/22 11:14
 */
@Data
public class RetVal {

    /**
     * 返回值类型
     */
    private String retType;


    /**
     * 返回值里的属性
     */
    private List<Property> properties;
}
