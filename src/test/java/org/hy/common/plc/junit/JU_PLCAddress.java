package org.hy.common.plc.junit;

import org.hy.common.plc.util.PLCAddress;
import org.junit.Test;





/**
 * 测试单元：PLC通讯数据地址的工具类
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-19
 * @version     v1.0
 */
public class JU_PLCAddress
{
    
    @Test
    public void test_PLCAddress()
    {
        System.out.println(new PLCAddress("DBB1.0"));
        System.out.println(new PLCAddress("DBB20.0"));
        System.out.println(new PLCAddress("DBB30.1"));
        System.out.println(new PLCAddress("DBB400"));
        System.out.println(new PLCAddress("DBW0.0"));
        System.out.println(new PLCAddress("DBW0"));
        System.out.println(new PLCAddress("M16"));
        System.out.println(new PLCAddress("M16.0"));
        System.out.println(new PLCAddress("MX16.0"));
    }
    
}
