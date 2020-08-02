package com.example.kiylx.ti.tool.networkpack;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.xapplication.Xapplication;

import java.util.Map;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/9 14:11
 */
public class NetworkLiveData extends LiveData<NetState> {
    private static NetworkLiveData mInstance;
    private static final String TAG = "监听网络livedata";

    private Map<NetState, Boolean> netWorkState;//网络状态

    private ConnectivityManager connectivityManager;
    private NetworkRequest request;
    private ConnectivityManager.NetworkCallback networkCallback;

    public static NetworkLiveData getInstance() {
        LogUtil.d(TAG, "NetworkLiveData: getInstance初始化");
        if (mInstance == null) {
            synchronized (NetworkLiveData.class) {
                if (mInstance == null) {
                    mInstance = new NetworkLiveData();
                }
            }
        }
        return mInstance;
    }

    private NetworkLiveData() {
        LogUtil.d(TAG, "NetworkLiveData: 初始化");
        request = new NetworkRequest.Builder().build();
        connectivityManager = (ConnectivityManager) Xapplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new NetWorkCallbackImpl();

    }


    @Nullable
    @Override
    public NetState getValue() {
        return super.getValue();
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }

    }

    @Override
    protected void onInactive() {
        super.onInactive();
        connectivityManager.unregisterNetworkCallback(networkCallback);

    }


    /**
     * 监听网络状况的变化
     */

    public class NetWorkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            LogUtil.d(TAG, "onAvailable: ");
            getInstance().postValue(NetState.ON);

        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);

            LogUtil.d(TAG, "onLost: ");
            getInstance().postValue(NetState.OFF);

        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }

        /**
         * 当网络连接到此请求的框架更改功能但仍满足规定的需求时调用。
         *
         * @param network             network
         * @param networkCapabilities networkCapabilities
         *                            <p>
         *                            network:
         *                            标识网络。
         *                            通过ConnectivityManager.NetworkCallback将其提供给应用程序，以响应主动的requestNetwork（NetworkRequest，PendingIntent）或被动的registerNetworkCallback（NetworkRequest，PendingIntent）调用。
         *                            它用于将流量定向到给定的网络，或者基于目标SocketFactory在Socket上，或者通过bindProcessToNetwork（Network）在整个进程范围内。
         *                            <p>
         *                            networkCapabilities:
         *                            此类表示网络的功能。 这既用于指定ConnectivityManager的需求，又用于检查网络。
         *                            请注意，这取代了旧的TYPE_MOBILE网络选择方法。
         *                            该应用程序应指定需要高带宽，而不是因为应用程序需要高带宽并在出现新的快速网络（如LTE）时有过时的风险而表明需要Wi-Fi，而是应指定它需要高带宽。
         *                            同样，如果应用程序需要不计量的网络进行批量传输，则可以指定而不是假设对所有基于蜂窝的连接进行计量而对所有基于Wi-Fi的连接均不进行计量。</p>
         */
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {//测试此实例的能力
                LogUtil.d(TAG, "onCapabilitiesChanged: 开始");
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    getInstance().postValue(NetState.DATA);
                    LogUtil.d(TAG, "onCapabilitiesChanged: data");
                    //移动数据
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {//测试实例有无这个传输存在
                    //wifi
                    getInstance().postValue(NetState.WIFI);
                    LogUtil.d(TAG, "onCapabilitiesChanged: wifi");
                } else {
                    getInstance().postValue(NetState.OTHER);
                }

            }

        }

        /**
         * 描述网络链接的属性。 链接表示与网络的连接。
         * 它可能具有多个地址和多个网关，多个dns服务器，但只有一个http代理和一个网络接口。
         * 请注意，这只是数据的持有者。 对其进行修改不会影响实时网络。
         *
         * @param network
         * @param linkProperties
         */
        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    }
}
