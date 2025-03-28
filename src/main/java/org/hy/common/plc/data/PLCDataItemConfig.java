package org.hy.common.plc.data;

import org.hy.common.Help;
import org.hy.common.plc.enums.PLCDataType;
import org.hy.common.plc.enums.PLCRegisterType;





/**
 * 寄存器的数据项
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-05-14
 * @version     v1.0
 */
public class PLCDataItemConfig
{
    
    /** 数据项编码 */
    private String          code;
    
    /** 数据项名称 */
    private String          name;
    
    /** 寄存器类型 */
    private PLCRegisterType registerType;
    
    /** 寄存器编号 */
    private Integer         registerNo;
    
    /** 寄存器位偏移量 */
    private String          registerOffset;
    
    /** 数据类型 */
    private PLCDataType     dataType;
    
    /** 数据最大长度（仅用于String和WString） */
    private Integer         dataMaxLength;
    
    /** 是否为数字类型的 */
    private Boolean         isNumber;
    
    /** 有效数据的最大范围（包含最大值）。仅对数字类的有效。NULL表示不效验 */
    private String          maxValue;
    
    /** 有效数据的最小范围（包含最小值）。仅对数字类的有效。NULL表示不效验 */
    private String          minValue;
    
    /** 关联的数据报文（仅内部使用）。允许一个数据项被多个数据报文引用 */
    private String          datagramID;
    
    /** 数据项整合生成的PLC请求地址 */
    private String          plcTagAddress;
    
    
    
    /**
     * 生成数据项的PLC请求地址
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-15
     * @version     v1.0
     *
     * @return
     */
    public String makePLCTagAddress()
    {
        if ( this.registerType == null )
        {
            this.plcTagAddress = "";
            return this.plcTagAddress;
        }
        
        if ( this.dataType == null )
        {
            this.plcTagAddress = "";
            return this.plcTagAddress;
        }
        
        if ( this.registerNo == null || this.registerNo < 0 )
        {
            this.plcTagAddress = "";
            return this.plcTagAddress;
        }
        
        if ( Help.isNull(this.registerOffset) )
        {
            this.plcTagAddress = "";
            return this.plcTagAddress;
        }
        
        StringBuilder v_Builder = new StringBuilder();
        
        v_Builder.append("%").append(this.registerType.getCode());
        v_Builder.append(this.registerNo);
        v_Builder.append(".").append(this.registerOffset.trim());
        v_Builder.append(":").append(this.dataType.getCode());
        
        if (  PLCDataType.String .equals(this.dataType)
           || PLCDataType.WString.equals(this.dataType) ) 
        {
            if ( this.dataMaxLength != null )
            {
                v_Builder.append("(").append(this.dataMaxLength).append(")");
            }
        }
        
        this.plcTagAddress = v_Builder.toString();
        return plcTagAddress;
    }
    
    
    
    /**
     * 验证数值是否为有效范围内的数值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-16
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public boolean isValid(Double i_Value)
    {
        if ( i_Value == null )
        {
            return false;
        }
        
        if ( this.minValue != null )
        {
            if ( Double.parseDouble(this.minValue) > i_Value.doubleValue() )
            {
                return false;
            }
        }
        
        if ( this.maxValue != null )
        {
            if ( Double.parseDouble(this.maxValue) < i_Value.doubleValue() )
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    
    /**
     * 获取：是否为数字类型的
     */
    public boolean isNumber()
    {
        return isNumber;
    }


