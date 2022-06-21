package openapi.server.sdk.doc.model;

import lombok.Data;

/**
 * API方法参数信息
 *
 * @author wanghuidong
 * 时间： 2022/6/21 21:42
 */
@Data
public class Param {

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数名
     */
    private String name;

    /**
     * 参数中文名
     */
    private String cnName;

    /**
     * 参数描述
     */
    private String describe;
}
