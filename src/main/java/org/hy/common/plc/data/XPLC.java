package org.hy.common.plc.data;

import java.io.Serializable;

import org.apache.plc4x.java.api.PlcConnection;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.XJavaID;
import org.hy.common.plc.enums.PLCProtocolType;
import org.hy.common.plc.io.IPlcIO;
import org.hy.common.plc.io.PlcIO4X;
import org.hy.common.plc.io.PlcIOS200;
import org.hy.common.xml.log.Logger;





/**
 * PLC设备的XJava实例类
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-05-11
 * @version     v1.0
 */
public class XPLC implements XJavaID ,Serializable
{
    
    private static final Logger $Logger = new Logger(XPLC.class);
    
    private static final long serialVersionUID = 157235421062133903L;
    
    
    
    /** 外界自定义的配置信息 */
    private Object    config;
    
    /** PLC设备配置 */
    private PLCConfig plcConfig;
    
    /** 统一多个组件的PLC连接、读、写等操作 */
    private IPlcIO    plcIO;
    
    
    
    public XPLC(PLCConfig i_PLCDeviceConfig)
    {
        this(i_PLCDeviceConfig ,i_PLCDeviceConfig);
    }
    
    
    
    public XPLC(Object i_Config ,PLCConfig i_PLCDeviceConfig)
    {
        this.config    = i_Config;
        this.plcConfig = i_PLCDeviceConfig;
    }
    
    
    
    /**
     * 连接物联设备
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-11
     * @version     v1.0
     *
     * @return
     * @throws Exception 
     */
    public synchronized Return<PlcConnection> connect()
    {
        if ( this.plcConfig == null )
        {
            Exception v_Error = new NullPointerException("PLC is null.");
            $Logger.error(v_Error);
            return new Return<PlcConnection>(false);
        }
        
        if ( Help.isNull(this.plcConfig.getProtocol()) )
        {
            Exception v_Error = new NullPointerException("PLC[" + this.plcConfig.getXid() + "]'s Protocol is null.");
            $Logger.error(v_Error);
            return new Return<PlcConnection>(false);
        }
        
        if ( PLCProtocolType.S7_200_Smart.equals(PLCProtocolType.get(this.plcConfig.getProtocol())) )
        {
            this.plcIO = new PlcIOS200(this.plcConfig);
        }
        else
        {
            this.plcIO = new PlcIO4X(this.plcConfig);
        }
        
        try
        {
            return this.plcIO.connect();
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        return new Return<PlcConnection>(false);
    }
    
    
    
    /**
     * 是否已连接成功
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-04
     * @version     v1.0
     *
     * @return
     */
    public boolean isConnected()
    {
        return this.plcIO.isConnected();
    }
    
    
    
    /**
     * 关闭连接
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-04
     * @version     v1.0
     *
     * @param i_PlcConnection  PLC连接对象
     */
    public void close(PlcConnection i_PlcConnection)
    {
        this.plcIO.close(i_PlcConnection);
    }
    
    
    
    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    @Override
    public void setXJavaID(String i_XJavaID)
    {
        this.plcConfig.setXid(i_XJavaID);
    }
    
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    @Override
    public String getXJavaID()
    {
        return this.plcConfig.getXid();
    }


    
    /**
     * 获取：注解说明
     */
    @Override
    public String getComment()
    {
        return this.plcConfig.getComment();
    }


    
    /**
     * 设置：注解说明
     * 
     * @param i_Comment 注解说明
     */
    @Override
    public void setComment(String i_Comment)
    {
        this.plcConfig.setComment(i_Comment);
    }
    
    
    
    /**
     * 获取：统一多个组件的PLC连接、读、写等操作
     */
    public IPlcIO getPlcIO()
    {
        return plcIO;
    }
    
    
    
    /**
     * 获取：外界自定义的配置信息
     */
    public Object getConfig()
    {
        return config;
    }
    
}
