package org.hy.common.plc.junit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.junit.Test;





/**
 * 测试单元：PLC立库（西门子S7-300）
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-03-11
 * @version     v1.0
 */
public class JU_PLC
{
    
    @Test
    public void test_LiKu()
    {
        // 10.1.154.112 102 机架为0 插槽2
        String v_ConnString = "s7://10.1.154.112:102?timeout=5000";

        try
        {
            PlcConnection v_PLCConn = PlcDriverManager.getDefault().getConnectionManager().getConnection(v_ConnString);
            
            // Check if this connection support reading of data.
            if ( !v_PLCConn.getMetadata().isReadSupported() )
            {
                System.err.println("This connection doesn't support reading.");
                return;
            }
            
            /*
            输入寄存器：s7:<db_number>.<data_type><start_address>
            输出寄存器：s7:<db_number>.<data_type><start_address>

            其中，<db_number> 是数据块编号，<data_type> 是数据类型（如 W 表示字，D 表示双字），<start_address> 是起始地址。
            示例：
                s7:DB1.DBD0 读取 Siemens 数据块 DB1 中的双字（DWord）0 的值。
                s7:DB2.DBW10 读取 Siemens 数据块 DB2 中的字（Word）10 的值。
            */
            
            // Create a new read request:
            // - Give the single item requested an alias name
            // %Q0.4:BOOL的含意
            //     %Q：  表示这是一个输出寄存器（Output Register），用于从PLC中读取数据。
            //     0：   表示这个寄存器的编号，通常从0开始递增。
            //     4：   表示这个寄存器中具体的位偏移量，即要读取的位在该寄存器中的位置。
            //     BOOL：表示这是一个布尔类型的数据，即只有两种取值，通常是0或1。
            //
            // %I0.1:BOOL：     表示输入寄存器（Input Register）0的第1位的布尔数值。
            // %M10:BYTE：      表示内存寄存器（Memory Register）10的一个字节（8位）数据。
            // %DB100.DBW2:INT：表示数据块（Data Block）100中的第2个字（16位）整数数据。
            // %QB3.2:BOOL：    表示输出寄存器（Output Register）B的第3个字的第2位的布尔数值。
            // %
            
            /* 常见数据类型
            BOOL：布尔类型，只有两种取值，通常是0或1。        DBX是布尔，举例：%DB20.DBX92.1:BOOL
            BYTE：字节类型，通常是8位的数据。               DBB是字节，举例：%DB20.DBB2.0:BYTE
            WORD：字类型，通常是16位的数据。               DBW是字，举例：%DB20.DBW6.0:WORD
            DWORD：双字类型，通常是32位的数据。             
            REAL：实数类型，通常是单精度浮点数。             DBD是实数，举例：%DB20.DBD102.0:REAL
            LREAL：长实数类型，通常是双精度浮点数。
            INT：有符号字节类型，通常是带符号的16位数据。      DBW
            SINT：有符号字节类型，通常是带符号的8位数据。      DBB
            USINT：无符号字节类型，通常是不带符号的8位数据。    DBB
            DINT：有符号双字类型，通常是带符号的32位数据。
            UDINT：无符号双字类型，通常是不带符号的32位数据。
            */
            
            /*
             * 源码分析
             *      S7SubscriptionTag
             *      S7PlcTagHandler
             *      S7StringVarLengthTag
             *      TransportSize
             */
            PlcReadRequest.Builder builder = v_PLCConn.readRequestBuilder();
            builder.addTagAddress("value-1"      ,"%Q0.4:BOOL");
            builder.addTagAddress("value-2"      ,"%Q0:BYTE");
            builder.addTagAddress("value-3"      ,"%I0.2:BOOL");
            builder.addTagAddress("LiKu-作业状态" ,"%M101.4:BOOL");       // 作业状态：M101.4（0代表当前无作业任务，1代表正在作业）
            builder.addTagAddress("LiKu-实际速度" ,"%DB20.DBD34:DINT");   // 实际速度：DB20.DBD34（数值范围0-200，单位m/s,如果不在范围内，代表没采集到或者未处理）
            builder.addTagAddress("LiKu-实际位置" ,"%DB20.DBD102:REAL");  // 实际位置：DB20.DBD102（数据范围0-100000,单位mm）
            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(5000, TimeUnit.MILLISECONDS);
            
            // 显示读取的数据
            for (String tagName : response.getTagNames())
            {
                if ( response.getResponseCode(tagName) == PlcResponseCode.OK )
                {
                    int numValues = response.getNumberOfValues(tagName);
                    // If it's just one element, output just one single line.
                    if ( numValues == 1 )
                    {
                        System.out.println("V[" + tagName + "]: " + response.getObject(tagName));
                    }
                    // If it's more than one element, output each in a single
                    // row.
                    else
                    {
                        System.out.println("V[" + tagName + "]:");
                        for (int i = 0; i < numValues; i++)
                        {
                            System.out.println(" - " + response.getObject(tagName ,i));
                        }
                    }
                }
                // Something went wrong, to output an error message instead.
                else
                {
                    System.err.println("Error[" + tagName + "]: " + response.getResponseCode(tagName).name());
                }
            }
        }
        catch (PlcConnectionException e)
        {
            // 连接时异常
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            // 读取时异常
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            // 读取时异常
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            // 读取时异常
            e.printStackTrace();
        }
    }
    
}
