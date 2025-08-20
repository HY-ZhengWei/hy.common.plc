package org.hy.common.plc.enums;

import Moka7.S7;





/**
 * 寄存器类型
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-05-14
 * @version     v1.0
 *              v2.0  2025-08-19  添加：S7-200 Smart对应的常量值
 */
public enum PLCRegisterType
{
    
    Input ("RI" ,"I"  ,"输入寄存器" ,S7.S7AreaPE),
    
    Output("RQ" ,"Q"  ,"输出寄存器" ,S7.S7AreaPA),
    
    Memory("RM" ,"M"  ,"内存寄存器" ,S7.S7AreaMK),
    
    Data(  "RD" ,"DB" ,"数据区块类" ,S7.S7AreaDB),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** PLC编码 */
    private String  code;
    
    /** 描述 */
    private String  comment;
    
    /** S7-200 Smart对应的常量值 */
    private int     s200;
    
    
    
    /**
     * 数值转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-02-11
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static PLCRegisterType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (PLCRegisterType v_Enum : PLCRegisterType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    /**
     * PLC编码转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-03-25
     * @version     v1.0
     *
     * @param i_Code
     * @return
     */
    public static PLCRegisterType getCode(String i_Code)
    {
        if ( i_Code == null )
        {
            return null;
        }
        
        String v_Code = i_Code.trim();
        for (PLCRegisterType v_Enum : PLCRegisterType.values())
        {
            if ( v_Enum.code.equalsIgnoreCase(v_Code) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    PLCRegisterType(String i_Value ,String i_Code ,String i_Comment ,int i_S200)
    {
        this.value   = i_Value;
        this.code    = i_Code;
        this.comment = i_Comment;
        this.s200    = i_S200;
    }

    
    
    public String getValue()
    {
        return this.value;
    }
    
    
    
    public String getCode()
    {
        return code;
    }



    public String getComment()
    {
        return this.comment;
    }
    
    
    
    /**
     * 获取：S7-200 Smart对应的常量值
     */
    public int getS200()
    {
        return s200;
    }


    public String toString()
    {
        return this.value + "";
    }
    
}
