package org.hy.common.plc.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.hy.common.Help;
import org.hy.common.plc.enums.PLCDataType;

import Moka7.S7;





/**
 * PLC通讯数据。
 * 
 * 主要用于：S7-200 Smart的实现
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-19
 * @version     v1.0
 */
public class PLCByteData
{
    
    /**
     * 设置数据到字节数组中
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @param i_PLCDataType  寄存器的数据类型
     * @param i_PLCAddress   PLC通讯数据地址
     * @param i_Value        数据
     * @return
     */
    public static byte [] setByteData(PLCDataType i_PLCDataType ,PLCAddress i_PLCAddress ,Object i_Value)
    {
        byte [] v_ByteData = null;
        
        if ( i_PLCDataType == null )
        {
            throw new NullPointerException("PLCDataType is null.");
        }
        else if ( i_PLCAddress == null )
        {
            throw new NullPointerException("PLCAddress is null.");
        }
        else if ( i_Value == null )
        {
            throw new NullPointerException("Value is null.");
        }
        // 布尔
        else if ( PLCDataType.Bool.equals(i_PLCDataType) )
        {
            boolean v_Value = false;
            if ( i_Value instanceof Boolean )
            {
                v_Value = (Boolean) i_Value;
            }
            else
            {
                v_Value = Boolean.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[1];
            S7.SetBitAt(v_ByteData ,0 ,i_PLCAddress.getOffsetBit() ,v_Value);
        }
        // 8位字节
        else if ( PLCDataType.Byte.equals(i_PLCDataType) )
        {
            Integer v_Value = 0;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1 : 0;
            }
            else
            {
                v_Value = Integer.valueOf(i_Value.toString());
            }
            
            v_ByteData    = new byte[1];
            v_ByteData[0] = v_Value.byteValue();
        }
        // 16位字
        else if ( PLCDataType.Word.equals(i_PLCDataType) )
        {
            Integer v_Value = 0;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1 : 0;
            }
            else
            {
                v_Value = Integer.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[2];
            S7.SetWordAt(v_ByteData ,0 ,v_Value);
        }
        // 32位双字
        else if ( PLCDataType.DWord.equals(i_PLCDataType) )
        {
            Long v_Value = 0L;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1L : 0L;
            }
            else
            {
                v_Value = Long.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[4];
            S7.SetDWordAt(v_ByteData ,0 ,v_Value);
        }
        // 8位有符号整数
        else if ( PLCDataType.SInt.equals(i_PLCDataType) )
        {
            Integer v_Value = 0;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1 : 0;
            }
            else
            {
                v_Value = Integer.valueOf(i_Value.toString());
            }
            
            v_ByteData    = new byte[1];
            v_ByteData[0] = v_Value.byteValue();
        }
        // 8位无符号的整数
        else if ( PLCDataType.USInt.equals(i_PLCDataType) )
        {
            Integer v_Value = 0;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1 : 0;
            }
            else
            {
                v_Value = Integer.valueOf(i_Value.toString());
            }
            
