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
let contextMenuNodeInfo = {};
let contextMenu = (event, treeId, treeNode) => {
    event.preventDefault();
    let $contextMenu = $('#contextMenu')[0];
    $contextMenu.style.display = 'block';
    $contextMenu.style.left = event.clientX + 'px';
    $contextMenu.style.top = event.clientY + 'px';
    contextMenuNodeInfo.event = event;
    contextMenuNodeInfo.treeId = treeId;
    contextMenuNodeInfo.treeNode = treeNode;
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
    },
    callback: {
        onRightClick: contextMenu
    }
};

class Page {
    //展示zk配置的一些详细信息
    static operateEvent = {
        'click [name="showDetail"]': function (event, value, row, index) {
            console.log(row);
            $.growl.notice({
                message: row.zkServerList

            });
            BootstrapDialog.alert();
        }
    };

    constructor() {
        //页面的中Table表的row 行点击选中后触发的事件
        this.rowClick();
        this.addChildNode();
        this.deleteCurNode();
        this.contextMenuInit();
    }

    contextMenuInit() {
        document.onclick = function () {
            let $contextMenu = $('#contextMenu')[0];
            $contextMenu.style.display = 'none';
        }
    }

    rowClick() {
        $('#zkList').on('click-row.bs.table', function (e, row, $element) {
            let $children = $('#zkList').find("tr");
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

    addChildNode() {
        $('#addChildNode').off('click').on('click', function () {
            BootstrapDialog.show({
                title: '新增节点',
                message: '输入新增路径：<input></input>',
                buttons: [
                    {
                        label: '确定',
                        cssClass: 'btn-primary',
                        hotkey: 13, // Enter.
                        action: function (dialog) {
                            let absolutePath = contextMenuNodeInfo.treeNode.absolutePath;
                            alert(absolutePath)
                        }
                    }, {
                        label: '取消',
                        cssClass: 'btn-default',
                        hotkey: 27, // Esc.
                        action: function (dialog) {
                            dialog.close();
                        }
                    }
                ]

            });
        });
    }

    deleteCurNode() {
        $('#deleteCurNode').off('click').on('click', function () {
            BootstrapDialog.confirm({
                title: '删除节点确认框',
                message: '确认删除节点？',//TODO 在这里添加上要删除节点的完整的路径
                type: BootstrapDialog.TYPE_WARNING,
                closable: true,
                draggable: true,
                btnOKLabel: '确定！',
                btnOKClass: 'btn-warning',
                btnCancelLabel: '取消',
                callback: function (result) {
                    if (result) {
                        //如果点击了确定
                        // alert('yes');
                        $.ajax("/zookeeper/node/delete", {
                            type: "DELETE",
                            data: JSON.stringify({
                                "absolutePath": contextMenuNodeInfo.treeNode.absolutePath,
                                "zkAddress": contextMenuNodeInfo.treeNode.zkAddress
                            }),
                            cache: false,
                            async: true,
                            timeout: 30000,
                            contentType: 'application/json',
                            dataType: 'json',
                            success: function (data, textStatus, jqXHR) {
                                // data 可能是 xmlDoc, jsonObj, html, text, 等等...
                                //界面上给TreeNode增加节点
                                if (data && data.code === "200") {
                                    $.message("删除节点" + contextMenuNodeInfo.treeNode.absolutePath + "成功！")
                                } else {
                                    $.message({
                                        message: "删除节点失败！！！" + data.message,
                                        type: "error"
                                    });
                                }
                            },
                            error: function (XMLHttpRequest, textStatus, errorThrown) {
                                // 通常 textStatus 和 errorThrown 之中
                                // 只有一个会包含信息
                                $.message({
                                    message: "删除节点失败！！！",
                                    type: "error"
                                });
                            }
                        })
                    } else {
                        // alert('no')
                    }
                }

            });
        });
    }


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
        return "<button name = 'showDetail' class='btn btn-primary'>详情</button>";
    },
    events: Page.operateEvent


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