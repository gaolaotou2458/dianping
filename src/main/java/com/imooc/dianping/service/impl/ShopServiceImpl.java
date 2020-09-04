package com.imooc.dianping.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imooc.dianping.common.BusinessException;
import com.imooc.dianping.common.EmBusinessError;
import com.imooc.dianping.mapper.CategoryMapper;
import com.imooc.dianping.mapper.SellerMapper;
import com.imooc.dianping.mapper.ShopMapper;
import com.imooc.dianping.model.Category;
import com.imooc.dianping.model.Seller;
import com.imooc.dianping.model.Shop;
import com.imooc.dianping.service.ShopService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Administrator
 * @date: 2020-08-13 14:27
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    @Transactional
    public Shop create(Shop shop) throws BusinessException {
        shop.setCreatedAt(new Date());
        shop.setUpdatedAt(new Date());

        //校验商家是否存在正确
        Seller sellerModel = sellerMapper.selectByPrimaryKey(shop.getSellerId());
        if(sellerModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商户不存在");
        }

        if(sellerModel.getDisabledFlag().intValue() == 1){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商户已禁用");
        }

        //校验类目
        Category categoryModel = categoryMapper.selectByPrimaryKey(shop.getCategoryId());
        if(categoryModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"类目不存在");
        }
        shopMapper.insertSelective(shop);

        return get(shop.getId());
    }

    @Override
    public Shop get(BigDecimal longitude,
                    BigDecimal latitude,Integer id) {
        Shop shopModel = shopMapper.searchById(longitude, latitude,id);
        if(shopModel == null) {
            return null;
        }
        shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
        shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        return shopModel;
    }

    public Shop get(Integer id) {
        Shop shopModel = shopMapper.selectByPrimaryKey(id);
        if(shopModel == null) {
            return null;
        }
        shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
        shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        return shopModel;
    }

    @Override
    public List<Shop> selectAll() {
        List<Shop> shopModelList = shopMapper.selectAll();
        shopModelList.forEach(shopModel -> {
            shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
            shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<Shop> recommend(BigDecimal longitude, BigDecimal latitude) {
        List<Shop> shopModelList = shopMapper.recommend(longitude, latitude);
        shopModelList.forEach(shopModel -> {
            shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
            shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        Example example = new Example(Shop.class);
        Example.Criteria criteria = example.createCriteria();
        String searchKey = "";
        if(StringUtils.isNotBlank(keyword)){
            searchKey = "%" + keyword + "%";
            criteria.andLike("name", searchKey);
        }
        if(categoryId != null){
            criteria.andEqualTo("categoryId", categoryId);

        }
        if(StringUtils.isNotBlank(tags)){
            criteria.andEqualTo("tags", tags);
        }
        List<Shop> shops = shopMapper.selectByExample(example);

        //根据tags分组
        Map<String, List<Shop>> dateListMap = shops.stream()
                .collect(Collectors.groupingBy(Shop::getTags));

        //System.out.println(dateListMap);
        // 遍历map,求tag对应记录的条数

        for (Map.Entry<String, List<Shop>> detailEntry:dateListMap.entrySet()){
            HashMap<String, Object> resMap = new HashMap<>(128);
            String tag = detailEntry.getKey();
            int daySize = detailEntry.getValue().size();
            resMap.put("tags",tag);
            resMap.put("num",daySize);
            resultList.add(resMap);
        }
        System.out.println(resultList);
        return resultList;
    }

    @Override
    public Integer countAllShop() {
        return shopMapper.selectCount(new Shop());
    }

    @Override
    public List<Shop> search(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) {

        List<Shop> shopModelList = shopMapper.search(longitude,latitude,keyword,orderby,categoryId,tags);
        shopModelList.forEach(shopModel -> {
            shopModel.setSeller(sellerMapper.selectByPrimaryKey(shopModel.getSellerId()));
            shopModel.setCategory(categoryMapper.selectByPrimaryKey(shopModel.getCategoryId()));
        });
        return shopModelList;
    }



    /* es7返回的数据
    "max_score" : 5.204194,
    "hits" : [
    {
        "_shard" : "[shop][0]",
            "_node" : "LbBDV_YaQKaMUBXE3B4YcQ",
            "_index" : "shop",
            "_type" : "_doc",
            "_id" : "9",
            "_score" : 5.204194,
            "_source" : {
        "category_name" : "酒店",
                "seller_disabled_flag" : 0,
                "tags" : "落地大窗",
                "@timestamp" : "2020-09-01T06:17:00.296Z",
                "category_id" : 2,
                "@version" : "1",
                "name" : "凯悦酒店",
                "seller_remark_score" : 4.3,
                "location" : "31.306172,121.525843",
                "remark_score" : 3.9,
                "price_per_man" : 97,
                "id" : 9,
                "seller_id" : 17
    },
        "fields" : {
        "distance" : [
        566.6088616442112
          ]
    },
    */
    //基于Rest Client简易的请求
    /*
    @Override
    public Map<String, Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) throws IOException {
        Map<String, Object> result = new HashMap<>();
        //搜索索引名
        SearchRequest searchRequest = new SearchRequest("shop");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
        //设置超时时间
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);

        //存储es7返回的document 主键
        List<Integer> shopIdsList = new ArrayList<>();
        //用默认GET方式去请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //命中的
        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit hit : hits) {
            shopIdsList.add(new Integer(hit.getSourceAsMap().get("id").toString()));
        }
        List<Shop> shopList =shopIdsList.stream().map(id -> {
            return get(longitude,latitude,id);
        }).collect(Collectors.toList());
        result.put("shop", shopList);
        return result;
    }
    */


    //进一步加深搜索引擎 构建复杂搜索模型
    @Override
    public Map<String, Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) throws IOException {
        Map<String, Object> result = new HashMap<>();

        Request request = new Request("GET","/shop/_search");
        //直接从Kibana中复制然后黏贴再替换参数
//        String reqJson = "{\n" +
//                "  \"_source\": \"*\", \n" +
//                "  \"script_fields\": {\n" +
//                "    \"distance\": {\n" +
//                "      \"script\": {\n" +
//                "        \"source\":\"haversin(lat,lon,doc['location'].lat,doc['location'].lon)\",\n" +
//                "        \"lang\":\"expression\",\n" +
//                "        \"params\": {\"lat\":" +latitude.toString() +",\"lon\":" + longitude.toString() +"}\n" +
//                "      }\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"query\": {\n" +
//                "    \"function_score\": {\n" +
//                "      \"query\": {\n" +
//                "        \"bool\": {\n" +
//                "          \"must\": [\n" +
//                "            {\"match\":{\"name\": {\"query\": \""+keyword+"\",\"boost\": 0.1}}},\n" +
//                "            {\"term\":{\"seller_disabled_flag\": 0}}\n" +
//                "          ]\n" +
//                "        }\n" +
//                "      },\n" +
//                "      \"functions\": [\n" +
//                "        {\n" +
//                "          \"gauss\": {\n" +
//                "            \"location\": {\n" +
//                "              \"origin\": \"" +latitude.toString()+"," + longitude.toString() +"\",\n" +
//                "              \"scale\": \"100km\",\n" +
//                "              \"offset\": \"0km\",\n" +
//                "              \"decay\": 0.5\n" +
//                "            }\n" +
//                "          },\n" +
//                "          \"weight\": 9\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"field_value_factor\":{\n" +
//                "            \"field\":\"remark_score\"\n" +
//                "          },\n" +
//                "          \"weight\": 0.2\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"field_value_factor\":{\n" +
//                "            \"field\":\"seller_remark_score\"\n" +
//                "          },\n" +
//                "          \"weight\": 0.1\n" +
//                "        }\n" +
//                "      ],\n" +
//                "      \n" +
//                "      \"score_mode\": \"sum\",\n" +
//                "      \"boost_mode\": \"sum\"\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"sort\": [\n" +
//                "    {\n" +
//                "      \"_score\": {\n" +
//                "        \"order\": \"desc\"\n" +
//                "      }\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
        //通过JSONObject构建请求
        JSONObject jsonRequestObj = new JSONObject();
        //构建source部分
        jsonRequestObj.put("_source","*");
        //构建自定义距离字段
        jsonRequestObj.put("script_fields",new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").put("distance",new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").put("script" ,new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("source","haversin(lat,lon,doc['location'].lat,doc['location'].lon)");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("lang","expression");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("params",new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lat",latitude);
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lon",longitude);

        //构建query
        jsonRequestObj.put("query",new JSONObject());

        //构建function score
        jsonRequestObj.getJSONObject("query").put("function_score",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("query",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").put("bool", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").put("must", new JSONArray());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                .getJSONObject("bool").getJSONArray("must").add(new JSONObject());
        //构建match query {"match":{"name": {"query": "凯悦","boost": 0.1}}},
        int queryIndex = 0;
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("match",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").put("name",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name")
                .put("query",keyword);
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name")
                .put("boost",0.1);

        queryIndex++;
        //构建第二个query的条件 {"term":{"seller_disabled_flag": 0}},
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                .getJSONObject("bool").getJSONArray("must").add(new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term")
                .put("seller_disabled_flag",0);

        if(categoryId != null) {
            queryIndex++;
            //构建第三个query的条件   {"term": {"category_id":2}}
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                    .getJSONObject("bool").getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                    .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                    .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term")
                    .put("category_id",categoryId);
        }

        if(StringUtils.isNotBlank(tags)) {
            queryIndex++;
            //构建第三个query的条件   {"term": {"category_id":2}}
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                    .getJSONObject("bool").getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                    .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query")
                    .getJSONObject("bool").getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term")
                    .put("tags",tags);
        }

        //构建functions部分
        int functionIndex = 0;
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("functions", new JSONArray());
        if(orderby == null) {
            //构建"gauss": {
            //            "location": {
            //              "origin": "31.23916171,127.48789949",
            //              "scale": "100km",
            //              "offset": "0km",
            //              "decay": 0.5
            //            }
            //          },
            //          "weight": 9
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("gauss", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("gauss").put("location", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("origin", latitude.toString() + "," + longitude.toString());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("scale", "100km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("offset", "0km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("gauss").getJSONObject("location").put("decay", "0.5");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("weight", 9);

            //构建 "field_value_factor":{
            //            "field":"remark_score"
            //          },
            //          "weight": 0.2
            functionIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("weight", 0.2);
            //构建"field_value_factor":{
            //            "field":"seller_remark_score"
            //          },
            //          "weight": 0.1
            functionIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "seller_remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("weight", 0.1);


            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode", "sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode", "sum");
        } else {
            //低价排序
            functionIndex = 0 ;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).getJSONObject("field_value_factor").put("field", "price_per_man");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions")
                    .getJSONObject(functionIndex).put("weight", 1);


            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode", "sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode", "replace");
        }

        //构建排序字段
        // "sort": [
        //    {
        //      "_score": {
        //        "order": "desc"
        //      }
        //    }
        //  ]
        jsonRequestObj.put("sort",new JSONArray());
        jsonRequestObj.getJSONArray("sort").add(new JSONObject());
        jsonRequestObj.getJSONArray("sort").getJSONObject(0).put("_score",new JSONObject());
        if(orderby == null) {
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","desc");
        } else {
            //低价排序
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","asc");
        }

        //集合字段 tags
        jsonRequestObj.put("aggs",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").put("group_by_tags",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms")
            .put("field","tags");



        String reqJson = jsonRequestObj.toJSONString();
        System.out.println("reqJson***************************************");
        System.out.println(reqJson);
        request.setJsonEntity(reqJson);
        Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
        String responseStr = EntityUtils.toString(response.getEntity());
        System.out.println(responseStr);

        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArr = jsonObject.getJSONObject("hits").getJSONArray("hits");
        List<Shop> shopList = new ArrayList<>();
        for(int i=0;i<jsonArr.size();i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            Integer id = new Integer(jsonObj.get("_id").toString());
            //获取distince
            BigDecimal distance = new BigDecimal(jsonObj.getJSONObject("fields").
                    getJSONArray("distance").get(0).toString());
            Shop shop = get(id);
            //向上取整数 从km转换为m
            shop.setDistance(distance.setScale(0,BigDecimal.ROUND_CEILING).intValue() * 1000);
            shopList.add(shop);

        }
        result.put("shop", shopList);
        //返回es 中的tags"
        // aggregations" : {
        //    "group_by_tags" : {
        //      "doc_count_error_upper_bound" : 0,
        //      "sum_other_doc_count" : 0,
        //      "buckets" : [
        //        {
        //          "key" : "落地大窗",
        //          "doc_count" : 2
        //        },
        //        {
        //          "key" : "有WIFI",
        //          "doc_count" : 1
        //        }
        //      ]
        //    }
        //  }
        ArrayList<Map> tagsList = new ArrayList<>();
        JSONArray tagsJsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
        for(int i=0;i<tagsJsonArray.size();i++) {
            JSONObject jsonObj = tagsJsonArray.getJSONObject(i);
            Map<String,Object> tagMap = new HashMap<>();
            tagMap.put("tags", jsonObj.getString("key"));
            tagMap.put("num", jsonObj.getString("doc_count"));
            tagsList.add(tagMap);
        }
        result.put("tags", tagsList);
        return result;
    }
}
