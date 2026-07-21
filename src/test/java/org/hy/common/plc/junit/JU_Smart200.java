package org.hy.common.plc.junit;

import org.hy.common.ByteHelp;
import org.hy.common.Help;
import org.hy.common.plc.data.PLCConfig;
import org.hy.common.plc.data.PLCDataItemConfig;
import org.hy.common.plc.data.PLCDatagramConfig;
import org.hy.common.plc.data.XPLC;
import org.hy.common.plc.enums.PLCDataType;
import org.hy.common.plc.enums.PLCProtocolType;
import org.hy.common.plc.enums.PLCRegisterType;
import org.junit.Test;

import Moka7.S7;
import Moka7.S7Client;





/**
 * 测试单元：西门子S7-200 Smart的通讯协议
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-06
 * @version     v1.0
 */
public class JU_Smart200
{
    
    public static void main(String []  i_Args) throws InterruptedException
    {
        // java -cp hy.common.plc.jar:* Moka7.S7Demo
        // connectS7200Moka();
        // connectS71200();
        connectS7200();
    }
    
    
    
    /**
     * 破碎前处理IES系统对接
     * 1   PLC IP地址:  192.168.0.10
     * 2   MES系统给启动信号:M16.0，启动运行中状态反馈:M16.1=1。
     * 3   MES系统给停止信号:M16.2，停止状态反馈:M16.1=0。                          
     * 4   MES故障信号反馈给MES系统:M16.3
     */
    public static void connectS7200Moka()
    {
        // 创建S7客户端实例
        S7Client client = new S7Client();
        
        // PLC的IP地址和机架号、槽号(S7-200 Smart通常为0,1)
        String ipAddress = "192.168.0.10";              // 熔炼192.168.2.131
        int rack = 0;
        int slot = 1;
        
        // 连接到PLC
        int result = client.ConnectTo(ipAddress, rack, slot);
        
        if ( result == 0 )
        {
            System.out.println("成功连接到PLC!");
            
            byte[] buffer = new byte[1];
            S7.SetBitAt(buffer ,0 ,1 ,true);
            result = client.WriteArea(S7.S7AreaMK ,0 ,16 ,1 ,buffer);
            if ( result == 0 )
            {
                System.out.println("写入成功");
            }
            else
            {
                System.out.println("写入异常");
            }
            
            buffer = new byte[1];  // 读取1个字节(包含M16.0到M16.7)
            result = client.ReadArea(S7.S7AreaMK, 0, 16, 1, buffer); 
            if ( result == 0 )
            {
                for (int x=0; x<4; x++)
                {
                    boolean v_M16 = S7.GetBitAt(buffer ,0 ,x);
                    System.out.println("M16." + x + "的值: " + v_M16);
                }
            }
            else
            {
                System.out.println("读取异常");
            }
            
            // 断开连接
            client.Disconnect();
        }
        else
        {
            System.out.println("连接失败，错误码: " + result);
        }
    }
    
    
    
    public static void connectS7200() throws InterruptedException
    {
        PLCConfig v_Config = new PLCConfig();
        v_Config.setProtocol(PLCProtocolType.S7_200_Smart.getValue());
        v_Config.setHost("192.168.0.110");
        v_Config.setPort(102);
        v_Config.setRack(0);
        v_Config.setSlot(1);
        
        XPLC v_XPLC = new XPLC(v_Config);
        if ( v_XPLC.connect().booleanValue() )
        {
            System.out.println("成功连接到200!");
            
            PLCDataItemConfig v_DataItem = new PLCDataItemConfig();
            v_DataItem.setCode("C001");
            v_DataItem.setName("N001");
            v_DataItem.setRegisterType(PLCRegisterType.Memory);
            v_DataItem.setRegisterNo(0);
            v_DataItem.setRegisterOffset("MX21.1");
            v_DataItem.setDataType(PLCDataType.Bool);
            
            PLCDatagramConfig v_Datagram = new PLCDatagramConfig();
            v_Datagram.getItems().add(v_DataItem);
            
            for (int x=1; x<=100; x++)
            {
                Help.print(v_XPLC.getPlcIO().readDatas(v_Datagram ,5000));
                Thread.sleep(100);
            }
            
            v_XPLC.close(null);
        }
        else
        {
            System.out.println("连接失败200");
        }
    }
    
    
    
    public static void connectS71200()
    {
        PLCConfig v_Config = new PLCConfig();
        v_Config.setProtocol(PLCProtocolType.S7.getValue());
        v_Config.setHost("192.168.2.132");
        v_Config.setPort(102);
        v_Config.setRack(0);
        v_Config.setSlot(1);
        
        XPLC v_XPLC = new XPLC(v_Config);
        if ( v_XPLC.connect().booleanValue() )
        {
            System.out.println("成功连接到1200!");
            v_XPLC.close(null);
        }
        else
        {
            System.out.println("连接失败1200");
        }
    }
    
    
    
    @Test
    public void test_S7200Smart() 
    {
        S7Client client = new S7Client();
        String plcIp = "192.168.0.110";

        try {
            // 2. 建立连接 200Smart固定机架0，槽位1
            int connectRet = client.ConnectTo(plcIp, 0, 1);
            if (connectRet != 0) {
                System.out.println("连接PLC失败：" + client.ErrorText(connectRet));
                return;
            }
            System.out.println("PLC连接成功");

            // 3. 定义缓冲区：读取1个位，缓冲区1字节足够
            byte[] buffer = new byte[1];

            // 读取 M21.1
            // 参数：区域、DB号、起始字节、读取数量、数据长度、缓存
            int readRet = client.ReadArea(
                    S7.S7AreaMK,    // M存储区
                    0,                    // M区DB固定0
                    21,                   // 起始字节 M21
                    1,                    // 读取1个位
                    buffer
            );

            if (readRet == 0) {
                int m21_1 = buffer[0] & 0x01;
                System.out.println("M21.1 当前值：" + m21_1);
            } else {
                // 打印错误信息，返回4就是TCP接收超时
                System.out.println("读取失败 错误码：" + readRet + " 说明：" + client.ErrorText(readRet));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 4. 断开连接释放资源
            client.Disconnect();
        }
    }
    
    
    
    @Test
    public void test_Set()
    {
        for (int x=0; x<=7; x++)
        {
            byte [] v_ByteData = new byte[1];
            S7.SetBitAt(v_ByteData ,0 ,x ,true);
            System.out.println(ByteHelp.byteToInt(v_ByteData));
        }
        
        byte [] v_ByteData = new byte[1];
        S7.SetBitAt(v_ByteData ,0 ,2 ,true);
        System.out.println(ByteHelp.byteToInt(v_ByteData));
    }
    
}
