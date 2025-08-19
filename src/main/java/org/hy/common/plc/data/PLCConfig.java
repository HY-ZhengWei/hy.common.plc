package org.hy.common.plc.data;

import org.hy.common.XJavaID;





/**
 * PLC(Programmable Logic Controller)可编程逻辑控制器的配置
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-05-11
 * @version     v1.0
 */
public class PLCConfig implements XJavaID
{
    
    /** 默认协议 */
    public static final String $Protocol         = "s7";
    
    /** Smart200协议 */
    public static final String $ProtocolSmart200 = "s7 Smart200";
    
    /** 默认端口 */
    public static final int    $Port             = 102;
    
    /** 默认超时时长。单位：毫秒 */
    public static final int    $Timeout          = 5000;
    
    
    
    /** 逻辑ID */
    private String        xid;
    
    /** 注释。可用于日志的输出等帮助性的信息 */
    private String        comment;
    
    /** IP地址 */
    private String        host;
    
    /** 访问端口 */
    private Integer       port;
    
    /** 协议类型 */
    private String        protocol;
    
    /** 逻辑机架编号。从0开始编号 */
    private Integer       rack;
    
    /** 槽位。从1开始编号 */
    private Integer       slot;
    
    /** 超时时长。单位：毫秒 */
    private Integer       timeout;
    
    /** 连接用户名称 */
    private String        userName;
    
    /** 连接访问密码 */
    private String        userPassword;
    
    
    
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
     * 获取：IP地址
     */
    public String getHost()
    {
        return host;
    }

    
    /**
     * 设置：IP地址
     * 
     * @param i_Host IP地址
     */
    public void setHost(String i_Host)
    {
        this.host = i_Host;
    }

    
    /**
     * 获取：访问端口
     */
    public Integer getPort()
    {
        return port;
    }

    
    /**
     * 设置：访问端口
     * 
     * @param i_Port 访问端口
     */
    public void setPort(Integer i_Port)
    {
        this.port = i_Port;
    }

    
    /**
     * 获取：协议类型
     */
    public String getProtocol()
    {
        return protocol;
    }

    
    /**
     * 设置：协议类型
     * 
     * @param i_Protocol 协议类型
     */
    public void setProtocol(String i_Protocol)
    {
        this.protocol = i_Protocol;
    }

    
    /**
     * 获取：逻辑机架编号。从0开始编号
     */
    public Integer getRack()
    {
        return rack;
    }

    
    /**
     * 设置：逻辑机架编号。从0开始编号
     * 
     * @param i_Rack 逻辑机架编号。从0开始编号
     */
    public void setRack(Integer i_Rack)
    {
        this.rack = i_Rack;
    }

    
    /**
     * 获取：槽位。从1开始编号
     */
    public Integer getSlot()
    {
        return slot;
    }

    
    /**
     * 设置：槽位。从1开始编号
     * 
     * @param i_Slot 槽位。从1开始编号
     */
    public void setSlot(Integer i_Slot)
    {
        this.slot = i_Slot;
    }


    /**
     * 获取：超时时长。单位：毫秒
     */
    public Integer getTimeout()
    {
        return timeout;
    }

    
    /**
     * 设置：超时时长。单位：毫秒
     * 
     * @param i_Timeout 超时时长。单位：毫秒
     */
    public void setTimeout(Integer i_Timeout)
    {
        this.timeout = i_Timeout;
    }

    
    /**
     * 获取：连接用户名称
     */
    public String getUserName()
    {
        return userName;
    }

    
    /**
     * 设置：连接用户名称
     * 
     * @param i_UserName 连接用户名称
     */
    public void setUserName(String i_UserName)
    {
        this.userName = i_UserName;
    }

    
    /**
     * 获取：连接访问密码
     */
    public String getUserPassword()
    {
        return userPassword;
    }


    /**
     * 设置：连接访问密码
     * 
     * @param i_UserPassword 连接访问密码
     */
    public void setUserPassword(String i_UserPassword)
    {
        this.userPassword = i_UserPassword;
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
