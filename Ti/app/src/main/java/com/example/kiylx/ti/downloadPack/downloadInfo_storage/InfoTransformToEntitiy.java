package com.example.kiylx.ti.downloadPack.downloadInfo_storage;

import com.example.kiylx.ti.downloadPack.base.DownloadInfo;
import com.example.kiylx.ti.downloadPack.dFragments.SimpleDownloadInfo;

/**
 * downloadinfo和downloadentity互相转换的类
 */
public class InfoTransformToEntitiy {
    /**
     * @param info 下载信息
     * @return 转换成DownloadEntity，方便存入数据库。
     */
    public static DownloadEntity transformToEntity(DownloadInfo info) {
        return new DownloadEntity(
                info.getUrl(),
                info.getFileName(),
                info.getPath(),
                info.getBlockCompleteNum(),
                info.getBlockPauseNum(),
                BooleanToString(info.isPause()),
                BooleanToString(info.isCancel()),
                BooleanToString(info.isWaitDownload()),
                info.getThreadNum(),
                tString(info.splitStart),
                tString(info.splitEnd),
                info.getContentLength(),
                info.getCurrentLength(),
                info.getBlockSize(),
                BooleanToString(info.isDownloadSuccess()));
    }

    /**
     * @param entity 下载信息
     * @return 转换成DownloadEntity，方便存入数据库。
     */
    public static DownloadInfo transformToInfo(DownloadEntity entity) {
        return new DownloadInfo(
                entity.url,
                entity.filename,
                entity.path,
                entity.blockCompleteNum,
                entity.blockPauseNum,
                StringTobollean(entity.pause),
                StringTobollean(entity.cancel),
                StringTobollean(entity.waitDownload),
                entity.threadNum,
                lString(entity.splitStart),
                lString(entity.splitEnd),
                entity.contentLength,
                entity.currentLength,
                entity.blockSize,
                StringTobollean(entity.downloadSuccess));
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

    public static String BooleanToString(boolean b) {
        return b ? "true" : "false";
    }

    public static boolean StringTobollean(String s) {
        return s.equals("true");
    }

    public static SimpleDownloadInfo transformToSimple(DownloadEntity entity) {
        return new SimpleDownloadInfo(
                 entity.filename,
                entity.url,
                StringTobollean(entity.pause),
                StringTobollean(entity.waitDownload),
                StringTobollean(entity.cancel),
                (float) entity.currentLength / entity.contentLength);


    }
}
