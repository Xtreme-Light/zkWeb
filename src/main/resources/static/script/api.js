/**
 * 对调用进行一些封装
 */
export class API {
    constructor() {
        this.apis = [{}, {}];

    }

    /**
     * 注册接口的方法，添加api
     * @param aliasName 别名，短名，要求唯一
     * @param localPath 本地数据路径
     * @param apiPath 请求的api路径
     */
    add(aliasName, localPath, apiPath) {
        this.apis[0][aliasName] = localPath;
        this.apis[1][aliasName] = apiPath;
    }

    get(aliasName) {
        return this.apis[window.location.port === "9090" ? 1 : 0][aliasName];
    }
}