package org.hy.common.plc.io;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.plc.data.PLCConfig;
import org.hy.common.plc.data.PLCDataItemConfig;
import org.hy.common.plc.data.PLCDatagramConfig;
import org.hy.common.plc.util.PLCAddress;
import org.hy.common.plc.util.PLCByteData;
import org.hy.common.xml.log.Logger;

import Moka7.S7Client;





/**
 * 统一多个组件的PLC连接、读、写等操作：S7-200 Smart的实现
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-19
 * @version     v1.0
 *              v1.1  2025-12-10  修正：自动重连机制的问题：在关闭close()时，将 plcConnect 赋值为空
 */
public class PlcIOS200 implements IPlcIO
{
    
    private static final Logger $Logger = new Logger(PlcIOS200.class);
    
    
    
    /** PLC设备配置 */
    private PLCConfig plcConfig;
    
    /** PLC连接对象 */
    private S7Client  plcConnect;
    
    
    
    public PlcIOS200(PLCConfig i_PLCConfig)
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
        return this.plcConnect;
    }
    
    
    
    /**
     * 写入数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @param i_Datagram  数据报文
     * @param i_Datas     数据集合
     * @param i_Timeout   数据读写超时时长（单位：秒）
     * @return
     */
    public boolean writeDatas(PLCDatagramConfig i_Datagram ,Map<String ,Object> i_Datas ,long i_Timeout)
    {
        $Logger.debug("PLC写：开始" + Date.getNowTime().getTime());
        
        boolean v_Ret = true;
        
        try
        {
            if ( i_Datagram == null )
            {
                $Logger.error("Datagram is null");
                v_Ret = false;
                return false;
            }
            
            synchronized ( this )
            {
                if ( this.plcConnect == null )
                {
                    if ( !this.connect() )
                    {
                        $Logger.error("PlcXID[" + this.plcConfig.getXid() + "] connect error.");
                        v_Ret = false;
                        return false;
                    }
                }
            }
            
            List<PLCDataItemConfig>        v_Items              = i_Datagram.getItems();
            int                            v_ItemCount          = 0;
            Map<String ,PLCDataItemConfig> v_PlcWriteReqBuilder = new LinkedHashMap<String ,PLCDataItemConfig>();
            
            $Logger.info("PLC Write " + this.plcConfig.getComment() + this.plcConfig.getXid() + "." + i_Datagram.getComment() + i_Datagram.getXid());
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
                v_PlcWriteReqBuilder.put(v_Item.getCode() ,v_Item);
                v_ItemCount++;
            }
            
            if ( v_ItemCount <= 0 )
            {
                v_Ret = false;
                $Logger.error("DatagramXID[" + i_Datagram.getXid() + "] itemCount is 0");
            }
            else if ( v_Ret )
            {
                for (Map.Entry<String ,PLCDataItemConfig> v_KeyValue : v_PlcWriteReqBuilder.entrySet())
                {
                    PLCDataItemConfig v_Item     = v_KeyValue.getValue();
                    PLCAddress        v_PA       = new PLCAddress(v_Item.getRegisterNo() ,v_Item.getRegisterOffset());
                    Object            v_Value    = Help.getValueIgnoreCase(i_Datas ,v_KeyValue.getKey());
                    byte []           v_ByteData = PLCByteData.setByteData(v_Item.getDataType() ,v_PA ,v_Value);
                    int               v_Result   = this.plcConnect.WriteArea(v_PA.getRegisterType().getS200() 
                                                                            ,v_PA.getRegisterNo()
                                                                            ,v_PA.getOffsetByte() 
                                                                            ,1
                                                                            ,v_ByteData);
                    if ( v_Result != 0 )
                    {
                        $Logger.error("写入PLC数据失败：" + v_KeyValue.getKey() + " " + v_Item.getName()
                                    + "\n异常编码：" + v_Result
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
            v_Ret = false;
            $Logger.error(exce);
            if ( this.plcConfig.getReconnect() >= 1 )
            {
                this.close();
            }
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
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @param i_Datagram  数据报文
     * @param i_Timeout   数据读写超时时长（单位：秒）
     * @return
     */
    public Map<String ,Object> readDatas(PLCDatagramConfig i_Datagram ,long i_Timeout)
    {
        $Logger.debug("PLC读：开始" + Date.getNowTime().getTime());
        
        Map<String ,Object> v_Datas = new LinkedHashMap<String ,Object>();
        
        try
        {
            if ( i_Datagram == null )
            {
                $Logger.error("Datagram is null");
                return v_Datas;
            }
            
            synchronized ( this )
            {
                if ( this.plcConnect == null )
                {
                    if ( !this.connect() )
                    {
                        $Logger.error("PlcXID[" + this.plcConfig.getXid() + "] connect error.");
                        return v_Datas;
                    }
                }
            }
            
            List<PLCDataItemConfig>        v_Items             = i_Datagram.getItems();
            int                            v_ItemCount         = 0;
            Map<String ,PLCDataItemConfig> v_PLCReadReqBuilder = new LinkedHashMap<String ,PLCDataItemConfig>();
            
            $Logger.info("PLC Read " + Help.NVL(this.plcConfig.getComment()) + this.plcConfig.getXid() + "." + Help.NVL(i_Datagram.getComment()) + i_Datagram.getXid());
            for (PLCDataItemConfig v_Item : v_Items)
            {
                String v_PLCTagAddress = v_Item.makePLCTagAddress();
                if ( Help.isNull(v_PLCTagAddress) )
                {
                    continue;
                }
                
                $Logger.info("PLC Read " + v_Item.getName() + v_Item.getCode() + "：" + v_PLCTagAddress);
                v_PLCReadReqBuilder.put(v_Item.getCode() ,v_Item);
                v_ItemCount++;
            }
            
            if ( v_ItemCount <= 0 )
            {
                $Logger.error("DatagramXID[" + i_Datagram.getXid() + "] itemCount is 0");
                return v_Datas;
            }
            
            for (Map.Entry<String ,PLCDataItemConfig> v_KeyValue : v_PLCReadReqBuilder.entrySet())
            {
                PLCDataItemConfig v_Item     = v_KeyValue.getValue();
                PLCAddress        v_PA       = new PLCAddress(v_Item.getRegisterNo() ,v_Item.getRegisterOffset());
                byte []           v_ByteData = PLCByteData.newByte(v_Item.getDataType());
                int               v_Result   = this.plcConnect.ReadArea(v_PA.getRegisterType().getS200() 
                                                                       ,v_PA.getRegisterNo()
                                                                       ,v_PA.getOffsetByte()
                                                                       ,1
                                                                       ,v_ByteData); 
                if ( v_Result != 0 )
                {
                    $Logger.error("读取PLC数据失败：" + v_Item.getCode() + " " + v_Item.getName() 
                                + "\n异常编码：" + v_Result
                                + "\n寄存器名：" + v_Item.getRegisterType().getValue()
                                + "\n寄存编号：" + v_Item.getRegisterNo()
                                + "\n偏移数量：" + v_Item.getRegisterOffset()
                                + "\n数据类型：" + v_Item.getDataType().getValue());
                    break;
                }
                
                Object v_DataValue = PLCByteData.getByteData(v_Item.getDataType() ,v_PA ,v_ByteData);
                if ( v_DataValue != null )
                {
                    v_Datas.put(v_Item.getCode() ,v_DataValue);
                }
                
                $Logger.info("PLC Read " + v_Item.getName() + v_Item.getCode() + "=" + v_DataValue);
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            if ( this.plcConfig.getReconnect() >= 1 )
            {
                this.close();
            }
        }
        finally 
        {
            $Logger.debug("PLC读：结束" + Date.getNowTime().getTime());
        }
        
        return v_Datas;
    }
    
    
    
    /**
     * 连接物联设备
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     */
    public synchronized boolean connect()
    {
        // 创建S7客户端实例
        this.plcConnect = new S7Client();
        
        // PLC机架号、槽号(S7-200 Smart通常为0,1)
        Integer v_Rack = Help.NVL(this.plcConfig.getRack() ,0);
        Integer v_Slot = Help.NVL(this.plcConfig.getSlot() ,1);
        
        // 连接到PLC
        int v_Result = this.plcConnect.ConnectTo(this.plcConfig.getHost() ,v_Rack ,v_Slot);
        if ( v_Result == 0 )
        {
            return true;
        }
        else
        {
            $Logger.error("PLC[" + this.plcConfig.getXid() + "] connection error.");
            return false;
        }
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
        if ( this.plcConnect == null )
        {
            return false;
        }
        else
        {
            return this.plcConnect.Connected;
        }
    }
    
    
    
    /**
     * 关闭连接
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     */
    public synchronized void close()
    {
        if ( this.plcConnect == null )
        {
            return;
        }
        
        try
        {
            this.plcConnect.Disconnect();
            this.plcConnect = null;
        }
        catch (Exception exce)
        {
            $Logger.error(this.plcConfig.getXid() + " 连接关闭时异常" ,exce);
        }
    }
    
}
