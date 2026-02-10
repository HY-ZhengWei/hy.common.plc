package org.hy.common.plc.io;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.plc.data.PLCConfig;
import org.hy.common.plc.data.PLCDataItemConfig;
import org.hy.common.plc.data.PLCDatagramConfig;
import org.hy.common.plc.enums.PLCDataType;
import org.hy.common.plc.pool.PlcConnectionPool;
import org.hy.common.xml.log.Logger;





/**
 * 统一多个组件的PLC连接、读、写等操作：PLC4X的实现
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-19
 * @version     v1.0
 *              v1.1  2025-12-10  修正：自动重连机制的问题：在关闭close()时，将 plcConnect 赋值为空
 *                                修正：read和write两方法添加同步锁
 *              v1.2  2026-01-08  优化：日志输出逻辑，方便在《日志分析》页面上排查问题
 *              v1.3  2026-02-08  修正：超时时长从秒变为毫秒单位
 *              v2.0  2026-02-10  添加：连接池
 */
public class PlcIO4X implements IPlcIO
{
    
    private static final Logger $Logger = new Logger(PlcIO4X.class);
    
    
    /** PLC设备配置 */
    private PLCConfig         plcConfig;
    
    /** PLC连接池 */
    private PlcConnectionPool plcConnectPool;
    
    
    
    public PlcIO4X(PLCConfig i_PLCConfig)
    {
        this.plcConfig = i_PLCConfig;
    }
    
    
    
    /**
     * 获取PLC连接配置信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     */
    public PLCConfig getPLCConfig()
    {
        return this.plcConfig;
    }
    
    
    
    /**
     * 获取PLC具体实现类的连接对象
     * 
     * 注：常规情况下不会用的此方法，所有对外的方法应当由本接口其它方法来统一规划后实现。
     *     但又担心到前期规划不位，先暂时允许少量特殊情况下通过此方法解决问题。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     */
    @Deprecated
    public Object getConnectObject()
    {
        return this.plcConnectPool;
    }
    
    
    
