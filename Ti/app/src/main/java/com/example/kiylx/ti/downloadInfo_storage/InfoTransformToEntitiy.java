package com.example.kiylx.ti.downloadInfo_storage;

import com.example.kiylx.ti.corebase.DownloadInfo;

public class InfoTransformToEntitiy {
    /**
     * @param info 下载信息
     * @return 转换成DownloadEntity，方便存入数据库。
     */
    public static DownloadEntity transformInfo(DownloadInfo info) {
        return new DownloadEntity(
                info.getUrl(),
                info.getFileName(),
                info.getPath(),
                info.getBlockCompleteNum(),
                info.getBlockPauseNum(),
                info.getPauseFlags(),
                info.getThreadNum(),
                tString(info.splitStart),
                tString(info.splitEnd),
                info.getContentLength(),
                (float) (info.getTotalLength() / info.getContentLength()),
                info.getBlockSize(),
                info.getDownloadSuccess());
    }

    /**
     * @param l long数组
     * @return 转成的字符串
     */
    public static String tString(long[] l) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < l.length; i++) {
            builder.append(l[i]).append("/");
        }
        return builder.toString();
    }

    /**
     * @param num 字符串
     * @return 返回字符串转成的long数组
     */
    public static long[] lString(String num) {
        String[] ss = num.split("/");
        long[] ll = new long[ss.length];
        for (int i = 0; i < ss.length; i++) {

            ll[i] = Long.parseLong(ss[i], 10);
        }

        return ll;
    }

}
