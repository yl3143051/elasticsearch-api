package com.lee.search.web;

import com.lee.search.core.Result;
import com.lee.search.core.ResultGenerator;
import com.lee.search.model.ReadBooks;
import com.lee.search.service.BookIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class LuceneSearchController {
	
	@Autowired
	private BookIndexService bookIndexService;
	
	@GetMapping("/book")
	public Result getBook(@RequestParam (required = true)String keywords, @RequestParam (required = false)String bookName, @RequestParam (required = false)String author) {
		List<ReadBooks> books = bookIndexService.search(keywords, bookName, author);
		return ResultGenerator.genSuccessResult(books);
	}
	
	@GetMapping("/creat/book")
	public Result creatBook() {
		bookIndexService.creatIndexLucene();
		return ResultGenerator.genSuccessResult();
	}
	
}
