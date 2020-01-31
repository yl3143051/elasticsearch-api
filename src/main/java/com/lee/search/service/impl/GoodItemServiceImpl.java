package com.lee.search.service.impl;

import com.github.pagehelper.PageHelper;
import com.lee.search.core.AbstractService;
import com.lee.search.dao.GoodItemMapper;
import com.lee.search.model.GoodItem;
import com.lee.search.service.GoodItemService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeGenerator on 2018/12/21.
 */
@Service
@Transactional
//@Slf4j
public class GoodItemServiceImpl extends AbstractService<GoodItem> implements GoodItemService {

	private Logger log = LoggerFactory.getLogger(GoodItemServiceImpl.class);

	// 写索引实例
	private IndexWriter writer;

	private String indexDir = "d:\\lucene\\goods_2019";

	@Resource
	private GoodItemMapper bbgDtpMomItemMapper;

	@Override
	public List<GoodItem> list(String name) {
		Condition condition = new Condition(GoodItem.class);
		Criteria criteria = condition.createCriteria();
		criteria.andLike("itemDesc", "%" + name + "%");
		List<GoodItem> goodItems = super.findByCondition(condition);
		return goodItems;
	}

	@Override
	public List<GoodItem> search(String name) {
		List<GoodItem> goods = new ArrayList<>();
		try {
			Directory dir = FSDirectory.open(Paths.get(indexDir));
			// 创建索引读取器
			IndexReader reader = DirectoryReader.open(dir);
			// 创建索引查询器
			IndexSearcher is = new IndexSearcher(reader);
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			// 标准分词器
			Analyzer analyzer = new IKAnalyzer5x();	//建立的时候建两套  但是搜索的时候只有一套
			QueryParser parser = new QueryParser("itemDesc", analyzer);
			parser.setDefaultOperator(Operator.AND);		//AND 或者OR
			Query query = parser.parse(name);
			builder.add(query, Occur.MUST);
			
			long start = System.currentTimeMillis();
			System.out.println(query.toString());
			TopDocs hits = is.search(builder.build(), 10);
			long end = System.currentTimeMillis();
			System.out.println(
					"匹配 " + builder.toString() + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				GoodItem good = new GoodItem();
				good.setItemDesc(doc.get("itemDesc"));
				good.setId(Long.valueOf(doc.get("id")));
				goods.add(good);
			}
			reader.close();
		} catch (Exception e) {
			log.error("索引查询失败", e);
		}
		return goods;
	}

	@Override
	public void index() {
		try {
			Directory dir = FSDirectory.open(Paths.get(indexDir));
			// 标准分词器
			Analyzer analyzer = new IKAnalyzer5x();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			writer = new IndexWriter(dir, iwc);
			int total = bbgDtpMomItemMapper.selectCount(null);
			int size = 100;
			int page = total / size;
			if(page > 100) page = 100;
			if (total % size != 0)
				page++;
			log.info("goods数据库中记录为{}，按size={}，page={}", total, size, page);
			for (int i = 1; i <= page; i++) {
				log.info("生成page={}的索引", page);
				PageHelper.startPage(i, size);
				List<GoodItem> goods = bbgDtpMomItemMapper.selectAll();
				List<Document> documents = getDocument(goods);
				writer.addDocuments(documents);		//写入索引
				PageHelper.clearPage();
			}
			log.info("索引生成成功");
			writer.close();
		} catch (Exception e) {
			log.error("索引构建失败", e);
		}
	}
	
	private List<Document> getDocument(List<GoodItem> goods) throws Exception {
		List<Document> docs = new ArrayList<>();
		for (GoodItem good : goods) {
			Document doc = new Document();
			doc.add(new LongField("id", good.getId(), Field.Store.YES));		//Long 是不支持分词的
			doc.add(new TextField("itemDesc", good.getItemDesc(), Field.Store.YES));			//才支持分词 yes表示字段是存储的
			if(good.getMfgRecRetail() != null)
			doc.add(new DoubleField("price", good.getMfgRecRetail(), Field.Store.YES));
			docs.add(doc);
		}
		return docs;
	}
	
	void update(){
		
	}

	@Override
	public void update(Long id) {

	}

}
