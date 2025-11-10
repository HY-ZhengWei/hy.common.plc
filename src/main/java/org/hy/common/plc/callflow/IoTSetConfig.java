package org.hy.common.plc.callflow;

import java.util.HashMap;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.TablePartitionLink;
import org.hy.common.callflow.CallFlow;
import org.hy.common.callflow.common.ValueHelp;
import org.hy.common.callflow.execute.ExecuteElement;
import org.hy.common.callflow.execute.ExecuteResult;
import org.hy.common.callflow.file.IToXml;
import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.callflow.node.NodeConfigBase;
import org.hy.common.callflow.node.NodeParam;
import org.hy.common.db.DBSQL;
import org.hy.common.plc.PLC;
import org.hy.common.plc.data.PLCConfig;
import org.hy.common.plc.data.PLCDatagramConfig;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;





/**
 * 写PLC元素：发送PLC数据的配置
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-07
 * @version     v1.0
 *              v2.0  2025-09-26  添加：特性化的静态检查
 *                                修正：执行结果false时表示异常
 *              v3.0  2025-10-21  添加：物联设备XID支持更多的占位符格式
 *              v3.1  2025-11-04  添加：数据报文XID支持更多的占位符格式
 *                                优化：去除PLC属性，改为动态创建，以支持并发操作。
 */
public class IoTSetConfig extends NodeConfig implements NodeConfigBase
{
    
    private static final Logger $Logger      = new Logger(IoTSetConfig.class);
    
    private static final String $ElementType = "xiotset";
    
    
    
    /** 物联设备XID的变量名称或值 */
    private String                        deviceXID;
    
    /** 物联设备XID，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> deviceXIDPlaceholders;
    
    /** 数据报文XID */
    private String                        datagramXID;
    
    /** 数据报文XID，已解释完成的占位符（性能有优化，仅内部使用） */
    private PartitionMap<String ,Integer> datagramXIDPlaceholders;
    
    /** 物联参数数据 */
    private NodeParam                     callParam;
    
    
    
