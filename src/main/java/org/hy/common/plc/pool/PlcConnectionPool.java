package org.hy.common.plc.pool;

import java.time.Duration;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.hy.common.Help;
import org.hy.common.plc.data.PLCConfig;
import org.hy.common.xml.log.Logger;





/**
 * PLC连接池
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-02-09
 * @version     v1.0
 */
public class PlcConnectionPool
{

    private static final Logger $Logger = new Logger(PlcConnectionPool.class);
    
    
    
    private final GenericObjectPool<PlcConnection> pool;
    
    
    
    /**
     * 按PLC配置生成连接字符串
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-09
     * @version     v1.0
     *
     * @param i_PLCConfig  PLC配置
     * @return
     */
    public static String makeConnectString(PLCConfig i_PLCConfig)
    {
        // 机架号0和插槽号1。在 Siemens 自有软件中的 rack=0&slot=1 
        // PLC4X 或类似的第三方库，正确的格式是 remote-rack&remote-slot
        // 立体仓库   10.1.154.112
        // WYS71200 10.1.154.131、10.1.154.132 
        // 格式为 s7://username:password@IP:Port?timeout=5000
        // {protocol-code}:({transport-code})?//{transport-address}(?{parameter-string})?'
        StringBuilder v_ConnString = new StringBuilder();
        v_ConnString.append(i_PLCConfig.getProtocol()).append("://");
        if ( !Help.isNull(i_PLCConfig.getUserName()) && !Help.isNull(i_PLCConfig.getUserPassword()) )
        {
            v_ConnString.append(i_PLCConfig.getUserName());
            v_ConnString.append(":");
            v_ConnString.append(i_PLCConfig.getUserPassword());
            v_ConnString.append("@");
        }
        
        v_ConnString.append(i_PLCConfig.getHost());
        v_ConnString.append(":");
        v_ConnString.append(i_PLCConfig.getPort());
        v_ConnString.append("?timeout=").append(i_PLCConfig.getTimeout());
        
        if ( i_PLCConfig.getRack() != null )
        {
            v_ConnString.append("&remote-rack=").append(i_PLCConfig.getRack());
        }
        if ( i_PLCConfig.getSlot() != null )
        {
            v_ConnString.append("&remote-slot=").append(i_PLCConfig.getSlot());
        }
        
        return v_ConnString.toString();
    }

    

    /**
     * 初始化连接池
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-09
     * @version     v1.0
     *
     * @param i_PLCConfig  PLC连接配置
     * @param i_MaxConn    最大连接数（不超过 PLC 设备限制），PLC 1200默认为8个
     * @param i_MinIdle    最小空闲连接
     * @param i_MaxIdle    最大空闲连接
     */
    public PlcConnectionPool(PLCConfig i_PLCConfig)
    {
        GenericObjectPoolConfig<PlcConnection> v_PoolConfig = new GenericObjectPoolConfig<>();
        v_PoolConfig.setMaxTotal(Help.max(Help.NVL(i_PLCConfig.getMaxConn()) ,1));
        v_PoolConfig.setMinIdle( Help.max(Help.NVL(i_PLCConfig.getMinIdle()) ,1));
        v_PoolConfig.setMaxIdle( Help.max(Help.NVL(i_PLCConfig.getMaxIdle()) ,1));
        v_PoolConfig.setTestOnBorrow(true);                                                         // 借用连接时校验是否有效
        v_PoolConfig.setTestOnReturn(true);                                                         // 归还连接时校验是否有效
        v_PoolConfig.setTestWhileIdle(true);                                                        // 空闲时校验
        v_PoolConfig.setMaxWait(Duration.ofMillis(i_PLCConfig.getTimeout()));                       // 设置获取连接的等待超时时长
        v_PoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(i_PLCConfig.getTimeout()* 10L) ); // 空闲检测间隔
        v_PoolConfig.setMinEvictableIdleDuration(Duration.ofMillis(i_PLCConfig.getTimeout()));      // 空闲超时
        
