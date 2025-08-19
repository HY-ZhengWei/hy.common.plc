package org.hy.common.plc.data;

import java.io.Serializable;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.hy.common.Help;
import org.hy.common.XJavaID;
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
    private Object        config;
    
    /** PLC设备配置 */
    private PLCConfig     plcConfig;
    
    /** PLC设备的连接对象 */
    private PlcConnection plcConnect;
    
    
    
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
     */
    public synchronized boolean connect() 
    {
        if ( this.plcConfig == null )
        {
            Exception v_Error = new NullPointerException("PLC is null.");
            $Logger.error(v_Error);
            return false;
        }
        
        if ( Help.isNull(this.plcConfig.getProtocol()) )
        {
            Exception v_Error = new NullPointerException("PLC[" + this.plcConfig.getXid() + "]'s Protocol is null.");
            $Logger.error(v_Error);
            return false;
        }
        
        if ( Help.isNull(this.plcConfig.getHost()) )
        {
            Exception v_Error = new NullPointerException("PLC[" + this.plcConfig.getXid() + "]'s Host is null.");
            $Logger.error(v_Error);
            return false;
        }
        
        if ( Help.isNull(this.plcConfig.getPort()) )
        {
            Exception v_Error = new NullPointerException("PLC[" + this.plcConfig.getXid() + "]'s Port is null.");
            $Logger.error(v_Error);
            return false;
        }
        
        try
        {
            // 机架号0和插槽号1。在 Siemens 自有软件中的 rack=0&slot=1 
            // PLC4X 或类似的第三方库，正确的格式是 remote-rack&remote-slot
            // 立体仓库   10.1.154.112
            // WYS71200 10.1.154.131、10.1.154.132 
            // 格式为 s7://username:password@IP:Port?timeout=5000
            // {protocol-code}:({transport-code})?//{transport-address}(?{parameter-string})?'
            StringBuilder v_ConnString = new StringBuilder();
            v_ConnString.append(this.plcConfig.getProtocol()).append("://");
            if ( !Help.isNull(this.plcConfig.getUserName()) && !Help.isNull(this.plcConfig.getUserPassword()) )
            {
                v_ConnString.append(this.plcConfig.getUserName());
                v_ConnString.append(":");
                v_ConnString.append(this.plcConfig.getUserPassword());
                v_ConnString.append("@");
            }
            
            v_ConnString.append(this.plcConfig.getHost());
            v_ConnString.append(":");
            v_ConnString.append(this.plcConfig.getPort());
            v_ConnString.append("?timeout=").append(this.plcConfig.getTimeout());
            
            if ( this.plcConfig.getRack() != null )
            {
                v_ConnString.append("&remote-rack=").append(this.plcConfig.getRack());
            }
            if ( this.plcConfig.getSlot() != null )
            {
                v_ConnString.append("&remote-slot=").append(this.plcConfig.getSlot());
            }
            
            PlcDriverManager     v_PlcDriverManager     = PlcDriverManager.getDefault();
            PlcConnectionManager v_PlcConnectionManager = v_PlcDriverManager.getConnectionManager();
            PlcConnection        v_PLCConn              = v_PlcConnectionManager.getConnection(v_ConnString.toString());
            if ( !v_PLCConn.getMetadata().isReadSupported() )
            {
                $Logger.error("PLC[" + this.plcConfig.getXid() + "] connection doesn't support reading.");
                return false;
            }
            
            this.plcConnect = v_PLCConn;
            return true;
        }
        catch (Exception exce)
        {
            $Logger.error("PLC[" + this.plcConfig.getXid() + "] connection error." ,exce);
        }
        
        return false;
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
        return this.plcConnect.isConnected();
    }
    
    
    
    /**
     * 关闭连接
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-04
     * @version     v1.0
     *
     */
    public void close()
    {
        try
        {
            this.plcConnect.close();
        }
        catch (Exception exce)
        {
            $Logger.error(this.plcConfig.getXid() + " 连接关闭时异常" ,exce);
        }
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
     * 获取：PLC设备的连接对象
     */
    public PlcConnection getPlcConnect()
    {
        return plcConnect;
    }
    
    
    
    /**
     * 获取：外界自定义的配置信息
     */
    public Object getConfig()
    {
        return config;
    }
    
}
