package com.lee.search.service.impl;

import com.github.pagehelper.PageHelper;
import com.lee.search.dao.ReadBooksMapper;
import com.lee.search.model.ReadBooks;
import com.lee.search.service.BookIndexService;
import com.lee.search.service.ReadBooksService;
import com.lee.search.service.RedisService;
import com.wltea.analyzer.core.IKAnalyzer5x;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookIndexServiceImpl implements BookIndexService {

	@Autowired
	private RedisService redisService;
	@Autowired
	private ReadBooksService booksService;
	@Autowired
	private ReadBooksMapper booksMapper;

	// 写索引实例
	private IndexWriter writer;

	private String indexDir = "d:\\lucene\\book";

	@Override
	public Integer creatIndexLucene() {
		try {
			Directory dir = FSDirectory.open(Paths.get(indexDir));
			// 标准分词器
			Analyzer analyzer = new IKAnalyzer5x();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			writer = new IndexWriter(dir, iwc);
			int total = booksMapper.selectCount(null);
			int size = 100;
			int page = total / size;
			if (total % size != 0)
				page++;
			log.info("book数据库中记录为{}，按size={}，page={}", total, size, page);
			for (int i = 1; i <= page; i++) {
				log.info("生成page={}的索引", page);
				PageHelper.startPage(i, size);
				List<ReadBooks> books = booksService.findAll();
				List<Document> documents = getDocument(books);
				writer.addDocuments(documents);
				PageHelper.clearPage();
			}
			log.info("索引生成成功");
			writer.close();
			return total;
		} catch (Exception e) {
			log.error("索引构建失败", e);
		}
		return 0;
	}

	private List<Document> getDocument(List<ReadBooks> books) throws Exception {
		List<Document> docs = new ArrayList<>();
		for (ReadBooks book : books) {
			Document doc = new Document();
			doc.add(new IntField("id", book.getId(), Field.Store.YES));
			doc.add(new TextField("bookName", book.getName(), Field.Store.YES));
			doc.add(new TextField("contents", book.getDiscription(), Field.Store.YES));
			doc.add(new StringField("author", book.getAuthor(), Field.Store.YES));
			doc.add(new IntField("type", book.getType(), Field.Store.YES));
			doc.add(new IntField("level", book.getLevel(), Field.Store.YES));
			doc.add(new DoubleField("price", book.getPrice().doubleValue(), Field.Store.YES));
			docs.add(doc);
		}
		return docs;
	}

	@Override
	public List<ReadBooks> search(String keywords, String bookName, String author) {
		List<ReadBooks> books = new ArrayList<>();
		try {
			Directory dir = FSDirectory.open(Paths.get(indexDir));
			// 创建索引读取器
			IndexReader reader = DirectoryReader.open(dir);
			// 创建索引查询器
			IndexSearcher is = new IndexSearcher(reader);
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			// 标准分词器
			Analyzer analyzer = new IKAnalyzer5x();
			QueryParser parser = new QueryParser("contents", analyzer);
			parser.setDefaultOperator(Operator.AND);
			Query query = parser.parse(keywords);
			builder.add(query, Occur.MUST);
			
			long start = System.currentTimeMillis();
			System.out.println(query.toString());
			TopDocs hits = is.search(builder.build(), 10);
			long end = System.currentTimeMillis();
			System.out.println(
					"匹配 " + builder.toString() + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				ReadBooks book = new ReadBooks();
				book.setAuthor(doc.get("author"));
				book.setId(Integer.valueOf(doc.get("id")));
				book.setName(doc.get("bookName"));
				book.setDiscription(doc.get("contents"));
				books.add(book);
			}
			reader.close();
		} catch (Exception e) {
			log.error("索引查询失败", e);
		}
		return books;
	}

}
