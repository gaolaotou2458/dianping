package com.imooc.dianping.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * DisposableBean:spring 荣弃销毁执行一些操作
 * 断开canal客户端 与deployer链接，防止连接泄露
 */
@Component
public class CanalClient implements DisposableBean {

    private CanalConnector canalConnector;

    @Bean
    public CanalConnector getCanalConnector(){
        canalConnector = CanalConnectors.newClusterConnector(
            Lists.newArrayList(
                new InetSocketAddress("127.0.0.1",11111)),
                    "example","canal","canal"
            );
        canalConnector.connect();
        //指定filter 格式{database}.{table}
        canalConnector.subscribe();
        //回滚寻找上次中断的位置
        canalConnector.rollback();

        return canalConnector;
    }

    @Override
    public void destroy() throws Exception {
        if(canalConnector != null) {
            //中断一下，防止泄露
            canalConnector.disconnect();
        }
    }
}
