import {BTable} from "./btable.js";
// 对与zTree开始配置
let nodeDataFilter = (treeId, parentNode, responseData) => {
    if (responseData && responseData.code === "200") {
        let children = responseData.data;
        let nodes = [];
        for (let child of children) {
            let node = {};
            node.zkAddress = parentNode.zkAddress;
            node.absolutePath = (parentNode.absolutePath === "/") ? "/" + child : parentNode.absolutePath + "/" + child;
            node.name = child;
            node.isParent = true;
            nodes.push(node);
        }
        return nodes;

    }
    let data = responseData.data;
};
let setting = {
    data: {
        simpleData: {
            /**
             * true / false 分别表示 使用 / 不使用 简单数据模式

             如果设置为 true，请务必设置 setting.data.simpleData 内的其他参数: idKey / pIdKey / rootPId，并且让数据满足父子关系。
             */
            enable: true,
            idKey: "absolutePath",
            pIdKey: "parentAbsolutePath",
            rootPId: "/"
        },
        key: {
            //当鼠标防止到节点上时显示的提示信息，这里设置成，显示该节点完整路径
            title: "absolutePath"
        }
    },
    view: {
        //是否可以通过ctrl键选取多个节点，目前该功能没有值得体现的地方，关掉，默认是true
        selectedMulti: false,
        //展示节点的图标
        showIcon: true,
        //展示节点之间的连线
        showLine: true,
        //需要配合setting.data.key.title同时使用，默认true，默认的情况下，提示的Title会显示name，通过修改setting.data.key.title来显示指定的属性
        showTitle: true,
    },
    async: {
        //发送请求的时候，附加的参数，也可以设置别名，这里咩有设置
        autoParam: ["absolutePath", "zkAddress"],
        contentType: "application/json",
        dataType: "text",
        //异步加载
        enable: true,
        type: "post",
        url: "/zookeeper/node/getTargetNodeChildren",
        dataFilter: nodeDataFilter
    }
};

class Page {
    constructor() {
        //页面的中Table表的row 行点击选中后触发的事件
        this.rowClick();
    }

    rowClick() {
        $('#zkList').on('click-row.bs.table', function (e, row, $element) {
            let $children = $('#zkList ').find("tr");
            for (let i = 0; i < $children.length; i++) {
                $children[i].bgColor = '#FFFFFF';
            }
            $element[0].bgColor = '#FFEE88';
            $('.text-loader-content').remove();
            $element[0].bgColor = '#FFEE88';
            let zk_address = row.zkServerList;
            $.fn.zTree.destroy(".zTree");
            let newZnodeTree = [
                {"absolutePath": "/", "name": "/", "parentAbsolutePath": "/", "zkAddress": zk_address, "isParent": true}
            ];
            $.fn.zTree.init($('.zTree'), setting, newZnodeTree);
        });
    };

    /**
     * 请求节点后返回的数据过滤
     * @param treeId
     * @param parentNode
     * @param responseData
     */

}


let page = new Page();

let columns = [{
    field: "zkName",
    title: "zk名称",
    width: "150px"
}, {
    field: "zkServerList",
    title: "zk集群地址"
}, {
    field: "timeout",
    title: "超时时间"
}, {
    title: "配置信息",
    formatter: function (value, row, index, field) {
        return "";
    }
}];
let bTable = new BTable("#zkList", "./data/data1.json", columns);
bTable.init();


let zNodes = [
    {
        "absolutePath": "/",
        "name": "/",
        "parentAbsolutePath": "/",
        "zkAddress": "20.26.25.44:2183,20.26.25.45:2183,20.26.25.46:2183",
        "isParent": true
    },


];


$(document).ready(function () {
    $.fn.zTree.init($(".zTree"), setting, zNodes);
});