        PlcConnectionFactory v_PlcConnFactory = new PlcConnectionFactory(i_PLCConfig);
        this.pool = new GenericObjectPool<>(v_PlcConnFactory ,v_PoolConfig);
    }



    /**
     * 从连接池获取连接
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-09
     * @version     v1.0
     *
     * @return
     * @throws Exception
     */
    public PlcConnection borrowConnection() throws Exception
    {
        PlcConnection v_PlcConn = this.pool.borrowObject();
        
        if ( v_PlcConn.isConnected() )
        {
            return v_PlcConn;
        }
        else
        {
            // 连接失效，销毁并重试
            this.pool.invalidateObject(v_PlcConn);
            throw new PlcConnectionException("获取的连接已失效，触发重试");
        }
    }



    /**
     * 归还连接到池
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-09
     * @version     v1.0
     *
     * @param i_PlcConnection
     */
    public void returnConnection(PlcConnection i_PlcConnection)
    {
        if ( i_PlcConnection != null )
        {
            this.pool.returnObject(i_PlcConnection);
        }
    }



    /**
     * 关闭连接池（程序退出时调用）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-09
     * @version     v1.0
     *
     */
    public void close()
    {
        this.pool.close();
    }



    /**
     * 连接工厂：负责创建/销毁 PLC 连接
     *
     * @author      ZhengWei(HY)
     * @createDate  2026-02-09
     * @version     v1.0
     */
    private static class PlcConnectionFactory extends BasePooledObjectFactory<PlcConnection>
    {

        private final PLCConfig plcConfig;
        
        
        
        public PlcConnectionFactory(PLCConfig i_PLCConfig)
        {
            this.plcConfig = i_PLCConfig;
        }
        
        
        
        /**
         * 创建新的 PLC 连接
         *
         * @author      ZhengWei(HY)
         * @createDate  2026-02-09
         * @version     v1.0
         *
         * @return
         * @throws PlcConnectionException
         *
         * @see org.apache.commons.pool2.BasePooledObjectFactory#create()
         */
        @Override
        public PlcConnection create() throws PlcConnectionException
        {
            try
            {
                PlcDriverManager     v_PlcDriverManager     = PlcDriverManager.getDefault();
                PlcConnectionManager v_PlcConnectionManager = v_PlcDriverManager.getConnectionManager();
                PlcConnection        v_PLCConn              = v_PlcConnectionManager.getConnection(makeConnectString(this.plcConfig));
                
                if ( !v_PLCConn.getMetadata().isReadSupported() )
                {
                    $Logger.error("PLC[" + this.plcConfig.getXid() + "] connection doesn't support reading.");
                    return null;
                }
                
                return v_PLCConn;
            }
            catch (Exception exce)
            {
                $Logger.error("PLC[" + this.plcConfig.getXid() + "] connection error." ,exce);
            }
            
            return null;
        }



        @Override
        public PooledObject<PlcConnection> wrap(PlcConnection conn)
        {
            return new DefaultPooledObject<>(conn);
        }



        /**
         * 校验连接是否有效
         *
         * @author      ZhengWei(HY)
         * @createDate  2026-02-09
         * @version     v1.0
         *
         * @param i_PooledObject
         * @return
         *
         * @see org.apache.commons.pool2.BasePooledObjectFactory#validateObject(org.apache.commons.pool2.PooledObject)
         */
        @Override
        public boolean validateObject(PooledObject<PlcConnection> i_PooledObject)
        {
            return i_PooledObject.getObject().isConnected();
        }
        
        

        /**
         * 销毁连接时关闭
         *
         * @author      ZhengWei(HY)
         * @createDate  2026-02-09
         * @version     v1.0
         *
         * @param i_PooledObject
         * @throws Exception
         *
         * @see org.apache.commons.pool2.BasePooledObjectFactory#destroyObject(org.apache.commons.pool2.PooledObject)
         */
        @Override
        public void destroyObject(PooledObject<PlcConnection> i_PooledObject) throws Exception
        {
            i_PooledObject.getObject().close();
        }
    }
}
