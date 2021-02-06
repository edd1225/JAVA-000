package com.guanys.dubboapi.service;

import com.guanys.dubboapi.vo.FreezeAssetDTO;
import org.dromara.hmily.annotation.Hmily;

public interface FreezeAssetService {

    @Hmily
    boolean updateTempConfirm(FreezeAssetDTO freezeAssetDTO);

    @Hmily
    boolean updateTempRollback(FreezeAssetDTO freezeAssetDTO);
}
