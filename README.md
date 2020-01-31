# elasticsearch-api
ES查询DEMO

# es和lucene是什么关系？
es基于lucene。只是在这基础上做出来一个分布式索引库，提供了集群的概念，还提供了api查询接口。es和solr是一样额原理。

lucene为何可以做大数据文本检索
1000w商品数据，要你做一个搜索功能。分词加like不行（适用10W级）
这个不必用es。维护成本高，适用千万级以上，稳定性高，查询量大，分布式查询。
最佳方案：分词+lucene（单机版）不适应高并发

# es和solr对比
## Solr            适合全文检索，但是实时性不高
## ElasticSearch   实时性更高的索引引擎
1、当单纯的对已有数据进行搜索时，Solr更快。
2、当实时建立索引时, Solr会产生io阻塞，查询性能较差, Elasticsearch具有明显的优势
3、随着数据量的增加，Solr的搜索效率会变得更低，而Elasticsearch却没有明显的变化

# windows搭建ELK (搭建完了之后凭感觉写的)
1.下载elasticsearch，然后修改elasticsearch.yml配置文件，如果是集群那就要修改相关参数，具体怎么修改还是度娘吧！
2.安装完后先启动看看能不能正常启动，启动完后默认端口是9200,9300是集群之间各台机器通信的端口（广播，投票等）
3.安装head插件（页面虽然有点丑，用起来蛮清晰的）[具体安装流程](https://blog.csdn.net/qq_36819281/article/details/83963475)
4.安装的时候会有很多坑，因为要node环境，中间遇到的就是npm install的时候，下载phantomjs一直不成功，[解决方案](https://blog.csdn.net/caseywei/article/details/83071203),完全按照上面操作就行。这一步操作完然后再去npm install
5.安装完后，启动后访问9100端口就行
6.安装kibana （这个好像下载解压后直接启动就行）

7.因为有logstash，所以还要看看日志能不能加载到es里面去（这里也是大坑）
8.解压完后在bin文件夹里面新建一个logstash.conf配置文件，里面写上（这个配置文件网上有很多写法，这个相对比较简单）
`input {
    file {
        path => "D:/logs/logstash.log"
        codec => "json"
        type => "logstash_type"
        start_position => "beginning"
    }
}
output {
	stdout{}
    elasticsearch {
		hosts => ["http://127.0.0.1:9200"]
		index => "log-%{+YYYY.MM}"
	}
}`
9.启动就是，启动的时候，控制台如果输出了你写的path路径下，日志文件的日志的话就说明对了，不然就是不对。然后去head插件或者kibana里面查询数据就行。
