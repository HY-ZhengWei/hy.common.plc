package org.hy.common.plc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.plc.data.PLCDataItemConfig;
import org.hy.common.plc.data.PLCDatagramConfig;
import org.hy.common.plc.data.XPLC;
import org.hy.common.plc.enums.PLCDataType;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * PLC数据读写
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-11-27
 * @version     v1.0
 */
public class PLC
{
    
    private static final Logger $Logger = new Logger(PLC.class);
    
    
    
    /** 物联设备XID */
    private String         plcXID;
    
    /** 数据报文XID */
    private String         datagramXID;
    
    /** 数据读写超时时长（单位：秒） */
    private Long           timeout;
    
    
    
    public PLC()
    {
        this.timeout = 10L;
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
        $Logger.debug("PLC写：开始" + Date.getNowTime().getTime());
        
        XPLC              v_XPLC      = (XPLC)              XJava.getObject(this.plcXID);
        PLCDatagramConfig v_XDatagram = (PLCDatagramConfig) XJava.getObject(this.datagramXID);
        boolean   v_Ret         = true;
        
        try
        {
            if ( v_XPLC == null )
            {
                $Logger.error("PlcXID[" + this.plcXID + "] is not exists.");
                v_Ret = false;
                return false;
            }
            
            if ( v_XDatagram == null )
            {
                $Logger.error("DatagramXID[" + this.datagramXID + "] is not exists.");
                v_Ret = false;
                return false;
            }
            
            synchronized ( v_XPLC )
            {
                if ( v_XPLC.getPlcConnect() == null )
                {
                    if ( !v_XPLC.connect() )
                    {
                        $Logger.error("PlcXID[" + this.plcXID + "] connect error.");
                        v_Ret = false;
                        return false;
                    }
                }
            }
            
            List<PLCDataItemConfig>       v_Items              = v_XDatagram.getItems();
            PlcWriteRequest.Builder v_PlcWriteReqBuilder = v_XPLC.getPlcConnect().writeRequestBuilder();
            int                     v_ItemCount          = 0;
            
            $Logger.info("PLC Write " + v_XPLC.getComment() + this.plcXID + "." + v_XDatagram.getComment() + this.datagramXID);
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
                
                $Logger.info("PLC Write " + v_Item.getName() + v_Item.getCode() + "：" + v_PLCTagAddress + "=" + v_DataItemValue);
                v_PlcWriteReqBuilder.addTagAddress(v_Item.getCode() ,v_PLCTagAddress ,v_DataItemValue);
                v_ItemCount++;
            }
            
            if ( v_ItemCount <= 0 )
            {
                v_Ret = false;
                $Logger.error("DatagramXID[" + this.datagramXID + "] itemCount is 0");
            }
            else if ( v_Ret )
            {
                PlcWriteRequest  v_PlcWriteRequest = v_PlcWriteReqBuilder.build();
                PlcWriteResponse v_Response        = v_PlcWriteRequest.execute().get(this.timeout ,TimeUnit.SECONDS);
                
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
                                    + "\n寄存器名：" + v_Item.getRegisterType().getValue()
                                    + "\n寄存编号：" + v_Item.getRegisterNo()
                                    + "\n偏移数量：" + v_Item.getRegisterOffset()
                                    + "\n数据类型：" + v_Item.getDataType().getValue());
                        v_Ret = false;
                        break;
                    }
                }
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        finally 
        {
            $Logger.debug("PLC写：结束" + Date.getNowTime().getTime());
        }
        
        return v_Ret;
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
        $Logger.debug("PLC读：开始" + Date.getNowTime().getTime());
        
        XPLC                v_XPLC      = (XPLC)              XJava.getObject(this.plcXID);
        PLCDatagramConfig   v_XDatagram = (PLCDatagramConfig) XJava.getObject(this.datagramXID);
        Map<String ,Object> v_Datas     = new LinkedHashMap<String ,Object>();
        
        try
        {
            if ( v_XPLC == null )
            {
                $Logger.error("PlcXID[" + this.plcXID + "] is not exists");
                return v_Datas;
            }
            
            if ( v_XDatagram == null )
            {
                $Logger.error("DatagramXID[" + this.datagramXID + "] is not exists");
                return v_Datas;
            }
            
            synchronized ( v_XPLC )
            {
                if ( v_XPLC.getPlcConnect() == null )
                {
                    if ( !v_XPLC.connect() )
                    {
                        $Logger.error("PlcXID[" + this.plcXID + "] connect error.");
                        return v_Datas;
                    }
                }
            }
            
            List<PLCDataItemConfig>      v_Items             = v_XDatagram.getItems();
            PlcReadRequest.Builder v_PLCReadReqBuilder = v_XPLC.getPlcConnect().readRequestBuilder();
            int                    v_ItemCount         = 0;
            
            $Logger.info("PLC Read " + Help.NVL(v_XPLC.getComment()) + this.plcXID + "." + Help.NVL(v_XDatagram.getComment()) + this.datagramXID);
            for (PLCDataItemConfig v_Item : v_Items)
            {
                String v_PLCTagAddress = v_Item.makePLCTagAddress();
                if ( Help.isNull(v_PLCTagAddress) )
                {
                    continue;
                }
                
                $Logger.info("PLC Read " + v_Item.getName() + v_Item.getCode() + "：" + v_PLCTagAddress);
                v_PLCReadReqBuilder.addTagAddress(v_Item.getCode() ,v_PLCTagAddress);
                v_ItemCount++;
            }
            
            if ( v_ItemCount <= 0 )
            {
                $Logger.error("DatagramXID[" + this.datagramXID + "] itemCount is 0");
                return v_Datas;
            }
            
            PlcReadRequest  v_PlcReadRequest  = v_PLCReadReqBuilder.build();
            PlcReadResponse v_PLCReadResponse = v_PlcReadRequest.execute().get(this.timeout ,TimeUnit.SECONDS);
            
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
                
                $Logger.info("PLC Read " + v_Item.getName() + v_Item.getCode() + "=" + v_DataValue);
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
            $Logger.error(exce);
        }
        finally 
        {
            $Logger.debug("PLC读：结束" + Date.getNowTime().getTime());
        }
        
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
     * 获取：数据读写超时时长（单位：秒）
     */
    public Long getTimeout()
    {
        return timeout;
    }

    
    /**
     * 设置：数据读写超时时长（单位：秒）
     * 
     * @param i_Timeout 数据读写超时时长（单位：秒）
     */
    public void setTimeout(Long i_Timeout)
    {
        this.timeout = i_Timeout;
    }
    
}
