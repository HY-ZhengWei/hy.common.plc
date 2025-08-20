package org.hy.common.plc.enums;





/**
 * PLC协议类型
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-20
 * @version     v1.0
 */
public enum PLCProtocolType
{
    
    S7          ("S7"           ,"支持S7-300、S7-400、S7-1200、S7-1500"),   // PROFINET网络
                                 
    S7_200_Smart("S7-200-SMART" ,"支持S7-200 SMART"),
                                 
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** 描述 */
    private String  comment;
    
    
    
    /**
     * 数值转为常量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-20
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static PLCProtocolType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (PLCProtocolType v_Enum : PLCProtocolType.values())
        {
            if ( v_Enum.value.equalsIgnoreCase(v_Value) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    PLCProtocolType(String i_Value ,String i_Comment)
    {
        this.value   = i_Value;
        this.comment = i_Comment;
    }

    
    
    public String getValue()
    {
        return this.value;
    }
    
    
    
    public String getComment()
    {
        return this.comment;
    }
    
    

    public String toString()
    {
        return this.value + "";
    }
    
}
