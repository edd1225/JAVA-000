package io.kimmking.rpcfx.api;

import lombok.Data;

/**
 * @PROJECT_NAME: rpcfx
 * @PACKAGE_NAME: io.kimmking.rpcfx.api
 * @NAME: RpcfxException
 * @Author: guanys
 * @DATE: 12/16/20:2:54 PM
 * @Description: TODO
 * @Version: 1.0
 **/
@Data
public class RpcfxException extends RuntimeException {

    private String errCode;

    private Object data;

    public RpcfxException(){

    }

    public RpcfxException(String errMsg){
        super(errMsg);
    }

    public RpcfxException(String errMsg, Throwable cas){
        super(errMsg,cas);
    }

    public RpcfxException(String errCode,String errMsg, Object data){
        super(errMsg);
        this.errCode = errCode;
        this.data = data;
    }
}
