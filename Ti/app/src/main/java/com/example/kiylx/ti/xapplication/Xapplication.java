package com.example.kiylx.ti.xapplication;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.example.kiylx.ti.conf.StateManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/6 11:09
 */
public class Xapplication extends Application {
    private static Context mContext;
    private StateManager stateManager;//持有一些状态标志

    private String cer = null;
    private String realCer = null;
    private static final String TAG = "SignCheck";


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initData();
    }

    /**
     * 初始化全局数据
     */
    private void initData() {
        stateManager = new StateManager(mContext);

    }

    public static Context getInstance() {
        return mContext;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    String checkSign() {
        PackageManager pm = mContext.getPackageManager();

        String packageName = mContext.getPackageName();
        int flag = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flag);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();

        InputStream inputStream = new ByteArrayInputStream(cert);

        CertificateFactory cf = null;

        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;

        try {
            c = (X509Certificate) cf.generateCertificate(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String hexString = null;

        try {
            //加密算法的类，这里的参数可以使 MD4,MD5 等加密算法
            MessageDigest md = MessageDigest.getInstance("SHA1");

            //获得公钥
            byte[] publicKey = md.digest(c.getEncoded());

            //字节到十六进制的格式转换
            hexString = byte2HexFormatted(publicKey);

        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;

    }

    //这里是将获取到得编码进行16 进制转换
    private String byte2HexFormatted(byte[] arr) {

        StringBuilder str = new StringBuilder(arr.length * 2);

        for (int i = 0; i <arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l =h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    /**
     * 检测签名是否正确
     * @return true 签名正常 false 签名不正常
     */
    public boolean check() {

        if (this.realCer != null) {
            cer = cer.trim();
            realCer = realCer.trim();
            if (this.cer.equals(this.realCer)) {
                return true;
            }
        }else {
            Log.e(TAG, "未给定真实的签名 SHA-1 值");
        }
        return false;
    }
}
