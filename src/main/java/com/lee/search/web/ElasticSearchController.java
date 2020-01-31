package com.lee.search.web;

import com.lee.search.core.Result;
import com.lee.search.core.ResultGenerator;
import com.lee.search.service.ElasticSearchService;
import com.lee.search.service.ReadBooksService;
import com.lee.search.vo.BookSearchParam;
import com.lee.search.vo.BookSearchResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/es")
@Slf4j
public class ElasticSearchController {

	@Autowired
	ReadBooksService booksService;
	@Autowired
	ElasticSearchService esSeachService;

	@GetMapping("/creatIndex")
	public Result creatIndex() {
		booksService.creatIndex();		//做一次全量数据插入 做一次就行了。
		return ResultGenerator.genSuccessResult();
	}

	@GetMapping("/search")
	public Result search(@RequestParam(required = false) String keyWord) {

		BookSearchParam bookSearchParam = new BookSearchParam();
		bookSearchParam.setDesc("童话故事");
		List<BookSearchResultVo> books = esSeachService.queryDocumentByParam("test-es", "test-type", bookSearchParam);
		return ResultGenerator.genSuccessResult(books);
	}
}
