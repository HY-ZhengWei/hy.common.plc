package org.hy.common.plc.enums;





/**
 * 寄存器的数据类型
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-05-14
 * @version     v1.0
 */
public enum PLCDataType
{
    
    Bool   ("DBool"   ,"BOOL"    ,"布尔"),
                                 
    Byte   ("DByte"   ,"BYTE"    ,"字节"),
                                 
    Word   ("DWord"   ,"WORD"    ,"字"),
                                 
    DWord  ("DDWord"  ,"DWORD"   ,"双字"),
                                 
    SInt   ("DSInt"   ,"SINT"    ,"8位有符号整数"),
                                 
    USInt  ("DUSInt"  ,"USINT"   ,"8位无符号的整数"),
                                 
    Int    ("DInt"    ,"INT"     ,"16位有符号整数"),
                                 
    DInt   ("DDInt"   ,"DINT"    ,"32位有符号整数"),
                                 
    UDInt  ("DUDInt"  ,"UDINT"   ,"32位无符号整数"),
                                 
    Real   ("DReal"   ,"REAL"    ,"32位浮点"),
                                 
    LReal  ("DLReal"  ,"LREAL"   ,"64位双精度浮点"),
    
    String ("DString" ,"STRING"  ,"字符串Ascii"),
    
    WString("DString" ,"WSTRING" ,"字符串Unicode"),
    
    ;
    
    
    
    /** 值 */
    private String  value;
    
    /** PLC编码 */
    private String  code;
    
    /** 描述 */
    private String  comment;
    
    
    
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
    public static PLCDataType get(String i_Value)
    {
        if ( i_Value == null )
        {
            return null;
        }
        
        String v_Value = i_Value.trim();
        for (PLCDataType v_Enum : PLCDataType.values())
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
    public static PLCDataType getCode(String i_Code)
    {
        if ( i_Code == null )
        {
            return null;
        }
        
        String v_Code = i_Code.trim();
        for (PLCDataType v_Enum : PLCDataType.values())
        {
            if ( v_Enum.code.equalsIgnoreCase(v_Code) )
            {
                return v_Enum;
            }
        }
        
        return null;
    }
    
    
    
    PLCDataType(String i_Value ,String i_Code ,String i_Comment)
    {
        this.value   = i_Value;
        this.code    = i_Code;
        this.comment = i_Comment;
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
    
    

    public String toString()
    {
        return this.value + "";
    }
    
}