    static
    {
        CallFlow.getHelpExport().addImportHead($ElementType ,IoTSetConfig.class);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-03-07
     * @version     v1.0
     *
     */
    public IoTSetConfig()
    {
        this(0L ,0L);
    }
    
    
    
    /**
     * 构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @param i_RequestTotal  累计的执行次数
     * @param i_SuccessTotal  累计的执行成功次数
     */
    public IoTSetConfig(long i_RequestTotal ,long i_SuccessTotal)
    {
        super(i_RequestTotal ,i_SuccessTotal);
        
        this.setCallMethod("executeIoT");
        
        NodeParam v_CallParamDeviceXID = new NodeParam();
        v_CallParamDeviceXID.setValueClass(String.class.getName());
        v_CallParamDeviceXID.setValue("");
        this.setCallParam(v_CallParamDeviceXID);
        
        NodeParam v_CallParamDatagramXID = new NodeParam();
        v_CallParamDatagramXID.setValueClass(String.class.getName());
        v_CallParamDatagramXID.setValue("");
        this.setCallParam(v_CallParamDatagramXID);
        
        this.callParam = new NodeParam();
        this.callParam.setValueClass(Map.class.getName());
        this.setCallParam(this.callParam);
        this.setRetFalseIsError(true);
    }
    
    
    
    /**
     * 静态检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-26
     * @version     v1.0
     *
     * @param io_Result     表示检测结果
     * @return
     */
    public boolean check(Return<Object> io_Result)
    {
        if ( !super.check(io_Result) )
        {
            return false;
        }
        
        if ( Help.isNull(this.getDeviceXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].deviceXID is null.");
            return false;
        }
        if ( Help.isNull(this.getDatagramXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].datagramXID is null.");
            return false;
        }
        if ( Help.isNull(this.getDataXID()) )
        {
            io_Result.set(false).setParamStr("CFlowCheck：" + this.getClass().getSimpleName() + "[" + Help.NVL(this.getXid()) + "].dataXID is null.");
            return false;
        }
        
        return true;
    }
    
    
    
    /**
     * 运行时中获取模拟数据。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-07
     * @version     v1.0
     *
     * @param io_Context   上下文类型的变量信息
     * @param i_BeginTime  编排元素的开始时间
     * @param io_Result    编排元素的执行结果
     * @return             表示是否有模拟数据
     */
    public boolean mock(Map<String ,Object> io_Context ,long i_BeginTime ,ExecuteResult io_Result) 
    {
        return super.mock(io_Context ,i_BeginTime ,io_Result ,null ,Boolean.class.getName());
    }
    
    
    
    /**
     * 当用户没有设置XID时，可使用此方法生成
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-10-21
     * @version     v1.0
     *
     * @return
     */
    public String makeXID()
    {
        return "XIOTS_" + StringHelp.getUUID9n();
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
        return "IOTSET";
    }
    
    
    
    /**
     * 获取：物联设备XID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-03
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    private String getDeviceXID(Map<String ,Object> i_Context)
    {
        try
        {
            Object v_Value = ValueHelp.getValueReplace(this.deviceXID ,this.deviceXIDPlaceholders ,null ,null ,i_Context);
            if ( v_Value instanceof String )
            {
                String v_DeviceXID = (String) v_Value;
                if ( XJava.getObject(v_DeviceXID) == null )
                {
                    throw new NullPointerException(this.getXid() + "." + this.deviceXID + "[" + v_DeviceXID + "] is not exists.");
                }
                return v_DeviceXID;
            }
            else if ( v_Value instanceof PLCConfig )
            {
                PLCConfig v_Device = (PLCConfig) v_Value;
                return v_Device.getXid();
            }
            else
            {
                throw new NullPointerException(this.getXid() + "." + this.deviceXID + " is not exists.");
            }
        }
        catch (Exception exce)
        {
            String v_Msg = this.getXid() + " 物联设备[" + this.deviceXID + "] 获取运行实例异常";
            $Logger.error(v_Msg ,exce);
            throw new RuntimeException(v_Msg ,exce);
        }
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
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_DeviceXID) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_DeviceXID ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.deviceXIDPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.deviceXIDPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.deviceXID = i_DeviceXID.trim();
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：数据报文XID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-03
     * @version     v1.0
     *
     * @param i_Context  上下文类型的变量信息
     * @return
     */
    private String getDatagramXID(Map<String ,Object> i_Context)
    {
        try
        {
            Object v_Value = ValueHelp.getValueReplace(this.datagramXID ,this.datagramXIDPlaceholders ,null ,null ,i_Context);
            if ( v_Value instanceof String )
            {
                String v_DatagramXID = (String) v_Value;
                if ( XJava.getObject(v_DatagramXID) == null )
                {
                    throw new NullPointerException(this.getXid() + "." + this.datagramXID + "[" + v_DatagramXID + "] is not exists.");
                }
                return v_DatagramXID;
            }
            else if ( v_Value instanceof PLCDatagramConfig )
            {
                PLCDatagramConfig v_Datagram = (PLCDatagramConfig) v_Value;
                return v_Datagram.getXid();
            }
            else
            {
                throw new NullPointerException(this.getXid() + "." + this.datagramXID + " is not exists.");
            }
        }
        catch (Exception exce)
        {
            String v_Msg = this.getXid() + " 物联报文[" + this.datagramXID + "]获取运行实例异常";
            $Logger.error(v_Msg ,exce);
            throw new RuntimeException(v_Msg ,exce);
        }
    }



    /**
     * 获取：数据报文XID
     */
    public String getDatagramXID()
    {
        return this.datagramXID;
    }
    
    
    
    /**
     * 设置：数据报文XID
     * 
     * @param i_DatagramXID 数据报文XID
     */
    public void setDatagramXID(String i_DatagramXID)
    {
        PartitionMap<String ,Integer> v_PlaceholdersOrg = null;
        if ( !Help.isNull(i_DatagramXID) )
        {
            v_PlaceholdersOrg = StringHelp.parsePlaceholdersSequence(DBSQL.$Placeholder ,i_DatagramXID ,true);
        }
        
        if ( !Help.isNull(v_PlaceholdersOrg) )
        {
            this.datagramXIDPlaceholders = Help.toReverse(v_PlaceholdersOrg);
            v_PlaceholdersOrg.clear();
            v_PlaceholdersOrg = null;
        }
        else
        {
            this.datagramXIDPlaceholders = new TablePartitionLink<String ,Integer>();
        }
        this.datagramXID = i_DatagramXID.trim();
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }


    
    /**
     * 获取：物联参数数据
     */
    public String getDataXID()
    {
        return this.callParam.getValue();
    }

    
    
    /**
     * 设置：物联参数数据
     * 
     * @param i_DataXID 物联参数数据
     */
    public void setDataXID(String i_DataXID)
    {
        this.callParam.setValue(i_DataXID);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 获取：物联参数数据的默认值
     */
    public String getDataDefault()
    {
        return this.callParam.getValueDefault();
    }
    
    
    
    /**
     * 设置：物联参数数据的默认值
     *
     * @param i_DataDefault  物联参数数据的默认值
     */
    public void setDataDefault(String i_DataDefault)
    {
        this.callParam.setValueDefault(i_DataDefault);
        this.reset(this.getRequestTotal() ,this.getSuccessTotal());
        this.keyChange();
    }
    
    
    
    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * 自己反射调用自己的实例中的方法
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_Xid)
    {
        super.setXJavaID(i_Xid);
        this.setCallXID(this.getXid());
    }
    
    
    
    /**
     * 执行方法前，对执行对象的处理
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-04
     * @version     v1.0
     *
     * @param io_Context        上下文类型的变量信息
     * @param io_ExecuteObject  执行对象。已用NodeConfig自己的力量生成了执行对象。
     * @return
     */
    public Object generateObject(Map<String ,Object> io_Context ,Object io_ExecuteObject)
    {
        // 其实就是返回自己。io_ExecuteObject 获取正确时，也是this自己
        return io_ExecuteObject == null ? this : io_ExecuteObject;
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
        String v_DeviceXID   = this.getDeviceXID(  io_Context);
        String v_DatagramXID = this.getDatagramXID(io_Context);
        
        io_Params[0] = v_DeviceXID;
        io_Params[1] = v_DatagramXID;
        
        return io_Params;
    }
    
    
    
    /**
     * 执行IoT写
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-11-04
     * @version     v1.0
     *
     * @param i_DeviceXID    物联设备XID
     * @param i_DatagramXID  数据报文XID
     * @param i_Datas        物联参数数据。对应本类中的dataXID
     * @return
     */
    public boolean executeIoT(String i_DeviceXID ,String i_DatagramXID ,Map<String ,Object> i_Datas)
    {
        PLC v_PLC = new PLC(i_DeviceXID ,i_DatagramXID);
        return v_PLC.writeDatas(i_Datas);
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
     * 转XML时是否显示retFalseIsError属性
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-09-25
     * @version     v1.0
     *
     * @return
     */
    public boolean xmlShowRetFalseIsError()
    {
        return false;
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
        String v_NewSpace = "\n" + i_LevelN + i_Level1;
        
        if ( !Help.isNull(this.deviceXID) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("deviceXID" ,this.deviceXID));
        }
        if ( !Help.isNull(this.getDatagramXID()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("datagramXID" ,this.getDatagramXID()));
        }
        if ( !Help.isNull(this.getDataXID()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("dataXID" ,this.getDataXID() ,v_NewSpace));
        }
        if ( !Help.isNull(this.getDataDefault()) )
        {
            io_Xml.append(v_NewSpace).append(IToXml.toValue("dataDefault" ,this.getDataDefault() ,v_NewSpace));
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
        
        String v_DeviceXID = null;
        try
        {
            v_DeviceXID = this.getDeviceXID(i_Context);
        }
        catch (Exception exce)
        {
            // Nothing. 已在上面的方法内输出异常日志
        }
        if ( Help.isNull(v_DeviceXID) )
        {
            v_Builder.append("?");
        }
        else
        {
            if ( XJava.getObject(v_DeviceXID) != null )
            {
                v_Builder.append(v_DeviceXID);
            }
            else
            {
                v_Builder.append("[NULL]");
            }
        }
        
        v_Builder.append(".");
        
        String v_DatagramXID = null;
        try
        {
            v_DatagramXID = this.getDatagramXID(i_Context);
        }
        catch (Exception exce)
        {
            // Nothing. 已在上面的方法内输出异常日志
        }
        if ( Help.isNull(v_DatagramXID) )
        {
            v_Builder.append("?");
        }
        else
        {
            if ( XJava.getObject(v_DatagramXID) != null )
            {
                v_Builder.append(v_DatagramXID);
            }
            else
            {
                v_Builder.append("[NULL]");
            }
        }
        
        v_Builder.append(".writeDatas");
        
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
        
        if ( Help.isNull(this.deviceXID) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.getDeviceXID());
        }
        
        v_Builder.append(".");
        
        if ( Help.isNull(this.getDatagramXID()) )
        {
            v_Builder.append("?");
        }
        else
        {
            v_Builder.append(this.getDatagramXID());
        }
        
        v_Builder.append(".writeDatas");
        
        return v_Builder.toString();
    }
    
    
    
    /**
     * 仅仅创建一个新的实例，没有任何赋值
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @return
     */
    public Object newMy()
    {
        return new IoTSetConfig();
    }
    
    
    
    /**
     * 浅克隆，只克隆自己，不克隆路由。
     * 
     * 注：不克隆XID。
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     */
    public Object cloneMyOnly()
    {
        IoTSetConfig v_Clone = new IoTSetConfig();
        
        this.cloneMyOnly(v_Clone);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.setTimeout(this.getTimeout());
        v_Clone.deviceXID   = this.deviceXID;
        v_Clone.datagramXID = this.datagramXID;
        v_Clone.setDataXID(this.getDataXID());
        
        return v_Clone;
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @param io_Clone        克隆的复制品对象
     * @param i_ReplaceXID    要被替换掉的XID中的关键字（可为空）
     * @param i_ReplaceByXID  新的XID内容，替换为的内容（可为空）
     * @param i_AppendXID     替换后，在XID尾追加的内容（可为空）
     * @param io_XIDObjects   已实例化的XID对象。Map.key为XID值
     * @return
     */
    public void clone(Object io_Clone ,String i_ReplaceXID ,String i_ReplaceByXID ,String i_AppendXID ,Map<String ,ExecuteElement> io_XIDObjects)
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone IoTSetConfig xid is null.");
        }
        
        IoTSetConfig v_Clone = (IoTSetConfig) io_Clone;
        ((ExecuteElement) this).clone(v_Clone ,i_ReplaceXID ,i_ReplaceByXID ,i_AppendXID ,io_XIDObjects);
        
        // v_Clone.callXID  = this.callXID;     不能克隆callXID，因为它就是类自己的xid
        v_Clone.setTimeout(this.getTimeout());
        v_Clone.deviceXID   = this.deviceXID;
        v_Clone.datagramXID = this.datagramXID;
        v_Clone.setDataXID(this.getDataXID());
    }
    
    
    
    /**
     * 深度克隆编排元素
     * 
     * 建议：子类重写此方法
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-04-02
     * @version     v1.0
     *
     * @return
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        if ( Help.isNull(this.xid) )
        {
            throw new NullPointerException("Clone IoTSetConfig xid is null.");
        }
        
        Map<String ,ExecuteElement> v_XIDObjects = new HashMap<String ,ExecuteElement>();
        Return<String>              v_Version    = parserXIDVersion(this.xid);
        IoTSetConfig                v_Clone      = new IoTSetConfig();
        
        if ( v_Version.booleanValue() )
        {
            this.clone(v_Clone ,v_Version.getParamStr() ,XIDVersion + (v_Version.getParamInt() + 1) ,""         ,v_XIDObjects);
        }
        else
        {
            this.clone(v_Clone ,""                      ,""                                         ,XIDVersion ,v_XIDObjects);
        }
        
        v_XIDObjects.clear();
        v_XIDObjects = null;
        return v_Clone;
    }
    
}
