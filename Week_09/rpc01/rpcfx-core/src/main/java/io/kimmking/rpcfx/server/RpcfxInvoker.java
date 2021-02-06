package io.kimmking.rpcfx.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.kimmking.rpcfx.api.RpcfxException;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcfxInvoker {

    private RpcfxResolver resolver;

    public RpcfxInvoker(RpcfxResolver resolver){
        this.resolver = resolver;
    }

    public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        // 作业1：改成泛型和反射
        Object service = resolver.resolve(serviceClass);

        try {
            Method method = service.getClass().getMethod(request.getMethod(),request.getParamTypes());
            for(int i=0;i<request.getParamTypes().length;i++){
                //类型不匹配，则进行类型转换
                if (!request.getParamTypes()[i].isInstance(request.getParams()[i])) {
                    Constructor constructor = request.getParamTypes()[0].getConstructor(String.class);
                    request.getParams()[i] = constructor.newInstance(request.getParams()[i].toString());
                }
            }
            Object result = method.invoke(service, request.getParams());
            // 两次json序列化能否合并成一个
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch ( IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {

            // 3.Xstream

            // 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
            e.printStackTrace();
            response.setException(new RpcfxException("服务端invoke失败!",e));
            response.setStatus(false);
            return response;
        }
    }

//    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
//        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
//    }

}
