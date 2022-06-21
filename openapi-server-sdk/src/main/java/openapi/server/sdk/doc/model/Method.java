package openapi.server.sdk.doc.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * API方法信息
 *
 * @author wanghuidong
 * 时间： 2022/6/21 21:32
 */
@Data
public class Method {

    /**
     * 方法名
     */
    private String name;

    /**
     * 方法中文名
     */
    private String cnName;

    /**
     * 方法描述
     */
    private String describe;

    /**
     * 方法参数
     */
    private List<Param> paramList = new ArrayList<>();

    /**
     * 返回值类型
     */
    private String retType;
}
