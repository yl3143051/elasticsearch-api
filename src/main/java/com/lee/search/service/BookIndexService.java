package com.lee.search.service;


import com.lee.search.model.ReadBooks;

import java.util.List;

public interface BookIndexService {

	Integer creatIndexLucene();
	
	List<ReadBooks> search(String keywords, String bookName, String author);
}
