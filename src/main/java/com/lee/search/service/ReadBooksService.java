package com.lee.search.service;


import com.lee.search.core.Service;
import com.lee.search.model.ReadBooks;

import java.util.List;


/**
 * Created by CodeGenerator on 2018/12/16.
 */
public interface ReadBooksService extends Service<ReadBooks> {

	List<ReadBooks> search(String keyWords);
	
	public Boolean creatIndex();
}
