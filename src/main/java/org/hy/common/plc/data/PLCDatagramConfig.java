package org.hy.common.plc.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.XJavaID;





/**
 * 数据报文
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-05-15
 * @version     v1.0
 */
public class PLCDatagramConfig implements XJavaID
{
    
    /** 逻辑ID */
    private String                         xid;
    
    /** 数据项集合 */
    private List<PLCDataItemConfig>        items;
    
    /** 数据项集合（仅内部使用）。Map.key为数据项的code */
    private Map<String ,PLCDataItemConfig> itemMap;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String                         comment;
    
    /** 外界自定义的配置信息 */
    private Object                         config;
    
    
    
    public PLCDatagramConfig()
    {
        this.items = new ArrayList<PLCDataItemConfig>();
    }
    
    
    /**
     * 获取：逻辑ID。
     */
    public String getXid()
    {
        return xid;
    }

    
    /**
     * 设置：逻辑ID。
     * 
     * @param i_Xid 逻辑ID。
     */
    public void setXid(String i_Xid)
    {
        this.xid = i_Xid;
    }

    
    /**
     * 获取：数据项集合
     */
    public List<PLCDataItemConfig> getItems()
    {
        return items;
    }

    
    /**
     * 设置：数据项集合
     * 
     * @param i_DataItems 数据项集合
     */
    public void setItems(List<PLCDataItemConfig> i_DataItems)
    {
        this.items = i_DataItems;
    }

    
    /**
     * 获取：数据项集合（仅内部使用）。Map.key为数据项的code
     */
    public Map<String ,PLCDataItemConfig> gatItemMap()
    {
        return itemMap;
    }


    /**
     * 设置：数据项集合（仅内部使用）。Map.key为数据项的code
     * 
     * @param i_ItemMap 数据项集合（仅内部使用）
     */
    public void satItemMap(Map<String ,PLCDataItemConfig> i_ItemMap)
    {
        this.itemMap = i_ItemMap;
    }
    
    
    /**
     * 获取：外界自定义的配置信息
     */
    public Object getConfig()
    {
        return config;
    }

    
    /**
     * 设置：外界自定义的配置信息
     * 
     * @param i_Config 外界自定义的配置信息
     */
    public void setConfig(Object i_Config)
    {
        this.config = i_Config;
    }


    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    @Override
    public void setXJavaID(String i_XJavaID)
    {
        this.xid = i_XJavaID;
    }
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    @Override
    public String getXJavaID()
    {
        return this.xid;
    }
    
    
    /**
     * 获取：注释。可用于日志的输出等帮助性的信息
     */
    @Override
    public String getComment()
    {
        return comment;
    }

    
    /**
     * 设置：注释。可用于日志的输出等帮助性的信息
     * 
     * @param i_Comment 注释。可用于日志的输出等帮助性的信息
     */
    @Override
    public void setComment(String i_Comment)
    {
        this.comment = i_Comment;
    }

}
