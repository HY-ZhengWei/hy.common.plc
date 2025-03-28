package org.hy.common.plc.callflow;

import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.NodeConfigBase;
import org.hy.common.db.DBSQL;
import org.hy.common.plc.PLC;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 读PLC元素：获取PLC数据的配置
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-07
 * @version     v1.0
 */
public class IoTGetConfig extends NodeConfig implements NodeConfigBase
{
    
    private static final Logger $Logger      = new Logger(IoTGetConfig.class);
    
    private static final String $ElementType = "xiotget";
    
    
    
    /** 物联设备XID的变量名称或值 */
    private String deviceXID;
    
    /** 物联设备PLC */
    private PLC    callObject;
    
    
    
    static 
    {
        CallFlow.getHelpExport().addImportHead($ElementType ,IoTGetConfig.class);
    }
    
    
    
    public IoTGetConfig()
    {
        String v_CallObjectXID = "XIoTGet" + StringHelp.getUUID9n();
        this.callObject = new PLC();
        
        XJava.putObject(v_CallObjectXID ,this.callObject);
        this.setCallXID(v_CallObjectXID);
        this.setCallMethod("readDatas");
    }
    
    
    
    /**
     * 元素的类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     *
     * @return
     */
    public String getElementType()
    {
        return "IOTGET";
    }
    
    
    
    /**
     * 获取：物联设备XID
     */
    public String getDeviceXID()
    {
        return this.deviceXID;
    }


    
    /**
     * 设置：物联设备XID
     * 
     * @param i_DeviceXID 物联设备XID
     */
    public void setDeviceXID(String i_DeviceXID)
    {
        this.deviceXID = ValueHelp.standardRefID(i_DeviceXID);
    }



    /**
     * 获取：数据报文XID
     */
    public String getDatagramXID()
    {
        return DBSQL.$Placeholder + this.callObject.getDatagramXID();
    }


    
    /**
     * 设置：数据报文XID
     * 
     * @param i_DatagramXID 数据报文XID
     */
    public void setDatagramXID(String i_DatagramXID)
    {
        this.callObject.setDatagramXID(ValueHelp.standardValueID(i_DatagramXID));
    }
    
    
    
    /**
     * 执行方法前对方法入参的处理、加工、合成
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-11
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteReturn  执行结果。已用NodeConfig自己的力量获取了执行结果。
     * @return
     */
    public Object [] generateParams(Map<String ,Object> io_Context ,Object [] io_Params)
    {
        String v_DeviceXID = null;
        try
        {
            v_DeviceXID = (String) ValueHelp.getValue(this.deviceXID ,String.class ,null ,io_Context);
            this.callObject.setPlcXID(v_DeviceXID);
            return io_Params;
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            throw new RuntimeException(exce);
        }
    }



    /**
     * 获取XML内容中的名称，如<名称>内容</名称>
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     *
     * @return
     */
    public String toXmlName()
    {
        return $ElementType;
    }
    
    
    
    /**
     * 生成或写入个性化的XML内容
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     *
     * @param io_Xml         XML内容的缓存区
     * @param i_Level        层级。最小下标从0开始。
     *                           0表示每行前面有0个空格；
     *                           1表示每行前面有4个空格；
     *                           2表示每行前面有8个空格；
     * @param i_Level1       单级层级的空格间隔
     * @param i_LevelN       N级层级的空格间隔
     * @param i_SuperTreeID  父级树ID
     * @param i_TreeID       当前树ID
     */
    public void toXmlContent(StringBuilder io_Xml ,int i_Level ,String i_Level1 ,String i_LevelN ,String i_SuperTreeID ,String i_TreeID)
    {
        if ( !Help.isNull(this.callObject.getPlcXID()) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("deviceXID" ,this.getDeviceXID()));
        }
        if ( !Help.isNull(this.callObject.getDatagramXID()) )
        {
            io_Xml.append("\n").append(i_LevelN).append(i_Level1).append(IToXml.toValue("datagramXID" ,this.getDatagramXID()));
        }
    }
    
    
    
    /**
     * 解析为实时运行时的执行表达式
     * 
     * 注：禁止在此真的执行方法
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    public String toString(Map<String ,Object> i_Context)
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.returnID) )
        {
            v_Builder.append(DBSQL.$Placeholder).append(this.returnID).append(" = ");
        }
        
        if ( Help.isNull(this.deviceXID) )
        {
            v_Builder.append("?");
        }
        else
        {
            try
            {
                String v_DeviceXID = (String) ValueHelp.getValue(this.deviceXID ,String.class ,null ,i_Context);
                if ( XJava.getObject(v_DeviceXID) != null )
                {
                    v_Builder.append(v_DeviceXID);
                }
                else
                {
                    v_Builder.append("[NULL]");
                }
            }
            catch (Exception exce)
            {
                $Logger.error(exce);
                v_Builder.append("[ERROR]");
            }
        }
        
        v_Builder.append(".");
        
        if ( Help.isNull(this.callObject.getDatagramXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            if ( XJava.getObject(this.callObject.getDatagramXID()) != null )
            {
                v_Builder.append(this.getDatagramXID());
            }
            else
            {
                v_Builder.append("[NULL]");
            }
        }
        
        v_Builder.append(".readDatas");
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 解析为执行表达式
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        
        if ( !Help.isNull(this.returnID) )
        {
            v_Builder.append(DBSQL.$Placeholder).append(this.returnID).append(" = ");
        }
        
        if ( Help.isNull(this.callObject.getPlcXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.getDeviceXID());
        }
        
        v_Builder.append(".");
        
        if ( Help.isNull(this.callObject.getDatagramXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.getDatagramXID());
        }
        
        v_Builder.append(".readDatas");
        
        return v_Builder.toString();
    }
    
}
