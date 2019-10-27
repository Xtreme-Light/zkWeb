/*对bootstrap的一些中文化封装*/

export class BTable {
    constructor(tableId, localData, defineColumns) {
        this.tableId = tableId;
        this.localData = localData;
        this.defineColumns = defineColumns;
        this.bTableOption = {
            contentType: "application/x-www-form-urlencoded",
            url: this.localData,
            pagination: true,//是否显示分页条
            method: "get",
            ajaxOptions: { //ajax请求的附带参数
                data: {}
            },
            columns: defineColumns ? defineColumns : [],
            striped: true, //是否显示行间隔色
            cache: false, //是否使用缓存,默认为true
            sortable: false, //是否启用排序
            sortOrder: "desc", //排序方式
            pageNumber: 1, //初始化加载第一页，默认第一页
            pageSize: 10, //每页的记录行数（*）
            pageList: [10, 20, 50], //可供选择的每页的行数（*）
            queryParamsType: '', //设置为 'limit' 则会发送符合 RESTFul 格式的参数
            sidePagination: "server", //分页方式：client客户端分页，server服务端分页（*）
            search: false, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端
            strictSearch: true, //设置为 true启用 全匹配搜索，否则为模糊搜索
            minimumCountColumns: 2, //最少允许的列数
            clickToSelect: true, //是否启用点击选中行
            searchOnEnterKey: true, //设置为 true时，按回车触发搜索方法，否则自动触发搜索方法
            icons: {
                refresh: 'glyphicon-repeat',
                toggle: 'glyphicon-list-alt',
                columns: 'glyphicon-list'
            },
            iconSize: 'outline',
            //返回参数必须包含limit, offset, search, sort, order 否则, 需要包含: pageSize, pageNumber, searchText, sortName, sortOrder. 返回false将会终止请求。
            queryParams: function(params) {
                let pa = {
                    pageSize: params.pageSize,
                    pageNumber: params.pageNumber - 1
                };


                return $.extend(pa, this.queryParams);
            },
            // 向后台传递的自定义参数
            responseHandler: function(res) {
                if (res.code === "200") {
                    return {
                        "rows": res.data.zkInfo, // 具体每一个bean的列表
                        "total": res.data.totalElements // 总共有多少条返回数据
                    }
                }else if (res.code === "9527") {
                    window.parent.location.href = 'login.html';
                } else {
                    $.growl.error({
                        message: "查询失败！"
                    });
                }

            },
        };
        this.setLocale();
    }

    toString() {
        return '(' + this.tableId + ',' + this.localData + ',' + this.defineColumns + ')';
    }

    setLocalData(data) {
        this.bTableOption.data = data;
    }
    init() {
        if (this.bTableInstance) {
            $(this.tableId).bootstrapTable('destroy');
        }
        // 工具条配置
        var $toolbar = $(this.tableId).parents(".box").find("[name='toolbar']");
        this.bTableOption.toolbar = $toolbar.length ? $toolbar : undefined; //顶部工具条
        this.bTableOption.showColumns = !!$toolbar.length; //是否显示所有的列
        this.bTableOption.showRefresh = !!$toolbar.length; //是否显示刷新按钮
        this.bTableInstance = $(this.tableId).bootstrapTable(this.bTableOption);
        return this;
    }

    //获取选中行数据
    getSelections() {
        return this.bTableInstance.bootstrapTable("getSelections");
    }
    /**
     * 刷新 bootstrap 表格
     * Refresh the remote server data,
     * you can set {silent: true} to refresh the data silently,
     * and set {url: newUrl} to change the url.
     * To supply query params specific to this request, set {query: {foo: 'bar'}}
     */
    refresh(parms) {
        if (typeof parms != "undefined") {
            this.btInstance.bootstrapTable('refreshOptions', parms);
        } else {
            this.btInstance.bootstrapTable('destroy');
            this.btInstance = $(this.tableId).bootstrapTable(this.bTableOption);
            // this.btInstance.bootstrapTable('refresh');
        }
    }
    //获取数据
    getData() {
        return this.bTableInstance.bootstrapTable("getData");
    }

    /*本地化*/
    setLocale() {
        $.extend($.fn.bootstrapTable.defaults, {
            formatLoadingMessage: function () {
                return '正在努力地加载数据中，请稍候……';
            },
            formatRecordsPerPage: function (pageNumber) {
                return '每页显示 ' + pageNumber + ' 条记录';
            },
            formatShowingRows: function (pageFrom, pageTo, totalRows) {
                return '第 ' + pageFrom + ' - ' + pageTo + ' 条，总 ' + totalRows + ' 条';
            },
            formatSearch: function () {
                return '搜索';
            },
            formatNoMatches: function () {
                return '没有找到匹配的记录';
            },
            formatPaginationSwitch: function () {
                return '隐藏/显示分页';
            },
            formatRefresh: function () {
                return '刷新';
            },
            formatToggle: function () {
                return '切换';
            },
            formatColumns: function () {
                return '列';
            },
            formatExport: function () {
                return '导出数据';
            },
            formatClearFilters: function () {
                return '清空过滤';
            }
        })
    }
}