            v_ByteData    = new byte[1];
            v_ByteData[0] = v_Value.byteValue();
        }
        // 16位有符号整数
        else if ( PLCDataType.Int.equals(i_PLCDataType) )
        {
            Integer v_Value = 0;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1 : 0;
            }
            else
            {
                v_Value = Integer.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[2];
            S7.SetShortAt(v_ByteData ,0 ,v_Value);
        }
        // 32位有符号整数
        else if ( PLCDataType.DInt.equals(i_PLCDataType) )
        {
            Integer v_Value = 0;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1 : 0;
            }
            else
            {
                v_Value = Integer.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[4];
            S7.SetDIntAt(v_ByteData ,0 ,v_Value);
        }
        // 32位无符号整数
        else if ( PLCDataType.UDInt.equals(i_PLCDataType) )
        {
            Integer v_Value = 0;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1 : 0;
            }
            else
            {
                v_Value = Integer.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[4];
            S7.SetDIntAt(v_ByteData ,0 ,v_Value);
        }
        // 32位浮点
        else if ( PLCDataType.Real.equals(i_PLCDataType) )
        {
            Float v_Value = 0F;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1F : 0F;
            }
            else
            {
                v_Value =  Float.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[4];
            S7.SetFloatAt(v_ByteData ,0 ,v_Value);
        }
        // 64位双精度浮点
        else if ( PLCDataType.LReal.equals(i_PLCDataType) )
        {
            Double v_Value = 0D;
            if ( i_Value instanceof Boolean )
            {
                v_Value = ((Boolean) i_Value) ? 1D : 0D;
            }
            else
            {
                v_Value = Double.valueOf(i_Value.toString());
            }
            
            v_ByteData = new byte[8];
            
            // 使用 ByteBuffer 转换（默认大端序，S7 PLC 通常用小端序）
            ByteBuffer.wrap(v_ByteData)
                      .order(ByteOrder.LITTLE_ENDIAN)     // 小端序（S7 常用）
                      .putDouble(v_Value);
        }
        else if ( PLCDataType.String.equals(i_PLCDataType) )
        {
            // TODO 待用时再实现
            throw new RuntimeException("你可以去实现它了");
        }
        else if ( PLCDataType.WString.equals(i_PLCDataType) )
        {
            // TODO 待用时再实现
            throw new RuntimeException("你可以去实现它了");
        }
        else
        {
            throw new RuntimeException("未知类型");
        }
        
        return v_ByteData;
    }
    
    
    
    /**
     * 创建对应数据类型所需的内存空间
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @param i_PLCDataType  寄存器的数据类型
     * @return
     */
    public static byte [] newByte(PLCDataType i_PLCDataType)
    {
        byte [] v_ByteData = null;
        
        if ( i_PLCDataType == null )
        {
            throw new NullPointerException("PLCDataType is null.");
        }
        // 布尔
        else if ( PLCDataType.Bool.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[1];
        }
        // 8位字节
        else if ( PLCDataType.Byte.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[1];
        }
        // 16位字
        else if ( PLCDataType.Word.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[2];
        }
        // 32位双字
        else if ( PLCDataType.DWord.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[4];
        }
        // 8位有符号整数
        else if ( PLCDataType.SInt.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[1];
        }
        // 8位无符号的整数
        else if ( PLCDataType.USInt.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[1];
        }
        // 16位有符号整数
        else if ( PLCDataType.Int.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[2];
        }
        // 32位有符号整数
        else if ( PLCDataType.DInt.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[4];
        }
        // 32位无符号整数
        else if ( PLCDataType.UDInt.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[4];
        }
        // 32位浮点
        else if ( PLCDataType.Real.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[4];
        }
        // 64位双精度浮点
        else if ( PLCDataType.LReal.equals(i_PLCDataType) )
        {
            v_ByteData = new byte[8];
        }
        else if ( PLCDataType.String.equals(i_PLCDataType) )
        {
            // TODO 待用时再实现
            throw new RuntimeException("你可以去实现它了");
        }
        else if ( PLCDataType.WString.equals(i_PLCDataType) )
        {
            // TODO 待用时再实现
            throw new RuntimeException("你可以去实现它了");
        }
        else
        {
            throw new RuntimeException("未知类型");
        }
        
        return v_ByteData;
    }
    
    
    
    /**
     * 获取字节数组中的数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-20
     * @version     v1.0
     *
     * @param i_PLCDataType  寄存器的数据类型
     * @param i_PLCAddress   PLC通讯数据地址
     * @param i_ByteData     字节数组
     * @return
     */
    public static Object getByteData(PLCDataType i_PLCDataType ,PLCAddress i_PLCAddress ,byte [] i_ByteData)
    {
        if ( i_PLCDataType == null )
        {
            throw new NullPointerException("PLCDataType is null.");
        }
        else if ( i_PLCAddress == null )
        {
            throw new NullPointerException("PLCAddress is null.");
        }
        else if ( Help.isNull(i_ByteData) )
        {
            throw new NullPointerException("ByteData is null.");
        }
        // 布尔
        else if ( PLCDataType.Bool.equals(i_PLCDataType) )
        {
            return S7.GetBitAt(i_ByteData ,0 ,i_PLCAddress.getOffsetBit());
        }
        // 8位字节
        else if ( PLCDataType.Byte.equals(i_PLCDataType) )
        {
            return (int) i_ByteData[0];
        }
        // 16位字
        else if ( PLCDataType.Word.equals(i_PLCDataType) )
        {
            return S7.GetShortAt(i_ByteData ,0);
        }
        // 32位双字
        else if ( PLCDataType.DWord.equals(i_PLCDataType) )
        {
            return S7.GetDWordAt(i_ByteData ,0);
        }
        // 8位有符号整数
        else if ( PLCDataType.SInt.equals(i_PLCDataType) )
        {
            return (int) i_ByteData[0];
        }
        // 8位无符号的整数
        else if ( PLCDataType.USInt.equals(i_PLCDataType) )
        {
            // 使用位掩码确保无符号转换
            return (int) (i_ByteData[0] & 0xFF);
        }
        // 16位有符号整数
        else if ( PLCDataType.Int.equals(i_PLCDataType) )
        {
            return S7.GetShortAt(i_ByteData ,0);
        }
        // 32位有符号整数
        else if ( PLCDataType.DInt.equals(i_PLCDataType) )
        {
            return S7.GetDIntAt(i_ByteData ,0);
        }
        // 32位无符号整数
        else if ( PLCDataType.UDInt.equals(i_PLCDataType) )
        {
            return S7.GetDIntAt(i_ByteData ,0);
        }
        // 32位浮点
        else if ( PLCDataType.Real.equals(i_PLCDataType) )
        {
            return S7.GetFloatAt(i_ByteData ,0);
        }
        // 64位双精度浮点
        else if ( PLCDataType.LReal.equals(i_PLCDataType) )
        {
            return ByteBuffer.wrap(i_ByteData)
                             .order(ByteOrder.LITTLE_ENDIAN)  // 必须与写入时顺序一致！
                             .getDouble();
        }
        else if ( PLCDataType.String.equals(i_PLCDataType) )
        {
            // TODO 待用时再实现
            throw new RuntimeException("你可以去实现它了");
        }
        else if ( PLCDataType.WString.equals(i_PLCDataType) )
        {
            // TODO 待用时再实现
            throw new RuntimeException("你可以去实现它了");
        }
        else
        {
            throw new RuntimeException("未知类型");
        }
    }
    
    
    
    private PLCByteData()
    {
        // Nothing.
    }
    
}
