package org.hy.common.plc.util;

import org.hy.common.Help;
import org.hy.common.plc.enums.PLCRegisterType;





/**
 * PLC通讯数据地址的工具类。
 * 
 * 主要用于：S7-200 Smart
 * 
 *   将M16.0  地址分解的值项：M  空  16  0
 *   将DBW34.0地址分解的值项：DB  W  34  0
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-19
 * @version     v1.0
 */
public class PLCAddress
{
    
    /** 寄存器类型 */
    private PLCRegisterType registerType;
    
    /** 寄存器编号 */
    private Integer         registerNo;
    
    /** 数据类型编码 */
    private String          dataTypeCode;
    
    /** 偏移量：字节的偏移量 */
    private Integer         offsetByte;
    
    /** 偏移量：字节中位的偏移量 */
    private Integer         offsetBit;
    
    
    
    public PLCAddress(Integer i_RegisterNo ,String i_Address)
    {
        if ( Help.isNull(i_Address) )
        {
            throw new NullPointerException("Address is null.");
        }
        
        // 分解出：寄存器类型
        String v_Address = i_Address.trim().toUpperCase();
        for (PLCRegisterType v_Enum : PLCRegisterType.values())
        {
            String v_RTCode = v_Enum.getCode();
            int    v_Len    = v_RTCode.length();
            
            if ( v_Address.length() <= v_Len )
            {
                throw new RuntimeException("Address[" + i_Address + "] lenght invaild, missing register type.");
            }
            
            String v_ARType = v_Address.substring(0 ,v_Len);
            if ( v_RTCode.equalsIgnoreCase(v_ARType) )
            {
                this.registerType = v_Enum;
                v_Address = v_Address.substring(v_Len);
                
                if ( PLCRegisterType.Memory.equals(this.registerType)
                  || PLCRegisterType.Input .equals(this.registerType)
                  || PLCRegisterType.Output.equals(this.registerType) )
                {
                    // 对于其他区域(M、I、Q等)，DBNumber参数被忽略，通常设置为0
                    this.registerNo = 0;
                }
                else
                {
                    if ( i_RegisterNo == null || i_RegisterNo < 0 )
                    {
                        throw new RuntimeException("Address[" + i_Address + "] RegisterNo[" + i_RegisterNo + "] invaild.");
                    }
                    this.registerNo = i_RegisterNo;
                }
                
                break;
            }
        }
        
        // 分解出：数据类型
        if ( v_Address.length() <= 1 )
        {
            throw new RuntimeException("Address[" + i_Address + "] lenght invaild, missing data type.");
        }
        
        char v_DataType = v_Address.charAt(0);
        if ( 'A' <= v_DataType && v_DataType <= 'Z' )
        {
            this.dataTypeCode = v_Address.substring(0 ,1);
            v_Address = v_Address.substring(1);
        }
        else if ( '0' <= v_DataType && v_DataType <= '9' )
        {
            this.dataTypeCode = "";
        }
        else
        {
            throw new RuntimeException("Address[" + i_Address + "] data type[" + v_DataType + "] is invaild.");
        }
        
        // 分解出：偏移量
        if ( !Help.isNumber(v_Address) )
        {
            throw new RuntimeException("Address[" + i_Address + "] offset [" + v_Address + "] is invaild.");
        }
        
        String [] v_Offsets = v_Address.split("\\.");
        if ( v_Offsets.length == 1 )
        {
            this.offsetByte = Integer.parseInt(v_Offsets[0]);
            this.offsetBit  = 0;
        }
        else 
        {
            this.offsetByte = Integer.parseInt(v_Offsets[0]);
            this.offsetBit  = Integer.parseInt(v_Offsets[1]);
        }
    }
    
    
    /**
     * 获取：寄存器类型
     */
    public PLCRegisterType getRegisterType()
    {
        return registerType;
    }
    
    
    /**
     * 获取：寄存器编号
     */
    public Integer getRegisterNo()
    {
        return registerNo;
    }

    
    /**
     * 获取：数据类型编码
     */
    public String getDataTypeCode()
    {
        return dataTypeCode;
    }


    /**
     * 获取：偏移量：字节的偏移量
     */
    public Integer getOffsetByte()
    {
        return offsetByte;
    }

    
    /**
     * 获取：偏移量：字节中位的偏移量
     */
    public Integer getOffsetBit()
    {
        return offsetBit;
    }


    /**
     * 合并为PLC地址字符串
     *
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder v_Builder = new StringBuilder();
        v_Builder.append("%").append(this.registerType.getCode());
        v_Builder.append(this.registerNo);
        v_Builder.append(".");
        v_Builder.append(this.registerType.getCode());
        v_Builder.append(this.dataTypeCode);
        v_Builder.append(this.offsetByte);
        v_Builder.append(".");
        v_Builder.append(this.offsetBit);
        return  v_Builder.toString();
    }
    
}
