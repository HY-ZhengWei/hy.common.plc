package org.hy.common.plc;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.plc.data.PLCDatagramConfig;
import org.hy.common.plc.data.XPLC;
import org.hy.common.xml.XJava;





/**
 * PLC数据读写
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-11-27
 * @version     v1.0
 *              v2.0  2025-08-19  优化：统一多个组件的PLC连接、读、写等操作
 *              v2.1  2026-02-08  修正：超时时长从秒变为毫秒单位
 */
public class PLC
{
    
    /** 默认数据读写超时时长。单位：毫秒 */
    public static final long $Timeout = 5000L;
    
    
    
    /** 物联设备XID */
    private String plcXID;
    
    /** 数据报文XID */
    private String datagramXID;
    
    /** 数据读写超时时长（单位：毫秒） */
    private Long   timeout;
    
    
    
    public PLC()
    {
        this.timeout = $Timeout;
    }
    
    
    
    public PLC(String i_PlcXID ,String i_DatagramXID)
    {
        this();
        
        this.setPlcXID(i_PlcXID);
        this.setDatagramXID(i_DatagramXID);
    }
    
    
    
    /**
     * 写入数据。
     * 
     * 方法内部不再重复验证方法入参数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-11-27
     * @version     v1.0
     *
     * @param i_Datas  数据集合
     * @return
     */
    public boolean writeDatas(Map<String ,Object> i_Datas)
    {
        XPLC              v_XPLC      = (XPLC)              XJava.getObject(this.plcXID);
        PLCDatagramConfig v_XDatagram = (PLCDatagramConfig) XJava.getObject(this.datagramXID);
        
        return v_XPLC.getPlcIO().writeDatas(v_XDatagram ,i_Datas ,Help.max(v_XPLC.getPlcIO().getPLCConfig().getTimeout() ,this.timeout));
    }
    
    
    
    /**
     * 读取数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-11-27
     * @version     v1.0
     *
     * @return
     */
    public Map<String ,Object> readDatas()
    {
        XPLC                v_XPLC      = (XPLC)              XJava.getObject(this.plcXID);
        PLCDatagramConfig   v_XDatagram = (PLCDatagramConfig) XJava.getObject(this.datagramXID);
        
        return v_XPLC.getPlcIO().readDatas(v_XDatagram ,Help.max(v_XPLC.getPlcIO().getPLCConfig().getTimeout() ,this.timeout));
    }
    
    
    
    /**
     * 获取：物联设备XID
     */
    public String getPlcXID()
    {
        return plcXID;
    }

    
    /**
     * 设置：物联设备XID
     * 
     * @param i_PlcXID 物联设备XID
     */
    public void setPlcXID(String i_PlcXID)
    {
        this.plcXID = i_PlcXID;
    }

    
    /**
     * 获取：数据报文XID
     */
    public String getDatagramXID()
    {
        return datagramXID;
    }

    
    /**
     * 设置：数据报文XID
     * 
     * @param i_DatagramXID 数据报文XID
     */
    public void setDatagramXID(String i_DatagramXID)
    {
        this.datagramXID = i_DatagramXID;
    }

    
    /**
     * 获取：数据读写超时时长（单位：毫秒）
     */
    public Long getTimeout()
    {
        return timeout;
    }

    
    /**
     * 设置：数据读写超时时长（单位：毫秒）
     * 
     * @param i_Timeout 数据读写超时时长（单位：毫秒）
     */
    public void setTimeout(Long i_Timeout)
    {
        this.timeout = i_Timeout;
    }
    
}
