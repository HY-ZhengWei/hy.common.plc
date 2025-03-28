package org.hy.common.plc.junit;

import org.hy.common.callflow.node.NodeConfig;
import org.hy.common.plc.callflow.IoTGetConfig;
import org.junit.Test;





/**
 * 测试单元：编排引擎
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-03-28
 * @version     v1.0
 */
public class JU_CallFlow
{
    
    @Test
    public void test()
    {
        IoTGetConfig v_IoTGetConfig = new IoTGetConfig();
        if ( v_IoTGetConfig instanceof IoTGetConfig )
        {
            if ( v_IoTGetConfig instanceof NodeConfig )
            {
                System.out.println("YES NodeConfig");
            }
            else
            {
                System.out.println("YES IoTGetConfig");
            }
        }
        else
        {
            System.out.println("NO");
        }
    }
    
}
