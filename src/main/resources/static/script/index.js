import {BTable} from "./btable.js";

let columns = [{
    field: "zkName",
    title: "zk名称",
    width: "150px"
}, {
    field: "zkServerList",
    title: "zk集群地址"
},{
    field: "timeout",
    title: "超时时间"
}];
let bTable = new BTable("#zkList", "./data/data1.json", columns);
bTable.init();


// 对与zTree开始配置
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
        autoParam: ["absolutePath"],
        contentType: "application/json",
        dataType: "text",
        //异步加载
        enable: true,
        type: "get",
        url: "getTargetNodeChildren"
    }
};

/*let zNodes =[
    {
        name:"/",
        open:false,
        absolutePath: "/",
        parentPathName: "0",
    }
];*/
let zNodes = [
    {"absolutePath": "/", "name": "/", "parentAbsolutePath": "/"},
    {"absolutePath": "/root", "name": "/root", "parentAbsolutePath": "/"},
    {"absolutePath": "/root1", "name": "/root1", "parentAbsolutePath": "/"},
    {"absolutePath": "/root2", "name": "/root2", "parentAbsolutePath": "/"},
    {"absolutePath": "/root1/root11", "name": "/root11", "parentAbsolutePath": "/root1"},
    {"absolutePath": "/root1/root12", "name": "/root12", "parentAbsolutePath": "/root1"},
    {"absolutePath": "/root1/root13", "name": "/root13", "parentAbsolutePath": "/root1"},
    {"absolutePath": "/root2/root21", "name": "/root21", "parentAbsolutePath": "/root2"},
    {"absolutePath": "/root2/root22", "name": "/root22", "parentAbsolutePath": "/root2"},
    {"absolutePath": "/root2/root23", "name": "/root23", "parentAbsolutePath": "/root2"},



];

$(document).ready(function(){
    $.fn.zTree.init($(".zTree"), setting, zNodes);
});