    /**
     * 获取：寄存器类型ID
     */
    public String getRegisterTypeID()
    {
        return this.registerType == null ? null : this.registerType.getValue();
    }

    
    /**
     * 设置：寄存器类型ID
     * 
     * @param i_RegisterTypeID 寄存器类型ID
     */
    public void setRegisterTypeID(String i_RegisterTypeID)
    {
        this.registerType = PLCRegisterType.get(i_RegisterTypeID);
    }
    
    
    /**
     * 获取：寄存器类型Code
     */
    public String getRegisterTypeCode()
    {
        return this.registerType == null ? null : this.registerType.getCode();
    }

    
    /**
     * 设置：寄存器类型Code
     * 
     * @param i_RegisterTypeCode 寄存器类型Code
     */
    public void setRegisterTypeCode(String i_RegisterTypeCode)
    {
        this.registerType = PLCRegisterType.getCode(i_RegisterTypeCode);
    }
    
    
    /**
     * 获取：寄存器数据类型ID
     */
    public String getDataTypeID()
    {
        return this.dataType == null ? null : this.dataType.getValue();
    }

    
    /**
     * 设置：寄存器数据类型ID
     * 
     * @param i_DataTypeID 寄存器数据类型ID
     */
    public void setDataTypeID(String i_DataTypeID)
    {
        this.dataType = PLCDataType.get(i_DataTypeID);
        
        if ( this.dataType == null )
        {
            this.isNumber = false;
        }
        // 字
        else if ( PLCDataType.Word.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 双字
        else if ( PLCDataType.DWord.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 8位整数
        else if ( PLCDataType.SInt.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 8位无符号整数
        else if ( PLCDataType.USInt.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 16位整数
        else if ( PLCDataType.Int.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 32位整数
        else if ( PLCDataType.DInt.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 32位无符号整数
        else if ( PLCDataType.UDInt.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 浮点
        else if ( PLCDataType.Real.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        // 双精度浮点
        else if ( PLCDataType.LReal.equals(this.dataType) )
        {
            this.isNumber = true;
        }
        else
        {
            this.isNumber = false;
        }
    }
    
    
    /**
     * 获取：寄存器数据类型Code
     */
    public String getDataTypeCode()
    {
        return this.dataType == null ? null : this.dataType.getCode();
    }

    
    /**
     * 设置：寄存器数据类型Code
     * 
     * @param i_DataTypeCode 寄存器数据类型Code
     */
    public void setDataTypeCode(String i_DataTypeCode)
    {
        this.dataType = PLCDataType.getCode(i_DataTypeCode);
    }

    
    /**
     * 获取：数据项编码
     */
    public String getCode()
    {
        return code;
    }

    
    /**
     * 设置：数据项编码
     * 
     * @param i_Code 数据项编码
     */
    public void setCode(String i_Code)
    {
        this.code = i_Code;
    }

    
    /**
     * 获取：数据项名称
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * 设置：数据项名称
     * 
     * @param i_Name 数据项名称
     */
    public void setName(String i_Name)
    {
        this.name = i_Name;
    }


    /**
     * 获取：寄存器类型
     */
    public PLCRegisterType getRegisterType()
    {
        return registerType;
    }

    
    /**
     * 设置：寄存器类型
     * 
     * @param i_RegisterType 寄存器类型
     */
    public void setRegisterType(PLCRegisterType i_RegisterType)
    {
        this.registerType = i_RegisterType;
    }

    
    /**
     * 获取：寄存器编号
     */
    public Integer getRegisterNo()
    {
        return registerNo;
    }

    
    /**
     * 设置：寄存器编号
     * 
     * @param i_RegisterNo 寄存器编号
     */
    public void setRegisterNo(Integer i_RegisterNo)
    {
        this.registerNo = i_RegisterNo;
    }

    
    /**
     * 获取：寄存器位偏移量
     */
    public String getRegisterOffset()
    {
        return registerOffset;
    }

    
    /**
     * 设置：寄存器位偏移量
     * 
     * @param i_RegisterOffset 寄存器位偏移量
     */
    public void setRegisterOffset(String i_RegisterOffset)
    {
        this.registerOffset = i_RegisterOffset;
    }

    
    /**
     * 获取：数据类型
     */
    public PLCDataType getDataType()
    {
        return dataType;
    }

    
    /**
     * 设置：数据类型
     * 
     * @param i_DataType 数据类型
     */
    public void setDataType(PLCDataType i_DataType)
    {
        this.dataType = i_DataType;
    }
    
    
    /**
     * 获取：数据最大长度（仅用于String和WString）
     */
    public Integer getDataMaxLength()
    {
        return dataMaxLength;
    }

    
    /**
     * 设置：数据最大长度（仅用于String和WString）
     * 
     * @param i_DataMaxLength 数据最大长度（仅用于String和WString）
     */
    public void setDataMaxLength(Integer i_DataMaxLength)
    {
        this.dataMaxLength = i_DataMaxLength;
    }


    /**
     * 获取：关联的数据报文（仅内部使用）。允许一个数据项被多个数据报文引用
     */
    public String getDatagramID()
    {
        return datagramID;
    }

    
    /**
     * 设置：关联的数据报文（仅内部使用）。允许一个数据项被多个数据报文引用
     * 
     * @param i_DatagramID 关联的数据报文（仅内部使用）。允许一个数据项被多个数据报文引用
     */
    public void setDatagramID(String i_DatagramID)
    {
        this.datagramID = i_DatagramID;
    }

    
    /**
     * 获取：有效数据的最大范围（包含最大值）。仅对数字类的有效。NULL表示不效验
     */
    public String getMaxValue()
    {
        return maxValue;
    }

    
    /**
     * 设置：有效数据的最大范围（包含最大值）。仅对数字类的有效。NULL表示不效验
     * 
     * @param i_MaxValue 有效数据的最大范围（包含最大值）。仅对数字类的有效。NULL表示不效验
     */
    public void setMaxValue(String i_MaxValue)
    {
        this.maxValue = i_MaxValue;
    }

    
    /**
     * 获取：有效数据的最小范围（包含最小值）。仅对数字类的有效。NULL表示不效验
     */
    public String getMinValue()
    {
        return minValue;
    }

    
    /**
     * 设置：有效数据的最小范围（包含最小值）。仅对数字类的有效。NULL表示不效验
     * 
     * @param i_MinValue 有效数据的最小范围（包含最小值）。仅对数字类的有效。NULL表示不效验
     */
    public void setMinValue(String i_MinValue)
    {
        this.minValue = i_MinValue;
    }


    @Override
    public String toString()
    {
        if ( Help.isNull(this.plcTagAddress) )
        {
            return this.makePLCTagAddress();
        }
        else
        {
            return this.plcTagAddress;
        }
    }
    
}