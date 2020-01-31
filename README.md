# elasticsearch-api
ES查询DEMO

# es和lucene是什么关系？
### es基于lucene。只是在这基础上做出来一个分布式索引库，提供了集群的概念，还提供了api查询接口。es和solr是一样额原理。

### lucene为何可以做大数据文本检索
### 1000w商品数据，要你做一个搜索功能。分词加like不行（适用10W级）
### 这个不必用es。维护成本高，适用千万级以上，稳定性高，查询量大，分布式查询。
### 最佳方案：分词+lucene（单机版）不适应高并发

# es和solr对比
## Solr            适合全文检索，但是实时性不高
## ElasticSearch   实时性更高的索引引擎
### 1、当单纯的对已有数据进行搜索时，Solr更快。
### 2、当实时建立索引时, Solr会产生io阻塞，查询性能较差, Elasticsearch具有明显的优势
### 3、随着数据量的增加，Solr的搜索效率会变得更低，而Elasticsearch却没有明显的变化
