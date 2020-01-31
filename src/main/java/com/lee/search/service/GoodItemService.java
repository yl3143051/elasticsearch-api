package com.lee.search.service;


import com.lee.search.core.Service;
import com.lee.search.model.GoodItem;

import java.util.List;


/**
 * Created by CodeGenerator on 2018/12/21.
 */
public interface GoodItemService extends Service<GoodItem> {
	
	public List<GoodItem> list(String name);
	
	public List<GoodItem> search(String name);
	
	public void index();		//建立索引
	
	public void update(Long id);

}
