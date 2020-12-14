/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.registry.server.session.remoting.handler;

import com.alipay.sofa.registry.common.model.Node.NodeType;
import com.alipay.sofa.registry.common.model.sessionserver.DataPushRequest;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.Channel;
import com.alipay.sofa.registry.server.shared.remoting.AbstractClientHandler;
import com.alipay.sofa.registry.task.listener.TaskEvent;
import com.alipay.sofa.registry.task.listener.TaskEvent.TaskType;
import com.alipay.sofa.registry.task.listener.TaskListenerManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * current for standard env temp publisher push
 *
 * @author shangyu.wh
 * @version $Id: DataChangeRequestHandler.java, v 0.1 2017-12-12 15:09 shangyu.wh Exp $
 */
public class DataPushRequestHandler extends AbstractClientHandler<DataPushRequest> {

    private static final Logger LOGGER      = LoggerFactory.getLogger(DataPushRequestHandler.class);

    private static final Logger TASK_LOGGER = LoggerFactory.getLogger(DataPushRequestHandler.class,
                                                "[Task]");
    /**
     * trigger task com.alipay.sofa.registry.server.meta.listener process
     */
    @Autowired
    private TaskListenerManager taskListenerManager;

    @Override
    protected NodeType getConnectNodeType() {
        return NodeType.DATA;
    }

    @Override
    public Object doHandle(Channel channel, DataPushRequest request) {
        try {
            fireDataPushTask(request);
        } catch (Exception e) {
            LOGGER.error("DataPush Request error!", e);
            throw new RuntimeException("DataPush Request error!", e);
        }
        return null;
    }

    private void fireDataPushTask(DataPushRequest dataPushRequest) {
        //trigger fetch data for subscriber,and push to client node
        TaskEvent taskEvent = new TaskEvent(dataPushRequest, TaskType.DATA_PUSH_TASK);
        TASK_LOGGER.info("send " + taskEvent.getTaskType() + " taskEvent:{}", taskEvent);
        taskListenerManager.sendTaskEvent(taskEvent);
    }

    @Override
    public Class interest() {
        return DataPushRequest.class;
    }
}