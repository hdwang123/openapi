package openapi.example.server.model;

import lombok.Data;
import openapi.sdk.common.annotation.OpenApiDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * 树结点
 *
 * @author wanghuidong
 * 时间： 2022/8/20 0:46
 */
@Data
@OpenApiDoc(cnName = "树结点")
public class TreeNode {

    @OpenApiDoc(cnName = "结点id")
    private Long id;

    @OpenApiDoc(cnName = "树层级")
    private int level;

    @OpenApiDoc(cnName = "结点名称")
    private String name;

    @OpenApiDoc(cnName = "结点值")
    private String value;

    @OpenApiDoc(cnName = "孩子结点")
    private List<TreeNode> children = new ArrayList<>();
}
