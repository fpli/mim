package com.sap.mim.inteface;

import com.sap.mim.entity.BusinessPackets;
import com.sap.mim.entity.HeartBeatPackets;

/**
 * 描述:客服端推送数据接口
 */
public interface TCPHandle {

    int sendHeartBeatMessage(HeartBeatPackets message);



}
