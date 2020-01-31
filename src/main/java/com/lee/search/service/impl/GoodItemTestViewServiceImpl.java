package com.lee.search.service.impl;

import com.lee.search.core.AbstractService;
import com.lee.search.dao.GoodItemTestViewMapper;
import com.lee.search.model.GoodItemTestView;
import com.lee.search.service.GoodItemTestViewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2019/01/04.
 */
@Service
@Transactional
public class GoodItemTestViewServiceImpl extends AbstractService<GoodItemTestView> implements GoodItemTestViewService {
    @Resource
    private GoodItemTestViewMapper bbgDtpMomItemMapper;

}
