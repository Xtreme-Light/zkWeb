import {BTable} from "./btable.js";

import {API} from "./api.js"
// import {ClipboardJS} from "../lib/clipboard/clipboard.min.js"

let api = new API();
api.add("zkServerList", "./data/data1.json", "/zookeeper/server/list");
api.add("addZkServer", "", "/zookeeper/server/add");
api.add("getNodeDetail", "", "/zookeeper/node/detail");
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

    } else if (responseData) {
        $.message({
            type: 'error',
            message: responseData.message
        })
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
    console.log(event.clientX);
    console.log(event.clientY);
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
            BootstrapDialog.show({
                title: 'ZK 详细信息',
                message: function (dialog) {
                    let $template = Handlebars.compile($("#HBS_zk_info").html());
                    let _html = $template({
                        name: row.name,
                        zkAddress: row.zkAddress,
                        authority: row.authority,
                        sessionExpireMs: row.sessionExpireMs,
                        createTime: row.createTime,
                        description: row.description
                    });
                    return _html;
                },
            });
        },
        'click [name="deleteZk"]': function (event, value, row, index) {
            BootstrapDialog.confirm(
                {
                    title: '删除确认',
                    message: '确认从列表中移除ZK: ' + row.zkAddress + "？",
                    type: BootstrapDialog.TYPE_WARNING,
                    closable: true,
                    draggable: true,
                    closeByBackdrop: false,
                    btnOKLabel: '确定！',
                    btnOKHotkey: 13,
                    btnCancelHotkey: 27,// Esc.
                    btnOKClass: 'btn-warning',
                    btnCancelLabel: '取消',
                    callback: function (result) {
                        if (result) {
                            console.log("确定");
                            //TODO Bootstrap 删除行
                            $.ajax("/zookeeper/server/delete", {
                                type: "POST",
                                data: JSON.stringify(row),
                                cache: false,
                                async: true,
                                timeout: 30000,
                                contentType: 'application/json',
                                dataType: 'json',
                                success: function (data, textStatus, jqXHR) {
                                    if (data && data.code === "200") {
                                        console.log("移除" + row);
                                        $('#zkList').bootstrapTable("removeByUniqueId", row.zkAddress);
                                    } else {
                                        $.message({
                                            message: "移除失败！！！" + data.message,
                                            type: "error"
                                        });
                                    }
                                },
                                error: function (XMLHttpRequest, textStatus, errorThrown) {
                                    // 通常 textStatus 和 errorThrown 之中
                                    // 只有一个会包含信息
                                    $.message({
                                        message: "移除失败！！！",
                                        type: "error"
                                    });
                                }
                            });

                        }

                    }
                }
            );
        }

    };

    constructor() {
        //页面的中Table表的row 行点击选中后触发的事件
        this.rowClick();
        this.addChildNode();
        this.deleteCurNode();
        this.contextMenuInit();
        this.copyAbsolutePath();
        this.addNewZK();
        this.getNodeDetail();
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
            let zk_address = row.zkAddress;
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
                message: '输入新增路径：<input id="newNodePath">',
                buttons: [
                    {
                        label: '确定',
                        cssClass: 'btn-primary',
                        hotkey: 13, // Enter.
                        action: function (dialog) {
                            let absolutePath = contextMenuNodeInfo.treeNode.absolutePath;
                            let innerText = $('#newNodePath').val();
                            let newPath = absolutePath + "/" + innerText;
                            $.ajax("/zookeeper/node/create", {
                                type: "PUT",
                                data: JSON.stringify({
                                    "absolutePath": newPath,
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
                                        $.message("创建节点" + newPath + "成功！");
                                        console.log("========================");
                                        let treeObj = $.fn.zTree.getZTreeObj('zTree');
                                        treeObj.addNodes(contextMenuNodeInfo.treeNode, -1, {
                                            "absolutePath": newPath,
                                            "name": innerText,
                                            "parentAbsolutePath": contextMenuNodeInfo.treeNode.absolutePath,
                                            "zkAddress": contextMenuNodeInfo.treeNode.zkAddress,
                                            "isParent": true
                                        }, false);
                                        dialog.close();
                                    } else {
                                        $.message({
                                            message: "创建节点失败！！！" + data.message,
                                            type: "error"
                                        });
                                    }
                                },
                                error: function (XMLHttpRequest, textStatus, errorThrown) {
                                    // 通常 textStatus 和 errorThrown 之中
                                    // 只有一个会包含信息
                                    $.message({
                                        message: "创建节点失败！！！",
                                        type: "error"
                                    });
                                }
                            })
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
                message: '确认删除节点: ' + contextMenuNodeInfo.treeNode.absolutePath + "？",
                type: BootstrapDialog.TYPE_WARNING,
                closable: true,
                draggable: true,
                btnOKLabel: '确定！',
                btnOKHotkey: 13,
                btnCancelHotkey: 27,// Esc.
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
                                    $.message("删除节点" + contextMenuNodeInfo.treeNode.absolutePath + "成功！");
                                    let treeObj = $.fn.zTree.getZTreeObj('zTree');
                                    treeObj.removeNode(contextMenuNodeInfo.treeNode);
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

    copyAbsolutePath() {
        $('#copyAbsolutePath').off('click').on('click', function () {
            let clipboardJS = new ClipboardJS('#copyAbsolutePath', {
                text: function (trigger) {
                    return contextMenuNodeInfo.treeNode.absolutePath;
                }
            });
            clipboardJS.on('success', function (e) {
                console.info('Action:', e.action);
                console.info('Text:', e.text);
                console.info('Trigger:', e.trigger);
                e.clearSelection();
            });
            clipboardJS.on('error', function (e) {
                console.error('Action:', e.action);
                console.error('Trigger:', e.trigger);
                $.message(
                    {
                        message: "复制失败！！！！！",
                        type: "error"
                    }
                );
            });

        })
    }

    getNodeDetail() {
        $('#getNodeDetail').off('click').on('click', function () {
            $.ajax(api.get('getNodeDetail'), {
                type: 'POST',
                data: JSON.stringify({
                    "absolutePath": contextMenuNodeInfo.treeNode.absolutePath,
                    "zkAddress": contextMenuNodeInfo.treeNode.zkAddress
                }),
                contentType: 'application/json',
                dataType: 'json',
                cache: false,
                async: true,
                timeout: 30000,
                success: function (data, textStatus, jqXHR) {
                    if (data && data.code === "200") {
                        BootstrapDialog.show({
                            title: '节点详细信息',
                            closable: true,
                            draggable: true,
                            message: function (dialog) {
                                let $template = Handlebars.compile($("#HBS_ZK_node_info").html());
                                let _html = $template(data.data);
                                return _html;
                            }
                        })
                    } else if (data && data.code === "303") {
                        $.message({
                            message: "获取ZK节点信息失败！！！" + data.message,
                            type: "error"
                        });
                    } else {
                        $.message({
                            message: "获取ZK节点信息失败！！！",
                            type: "error"
                        });
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    // 通常 textStatus 和 errorThrown 之中
                    // 只有一个会包含信息
                    $.message({
                        message: "获取ZK节点信息失败！！！",
                        type: "error"
                    });
                }
            });

        });
    }

    addNewZK() {
        $('#addZK').off('click').on('click', function () {
            BootstrapDialog.show(
                {
                    title: '新增ZK集群',
                    type: BootstrapDialog.TYPE_WARNING,
                    closable: true,
                    draggable: true,
                    //不允许通过点击背景，关闭弹窗
                    closeByBackdrop: false,
                    buttons: [
                        {
                            label: '确定',
                            cssClass: 'btn-primary',
                            hotkey: 13, // Enter.
                            action: function (dialog) {
                                let _cmd = {
                                    name: document.getElementById("name").value,
                                    authority: document.getElementById("authority").value,
                                    description: document.getElementById("description").value,
                                    zkAddress: document.getElementById("zkAddress").value,
                                    sessionExpireMs: document.getElementById("sessionExpireMs").value === undefined ? 3000 : document.getElementById("sessionExpireMs").value,
                                    createTime: new Date()
                                };
                                $.ajax(api.get('addZkServer'), {
                                    type: "POST",
                                    data: _cmd,
                                    cache: false,
                                    async: true,
                                    timeout: 30000,
                                    success: function (data, textStatus, jqXHR) {
                                        // data 可能是 xmlDoc, jsonObj, html, text, 等等...
                                        //界面上给TreeNode增加节点
                                        if (data && data.code === "200") {
                                            $('#zkList').bootstrapTable("append", _cmd);
                                            $.message("新增ZK" + _cmd.zkAddress + "成功！");
                                            console.log("========================");
                                            dialog.close();
                                        } else {
                                            $.message({
                                                message: "新增ZK失败！！！" + data.message,
                                                type: "error"
                                            });
                                        }
                                    },
                                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                                        // 通常 textStatus 和 errorThrown 之中
                                        // 只有一个会包含信息
                                        $.message({
                                            message: "新增ZK失败！！！",
                                            type: "error"
                                        });
                                    }
                                })
                            }
                        }, {
                            label: '取消',
                            cssClass: 'btn-default',
                            hotkey: 27, // Esc.
                            action: function (dialog) {
                                dialog.close();
                            }
                        }
                    ],
                    message: function (dialog) {
                        // let $template = Handlebars.compile($("#HBS_add_zk").html());
                        // let _html = $template({});
                        return '<form role="form">' +
                            '        <div class="form-group">' +
                            '            <label for="name">标识名称：</label>' +
                            '            <input type="text" class="form-control" name="name" id="name" placeholder="name">' +
                            '        </div>' +
                            '        <div class="form-group">' +
                            '            <label for="zkAddress">ZK地址：</label>' +
                            '            <input type="text" class="form-control" name="zkAddress" id="zkAddress" placeholder="zkAddress">' +
                            '        </div>' +
                            '        <div class="form-group">' +
                            '            <label for="sessionExpireMs">超时时间：</label>' +
                            '            <input type="text" class="form-control" name="sessionExpireMs" id="sessionExpireMs" placeholder="sessionExpireMs">' +
                            '        </div>' +
                            '        <div class="form-group">' +
                            '            <label for="authority">认证信息（digest模式）：</label>' +
                            '            <input type="text" class="form-control" id="authority" name="authority" placeholder="authority">' +
                            '        </div>' +
                            '        <div class="form-group">' +
                            '            <label for="description">描述：</label>' +
                            '            <input type="text" class="form-control" name="description" id="description" placeholder="description">' +
                            '        </div>' +
                            '' +
                            '    </form>';
                    },
                }
            );

        });
    }

}


let page = new Page();

let columns = [{
    field: "name",
    title: "zk名称",
    width: "150px"
}, {
    field: "zkAddress",
    title: "zk集群地址"
}, {
    field: "sessionExpireMs",
    title: "超时时间"
}, {
    title: "配置信息",
    width: "150px",
    formatter: function (value, row, index, field) {
        return "<button name = 'showDetail' class='btn btn-primary'>详情</button>" +
            "<button name = 'deleteZk' class='btn btn-warning pull-right'>删除ZK</button>";
    },
    events: Page.operateEvent


}];
let bTable = new BTable("#zkList", api.get("zkServerList"), columns);
bTable.setUniqueId('zkAddress');
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