    /**
     * 写入数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-11-27
     * @version     v1.0
     *
     * @param i_Datagram  数据报文
     * @param i_Datas     数据集合
     * @param i_Timeout   数据读写超时时长（单位：毫秒）
     * @return
     */
    public boolean writeDatas(PLCDatagramConfig i_Datagram ,Map<String ,Object> i_Datas ,long i_Timeout)
    {
        StringBuilder v_LogBuffer = new StringBuilder();
        boolean       v_Ret       = true;
        PlcConnection v_PlcConn   = null;
        
        try
        {
            if ( i_Datagram == null )
            {
                $Logger.error("Datagram is null");
                v_Ret = false;
                return v_Ret;
            }
            
            v_PlcConn = this.connect().getParamObj();
            if ( v_PlcConn == null )
            {
                $Logger.error("PlcXID[" + this.plcConfig.getXid() + "] connect error.");
                v_Ret = false;
                return v_Ret;
            }
            
            String v_Titel = "PLC Write " + Help.NVL(this.plcConfig.getComment()) + this.plcConfig.getXid() + "." + Help.NVL(i_Datagram.getComment()) + i_Datagram.getXid();
            $Logger.info(v_Titel);
            v_LogBuffer.append(v_Titel).append("\n");
            
            List<PLCDataItemConfig> v_Items     = i_Datagram.getItems();
            int                     v_ItemCount = 0;
            PlcWriteResponse        v_Response  = null;
            //synchronized (this)
            //{
            PlcWriteRequest.Builder v_PlcWriteReqBuilder = v_PlcConn.writeRequestBuilder();
            
            for (PLCDataItemConfig v_Item : v_Items)
            {
                String v_PLCTagAddress = v_Item.makePLCTagAddress();
                if ( Help.isNull(v_PLCTagAddress) )
                {
                    continue;
                }
                
                Object v_DataItemValue = Help.getValueIgnoreCase(i_Datas ,v_Item.getCode());
                if ( v_DataItemValue == null )
                {
                    $Logger.error("写入PLC数据为空：" + v_Item.getCode() + " " + v_Item.getName()
                                + "\n寄存器名：" + v_Item.getRegisterType().getValue()
                                + "\n寄存编号：" + v_Item.getRegisterNo()
                                + "\n偏移数量：" + v_Item.getRegisterOffset()
                                + "\n数据类型：" + v_Item.getDataType().getValue());
                    v_Ret = false;
                    break;
                }
                
                v_LogBuffer.append("PLC Write " + v_Item.getName() + v_Item.getCode() + "：" + v_PLCTagAddress + "=" + v_DataItemValue).append("\n");
                v_PlcWriteReqBuilder.addTagAddress(v_Item.getCode() ,v_PLCTagAddress ,v_DataItemValue);
                v_ItemCount++;
            }
            
            if ( v_ItemCount <= 0 )
            {
                v_Ret = false;
                $Logger.error("DatagramXID[" + i_Datagram.getXid() + "] itemCount is 0");
                return v_Ret;
            }
            else if ( v_Ret )
            {
                long            v_Timeout         = i_Timeout <= 0L ? 0L : i_Timeout;
                PlcWriteRequest v_PlcWriteRequest = v_PlcWriteReqBuilder.build();
                v_Response = v_PlcWriteRequest.execute().get(v_Timeout ,TimeUnit.MILLISECONDS);
            }
            else
            {
                return v_Ret;
            }
            //}
            
            // 检查是否成功
            for (PLCDataItemConfig v_Item : v_Items)
            {
                String v_PLCTagAddress = v_Item.makePLCTagAddress();
                if ( Help.isNull(v_PLCTagAddress) )
                {
                    continue;
                }
                
                if ( v_Response.getResponseCode(v_Item.getCode()) != PlcResponseCode.OK )
                {
                    $Logger.error("写入PLC数据失败：" + v_Item.getCode() + " " + v_Item.getName()
                                + "\n" + v_Response.getResponseCode(v_Item.getCode()).getValue() + "=" + v_Response.getResponseCode(v_Item.getCode()).name()
                                + "\n寄存器名：" + v_Item.getRegisterType().getValue()
                                + "\n寄存编号：" + v_Item.getRegisterNo()
                                + "\n偏移数量：" + v_Item.getRegisterOffset()
                                + "\n数据类型：" + v_Item.getDataType().getValue());
                    v_Ret = false;
                    break;
                }
            }
        }
        catch (Exception exce)
        {
            v_Ret = false;
            $Logger.error(exce);
        }
        finally
        {
            this.close(v_PlcConn);
        }
        
        $Logger.info(v_LogBuffer.toString());
        
        return v_Ret;
    }
    
    
    
