package openapi.example.server.openapi;

import openapi.example.server.model.TreeNode;
import openapi.sdk.common.annotation.OpenApiDoc;
import openapi.server.sdk.annotation.OpenApi;
import openapi.server.sdk.annotation.OpenApiMethod;

/**
 * @author wanghuidong
 * 时间： 2022/8/20 0:43
 */
@OpenApi("treeApi")
public class TreeApi {

    @OpenApiDoc(cnName = "查询树", retCnName = "树")
    @OpenApiMethod("getTree")
    public TreeNode getTree() {
        TreeNode treeNode = this.buildTree();
        return treeNode;
    }

    private TreeNode buildTree() {
        TreeNode rootNode = new TreeNode();
        rootNode.setId(1L);
        rootNode.setLevel(0);

        TreeNode treeNode21 = new TreeNode();
        treeNode21.setId(2L);
        treeNode21.setLevel(1);
        rootNode.getChildren().add(treeNode21);

        TreeNode treeNode211 = new TreeNode();
        treeNode211.setId(4L);
        treeNode211.setLevel(2);
        treeNode21.getChildren().add(treeNode211);

        TreeNode treeNode22 = new TreeNode();
        treeNode22.setId(3L);
        treeNode22.setLevel(1);
        rootNode.getChildren().add(treeNode22);
        return rootNode;
    }
}
