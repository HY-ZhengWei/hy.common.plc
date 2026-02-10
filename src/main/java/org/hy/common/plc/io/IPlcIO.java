package org.hy.common.plc.io;

import java.util.Map;

import org.apache.plc4x.java.api.PlcConnection;
import org.hy.common.Return;
import org.hy.common.plc.data.PLCConfig;
import org.hy.common.plc.data.PLCDatagramConfig;





/**
 * 统一多个组件的PLC连接、读、写等操作的接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-08-19
 * @version     v1.0
 */
public interface IPlcIO
{
    
    /**
     * 获取PLC连接配置信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     */
    public PLCConfig getPLCConfig();
    
    
    
    /**
     * 获取PLC具体实现类的连接对象
     * 
     * 注：常规情况下不会用的此方法，所有对外的方法应当由本接口其它方法来统一规划后实现。
     *     但又担心到前期规划不位，先暂时允许少量特殊情况下通过此方法解决问题。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     */
    @Deprecated
    public Object getConnectObject();
    
    
    
    /**
     * 写入数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @param i_Datagram  数据报文
     * @param i_Datas     数据集合
     * @param i_Timeout   数据读写超时时长（单位：毫秒）
     * @return
     */
    public boolean writeDatas(PLCDatagramConfig i_Datagram ,Map<String ,Object> i_Datas ,long i_Timeout);
    
    
    
    /**
     * 读取数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @param i_Datagram  数据报文
     * @param i_Timeout   数据读写超时时长（单位：毫秒）
     * @return
     */
    public Map<String ,Object> readDatas(PLCDatagramConfig i_Datagram ,long i_Timeout);
    
    
    
    /**
     * 连接物联设备
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return  Return.boolean   表示是否连接成功
     *          Return.paramObj  表示PLC连接对象
     */
    public Return<PlcConnection> connect() throws Exception;
    
    
    
    /**
     * 是否已连接成功
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *
     * @return
     */
    public boolean isConnected();
    
    
    
    /**
     * 关闭连接
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-08-19
     * @version     v1.0
     *              v2.0  2026-02-09  添加：升级为连接池
     * 
     * @param i_PlcConnection  PLC连接对象
     */
    public void close(PlcConnection i_PlcConnection);
    
}