    /**
     * 读取数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-11-27
     * @version     v1.0
     *
     * @param i_Datagram  数据报文
     * @param i_Timeout   数据读写超时时长（单位：毫秒）
     * @return
     */
    public Map<String ,Object> readDatas(PLCDatagramConfig i_Datagram ,long i_Timeout)
    {
        StringBuilder       v_LogBuffer = new StringBuilder();
        Map<String ,Object> v_Datas     = new LinkedHashMap<String ,Object>();
        PlcConnection       v_PlcConn   = null;
        
        v_LogBuffer.append(Date.getNowTime().getFullMilli()).append("\n");
        
        try
        {
            if ( i_Datagram == null )
            {
                $Logger.error("Datagram is null");
                return v_Datas;
            }
            
            v_PlcConn = this.connect().getParamObj();
            if ( v_PlcConn == null )
            {
                $Logger.error("PlcXID[" + this.plcConfig.getXid() + "] connect error.");
                return v_Datas;
            }
            
            String v_Titel = "PLC Read " + Help.NVL(this.plcConfig.getComment()) + this.plcConfig.getXid() + "." + Help.NVL(i_Datagram.getComment()) + i_Datagram.getXid();
            $Logger.info(v_Titel);
            v_LogBuffer.append(v_Titel).append("\n");
            
            int                     v_ItemCount       = 0;
            List<PLCDataItemConfig> v_Items           = i_Datagram.getItems();
            PlcReadResponse         v_PLCReadResponse = null;
            // synchronized ( this )
            // {
            PlcReadRequest.Builder v_PLCReadReqBuilder = v_PlcConn.readRequestBuilder();
            
            for (PLCDataItemConfig v_Item : v_Items)
            {
                String v_PLCTagAddress = v_Item.makePLCTagAddress();
                if ( Help.isNull(v_PLCTagAddress) )
                {
                    continue;
                }
                
                v_LogBuffer.append("PLC Read " + v_Item.getName() + v_Item.getCode() + "：" + v_PLCTagAddress).append("\n");
                v_PLCReadReqBuilder.addTagAddress(v_Item.getCode() ,v_PLCTagAddress);
                v_ItemCount++;
            }
            
            if ( v_ItemCount <= 0 )
            {
                $Logger.error("DatagramXID[" + i_Datagram.getXid() + "] itemCount is 0");
                return v_Datas;
            }
            
            long           v_Timeout        = i_Timeout <= 0L ? 0L : i_Timeout;
            PlcReadRequest v_PlcReadRequest = v_PLCReadReqBuilder.build();
            v_PLCReadResponse = v_PlcReadRequest.execute().get(v_Timeout ,TimeUnit.MILLISECONDS);
            // }
            
            for (PLCDataItemConfig v_Item : v_Items)
            {
                String v_PLCTagAddress = v_Item.makePLCTagAddress();
                if ( Help.isNull(v_PLCTagAddress) )
                {
                    continue;
                }
                
                if ( v_PLCReadResponse.getResponseCode(v_Item.getCode()) != PlcResponseCode.OK )
                {
                    $Logger.error("读取PLC数据失败：" + v_Item.getCode() + " " + v_Item.getName() 
                                + "\n" + v_PLCReadResponse.getResponseCode(v_Item.getCode()).getValue() + "=" + v_PLCReadResponse.getResponseCode(v_Item.getCode()).name()
                                + "\n寄存器名：" + v_Item.getRegisterType().getValue()
                                + "\n寄存编号：" + v_Item.getRegisterNo()
                                + "\n偏移数量：" + v_Item.getRegisterOffset()
                                + "\n数据类型：" + v_Item.getDataType().getValue());
                    continue;
                }
                
                Object v_DataValue = this.readItemData(v_PLCReadResponse ,v_Item.getCode() ,v_Item.getDataType().getValue());
                if ( v_DataValue != null )
                {
                    v_Datas.put(v_Item.getCode() ,v_DataValue);
                }
                
                v_LogBuffer.append("PLC Read " + v_Item.getName() + v_Item.getCode() + "=" + v_DataValue).append("\n");
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        finally
        {
            this.close(v_PlcConn);
        }
        
        $Logger.info(v_LogBuffer.toString());
        
        return v_Datas;
    }
    
    
    
    /**
     * 读取PLC结果中的一项数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-12-04
     * @version     v1.0
     *
     * @param i_PLCReadResponse  PLC结果集
     * @param i_ItemCode         PLC变量名称
     * @param i_DataType         PLC数据类型
     * @return                   未成功时返回NULL
     */
    private Object readItemData(PlcReadResponse i_PLCReadResponse ,String i_ItemCode ,String i_DataType)
    {
        PLCDataType v_DataType  = PLCDataType.get(i_DataType);
        int         v_NumValues = i_PLCReadResponse.getNumberOfValues(i_ItemCode);
        for (int v_Index=0; v_Index<v_NumValues; v_Index++)
        {
            // 布尔
            if ( PLCDataType.Bool.equals(v_DataType) )
            {
                Boolean v_Value = i_PLCReadResponse.getBoolean(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 字节
            else if ( PLCDataType.Byte.equals(v_DataType) )
            {
                Byte v_Value = i_PLCReadResponse.getByte(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 字
            else if ( PLCDataType.Word.equals(v_DataType) )
            {
                Integer v_Value = i_PLCReadResponse.getInteger(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 双字
            else if ( PLCDataType.DWord.equals(v_DataType) )
            {
                Integer v_Value = i_PLCReadResponse.getInteger(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 8位整数
            else if ( PLCDataType.SInt.equals(v_DataType) )
            {
                Integer v_Value = i_PLCReadResponse.getInteger(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 8位无符号整数
            else if ( PLCDataType.USInt.equals(v_DataType) )
            {
                Integer v_Value = i_PLCReadResponse.getInteger(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 16位整数
            else if ( PLCDataType.Int.equals(v_DataType) )
            {
                Integer v_Value = i_PLCReadResponse.getInteger(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 32位整数
            else if ( PLCDataType.DInt.equals(v_DataType) )
            {
                Integer v_Value = i_PLCReadResponse.getInteger(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 32位无符号整数
            else if ( PLCDataType.UDInt.equals(i_DataType) )
            {
                Integer v_Value = i_PLCReadResponse.getInteger(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 浮点
            else if ( PLCDataType.Real.equals(v_DataType) )
            {
                Float v_Value = i_PLCReadResponse.getFloat(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 双精度浮点
            else if ( PLCDataType.LReal.equals(v_DataType) )
            {
                Double v_Value = i_PLCReadResponse.getDouble(i_ItemCode ,v_Index);
                return v_Value;
            }
            // 字符串
            else if ( PLCDataType.String.equals(v_DataType) )
            {
                String v_Value = i_PLCReadResponse.getString(i_ItemCode ,v_Index);
                return v_Value;
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 连接物联设备
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-11
     * @version     v1.0
     *              v2.0  2026-02-09  添加：升级为连接池
     *
     * @return  Return.boolean   表示是否连接成功
     *          Return.paramObj  表示PLC连接对象
     * @throws Exception 
     */
    public synchronized Return<PlcConnection> connect() throws Exception 
    {
        Return<PlcConnection> v_Ret = new Return<PlcConnection>(false);
        
        if ( this.plcConnectPool == null )
        {
            if ( this.plcConfig == null )
            {
                Exception v_Error = new NullPointerException("PLC is null.");
                $Logger.error(v_Error);
                return v_Ret;
            }
            
            if ( Help.isNull(this.plcConfig.getProtocol()) )
            {
                Exception v_Error = new NullPointerException("PLC[" + this.plcConfig.getXid() + "]'s Protocol is null.");
                $Logger.error(v_Error);
                return v_Ret;
            }
            
            if ( Help.isNull(this.plcConfig.getHost()) )
            {
                Exception v_Error = new NullPointerException("PLC[" + this.plcConfig.getXid() + "]'s Host is null.");
                $Logger.error(v_Error);
                return v_Ret;
            }
            
            if ( Help.isNull(this.plcConfig.getPort()) )
            {
                Exception v_Error = new NullPointerException("PLC[" + this.plcConfig.getXid() + "]'s Port is null.");
                $Logger.error(v_Error);
                return v_Ret;
            }
            
            this.plcConnectPool = new PlcConnectionPool(this.plcConfig);
        }
        
        return v_Ret.set(true).setParamObj(this.plcConnectPool.borrowConnection());
    }
    
    
    
    /**
     * 是否已连接成功
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     */
    public boolean isConnected()
    {
        if ( this.plcConnectPool == null )
        {
            return false;
        }
        else
        {
            return true;  // this.plcConnect.isConnected();
        }
    }
    
    
    
    /**
     * 关闭连接
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *              v2.0  2026-02-09  添加：升级为连接池
     * 
     * @param i_PlcConnection  PLC连接对象
     */
    public void close(PlcConnection i_PlcConnection)
    {
        if ( this.plcConnectPool == null )
        {
            return;
        }
        
        try
        {
            this.plcConnectPool.returnConnection(i_PlcConnection);
        }
        catch (Exception exce)
        {
            $Logger.error(this.plcConfig.getXid() + " 连接关闭时异常" ,exce);
        }
    }
    
}
