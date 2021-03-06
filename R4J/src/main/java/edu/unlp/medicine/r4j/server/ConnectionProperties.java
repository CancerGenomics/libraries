package edu.unlp.medicine.r4j.server;

/**
 * Created by diego on 31/10/15.
 */
public class ConnectionProperties {
    /**
     * No usa proxy, ni tiene ninguna propiedad especial la conexión
     */
    public static final ConnectionProperties NONE = new ConnectionProperties();

    private int proxyPort;
    private String proxyHost;
    private boolean usingProxy = false;
    public ConnectionProperties(String proxy, int port){
        this.usingProxy =proxy!=null && !proxy.trim().isEmpty();
        this.proxyHost=proxy;
        this.proxyPort=port;
    }

    private ConnectionProperties(){}

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public boolean isUsingProxy() {
        return usingProxy;
    }
}
