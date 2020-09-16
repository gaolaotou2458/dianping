package com.imooc.dianping.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.imooc.dianping.mapper.ShopMapper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息消费
 */
@Component
public class CanalScheduling implements Runnable, ApplicationContextAware {

    //用于获取bean
    private ApplicationContext applicationContext;

    @Resource
    private CanalConnector canalConnector;

    @Autowired
    private ShopMapper shopMapper;
    
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 容器每隔100ms唤醒CanalScheduling run的线程
     */
    @Scheduled(fixedDelay = 100)
    @Override
    public void run() {
        long bacthId = -1;
        try {
            int batchSize = 1000;
            //从canal获取1000条消息，没告诉ack，需要手动调用ack方法，确认消息完成消费，告诉delpoyer可以将这条消息标记为已消费
            Message message = canalConnector.getWithoutAck(batchSize);
            //获取批次ID
            bacthId = message.getId();
            List<CanalEntry.Entry> entries = message.getEntries();
            if(bacthId != -1 && entries.size() > 0) {
                //处理消息
                entries.forEach(entry -> {
                    //这条消息的beanlog是以ROWDATA的方式丢出来的
                    if(entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                        //解析处理
                        publishCanalEvent(entry);
                    }
                });
            }
            //通知deployer这条bacthId处理完成
            canalConnector.ack(bacthId);
        } catch (CanalClientException e) {
            e.printStackTrace();
            //通知这个批次没消费成功，下次继续可以消费
            canalConnector.rollback(bacthId);
        }
    }


    //解析处理
    private void publishCanalEvent(CanalEntry.Entry entry) {
        CanalEntry.EventType entryType = entry.getHeader().getEventType();
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        CanalEntry.RowChange change = null;
        try {
            change = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return;
        }
        change.getRowDatasList().forEach(rowDate -> {
            List<CanalEntry.Column> columns = rowDate.getAfterColumnsList();
            String primaryKey = "id";
            CanalEntry.Column idColumn = columns.stream().filter(
              column -> column.getIsKey() && primaryKey.equals(column.getName()))
                    .findFirst().orElse(null);
            Map<String, Object> dataMap = parseColumnsToMap(columns);
            try {
                indexES(dataMap,database,table);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    Map<String,Object> parseColumnsToMap(List<CanalEntry.Column> columns) {
        Map<String,Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if(column == null) {
                return;
            }
            jsonMap.put(column.getName(),column.getValue());

        });
        return jsonMap;
    }

    //根据判断修改的是哪几个表最终更新ES,
    private void indexES(Map<String,Object> dataMap, String database, String table) throws IOException {
        if(!StringUtils.equals("dianpingdb",database)){
            return;
        }
        System.out.println("=====================");
        System.out.println(dataMap);
        System.out.println(database);
        System.out.println(table);
        List<Map<String, Object>> result = new ArrayList<>();
        if(StringUtils.equals("seller",table)){
            result = shopMapper.buildESQuery(new Integer((String)dataMap.get("id")),null,null);
        } else if(StringUtils.equals("category",table)){
            result = shopMapper.buildESQuery(null,new Integer((String)dataMap.get("id")),null);
        } else if(StringUtils.equals("shop",table)){
            result = shopMapper.buildESQuery(null,null, new Integer((String)dataMap.get("id")));
        } else {
            return;
        }
        //更新索引
        for(Map<String, Object> map : result) {
            IndexRequest indexRequest = new IndexRequest("shop");
            indexRequest.id(String.valueOf(map.get("id")));
            indexRequest.source(map);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
