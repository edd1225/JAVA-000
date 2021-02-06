package com.guanys.dubboasset.service;


import com.guanys.dubboapi.service.FreezeAssetService;
import com.guanys.dubboapi.vo.FreezeAssetDTO;
import com.guanys.dubboasset.mapper.FreezeAmountMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @PROJECT_NAME: dubbo-asset
 * @PACKAGE_NAME: com.guanys.dubboasset.service
 * @NAME: FreezeAmountServiceImpl
 * @Author: guanys
 * @DATE: 12/18/20:10:30 AM
 * @Description: TODO
 * @Version: 1.0
 **/
@Service
@DubboService(version = "1.0.0")
public class FreezeAmountServiceImpl implements FreezeAssetService {

    @Autowired
    private FreezeAmountMapper mapper;


    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean updateTempConfirm(FreezeAssetDTO freezeAssetDTO) {
        return mapper.updateTempConfirm(freezeAssetDTO) > 0;
    }

    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public boolean updateTempRollback(FreezeAssetDTO freezeAssetDTO) {
        return mapper.updateTempRollBack(freezeAssetDTO) > 0;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean confirm(FreezeAssetDTO freezeAssetDTO) {
        System.out.println("============dubbo tcc 执行冻结===============");
        mapper.confirm(freezeAssetDTO);
        return Boolean.TRUE;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(FreezeAssetDTO freezeAssetDTO) {
        System.out.println("============ dubbo tcc 执行取消冻结===============");
        mapper.cancel(freezeAssetDTO);
        return Boolean.TRUE;
    }

}