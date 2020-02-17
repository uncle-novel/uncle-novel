package com.unclezs.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/*
 *@author unclezs.com
 *@date 2019.06.24 15:52
 */
public class VoiceUtil {
    private ActiveXComponent sap=null;
    private Dispatch sapo=null;
    //开始朗读
    public void readText(String content){
        ComThread.InitSTA();
        new Thread(()->{
            try {
                // 加载
                sap=new ActiveXComponent("Sapi.SpVoice");
                // 获取
                sapo = sap.getObject();
                // 音量 0-100
                sap.setProperty("Volume", new Variant(100));
                // 语音朗读速度 -10 到 +10
                sap.setProperty("Rate", new Variant(0));
                // 执行朗读
                Dispatch.call(sapo, "Speak", new Variant(content));
            }catch (Throwable t){
                System.out.println("朗读强行停止！");
            }finally {
                sap.safeRelease();
                sapo.safeRelease();
            }
        }).start();

    }
    //停止朗读
    public void stop(){
        try {
            if(sapo!=null){
                Dispatch.call(sapo,"Pause");
            }
        }catch (Throwable t){
            System.out.println("停止朗读");
        }
    }
}
