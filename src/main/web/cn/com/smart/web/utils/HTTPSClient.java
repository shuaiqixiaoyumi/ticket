package cn.com.smart.web.utils;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class HTTPSClient extends DefaultHttpClient {

    protected SSLSocketFactory socketFactory;

    /**
     * 初始化HTTPSClient
     * 
     * @return 返回当前实例
     * @throws Exception
     */
    public HTTPSClient init() throws Exception {
        this.prepareCertificate();
        this.regist();

        return this;
    }

    /**
     * 准备证书验证
     * 
     * @throws Exception
     */
    public abstract void prepareCertificate() throws Exception;

    /**
     * 注册协议和端口, 此方法也可以被子类重写
     */
    protected void regist() {
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, socketFactory));
    }
}

