package com.crystal.customview.fileindexview;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.crystal.customview.R;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileTools {
    private static final String TAG = "文件目录管理";
    private static final Charset charset = Charset.forName("GBK");

    /**
     * @param sourceFileName   待压缩文件的路径名称
     * @param outPutPath       输出到哪个文件夹
     * @param name             压缩文件后的名称，可不填写
     * @param isDelSrcFile     压缩后是否删除源文件
     * @param keepDirStructure 是否保留原目录结构
     * @throws ZipException
     * @throws IOException
     */
    public static void zipFile(String sourceFileName, String outPutPath, @Nullable String name, boolean isDelSrcFile, boolean keepDirStructure) throws ZipException, IOException {
        //待压缩文件
        File sourceFile = new File(sourceFileName);
        if (!sourceFile.exists()) {
            throw new IOException("文件不存在");
        }
        //压缩到该目录
        File outPutFolder = new File(outPutPath);
        if (!outPutFolder.exists()) {
            outPutFolder.mkdirs();
        }

        if (avoid(sourceFileName, outPutPath)) {
            throw new IOException("源文件路径不可以是目标路径的父路径" + sourceFileName + "--->" + outPutPath);
        }

        String finallyName ;//压缩文件最终的名称
        if (name == null || name.isEmpty()) {
            finallyName = sourceFileName.substring(sourceFileName.lastIndexOf("/"));
        } else {
            finallyName = name;
        }
        if (!outPutPath.endsWith("/") && !finallyName.startsWith("/")) {//若路径结尾没有/",加上它。
            outPutPath = outPutPath + "/";
        }
        String zipFileName = outPutPath + finallyName + ".zip";//压缩文件路径名称
        System.out.println(
                "路径：" + outPutPath + "\n"
                        + "文件名：" + finallyName + "\n"
                        + "文件路径：" + zipFileName + "\n");

        File zipFile = new File(zipFileName);//预创建压缩文件
        try(OutputStream os = new FileOutputStream(zipFile)) {
            //将文件压缩并写入上面构建的zipFile文件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                zipFile(sourceFile, os, sourceFile.getName(), isDelSrcFile, keepDirStructure);
            }
        }
    }

    /**
     * @param sourceFile       压缩文件
     * @param outputStream     将压缩文件输出到流
     * @param name             文件夹节点名称,也就是sourceFile的name
     * @param isDelSrcFile     是否删除源文件
     * @param keepDirStructure 是否保持原目录结构
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void zipFile(File sourceFile, OutputStream outputStream, String name, boolean isDelSrcFile, boolean keepDirStructure) {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream, charset)) {
            compress(sourceFile, zos, name, keepDirStructure);
            if (isDelSrcFile) {
                deleteFile(sourceFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             文件夹节点名称
     *                         压缩D:/we 则，name就填写we。也就是sourceFile的name
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws IOException {
        System.out.println("压缩时的节点名称：" + name);
        if (sourceFile.isFile()) {
            byte[] b = new byte[1024];
            int len;
            zos.putNextEntry(new ZipEntry(name));

            try (BufferedOutputStream bos=new BufferedOutputStream(zos);
                 FileInputStream is = new FileInputStream(sourceFile);BufferedInputStream bis=new BufferedInputStream(is)) {
                while ((len = bis.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                zos.closeEntry();
            }
        } else {
            if (keepDirStructure) {
                //若是文件夹，把文件夹节点写进去
                zos.putNextEntry(new ZipEntry(name+"/"));
                zos.closeEntry();
            }
            //是一个文件夹下有文件夹和文件的待压缩目录
            File[] files = sourceFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files)
                    if (keepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void unZipFile(String fileName, String unZipFilePath) throws IOException {
        //待解压文件
        File zipFile = new File(fileName);
        if (!zipFile.exists()) {
            throw new IOException("文件不存在");
        }
        //解压到该目录
        File pathFile = new File(unZipFilePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        //压缩文件
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));//zip文件
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            try (InputStream is = zip.getInputStream(zipEntry);BufferedInputStream bis=new BufferedInputStream(is)) {
                //输出目录
                String outPath = unZipFilePath + "/" + zipEntry.getName();
                System.out.println("outPath：" + outPath + "\n" + "实体：" + zipEntry.getName());
                /*
                 * 如果当前zipEntry是文件夹，最后是有一个斜杠：
                 * outPath：D:/we/新建文件夹 (2)/
                 * 实体：新建文件夹 (2)/
                 * 本轮filename:新建文件夹 (2)
                 *
                 * 如果是文件，最后是没有斜杠的:
                 * outPath：D:/we/新建文件夹 (2)/123459.txt
                 * 实体：新建文件夹 (2)/123459.txt
                 * 本轮filename:新建文件夹 (2)
                 *
                 * 所以，若是文件夹，去除最后一个斜杠，则表示这个文件夹的路径
                 * 若是文件，去除最后一个斜杠，表示的是这个文件的父文件夹
                 * */
                File file = new File(outPath.substring(0, outPath.lastIndexOf("/")));
                System.out.println("本轮filename:" + file.getName());
                if (!file.exists()) {//若是当前文件夹或当前文件的父文件夹不存在，创建文件夹
                    file.mkdirs();
                }
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                try (OutputStream outputStream = new FileOutputStream(outPath);BufferedOutputStream bos=new BufferedOutputStream(outputStream)) {
                    byte[] b = new byte[1024];
                    int len = 0;
                    while ((len = bis.read(b)) != -1) {
                        bos.write(b,0,len);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param fileName 文件路径
     * @return 若文件存在，返回该路径表示的file
     * @throws FileNotFoundException
     */
    public static File copyFile(String fileName) throws FileNotFoundException {
        File selectFile = new File(fileName);
        if (!selectFile.exists()) {
            throw new FileNotFoundException("文件不存在");
        }
        return selectFile;
    }

    /**
     * @param sourceFileName 待复制的文件
     * @param targetPath     目标文件夹
     */
    public static void pauseFile(String sourceFileName, String targetPath) throws IOException {
        //待复制文件不存在抛出异常
        File sourceFile = new File(sourceFileName);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("待复制文件不存在");
        }
        //目标目录不存在，创建目录
        File targetFolder = new File(targetPath);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        if (avoid(sourceFileName, targetPath))
            throw new IOException("源文件路径不可以是目标路径的父路径");
        try {
            pause(sourceFile, targetFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param sourceFile 源文件
     * @param targetPath 复制源文件到此目录下，也就是源文件的父级
     */
    private static void pause(File sourceFile, File targetPath) throws IOException {

        if (sourceFile.isFile()) {
            byte[] b = new byte[1024];
            File targetFile = new File(targetPath.getPath() + "/" + sourceFile.getName());
            try (BufferedInputStream bis=new BufferedInputStream(new FileInputStream(sourceFile));
                 BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(targetFile))) {
                int len = 0;
                while ((len = bis.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
            }catch (IndexOutOfBoundsException | IOException f){
                f.printStackTrace();
            }
        } else {//若源文件是文件夹，在targetPath下新建此文件夹，然后遍历此文件夹
            String nextTargetPath = targetPath.getPath() + "/" + sourceFile.getName();
            File file = new File(nextTargetPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //遍历文件夹
            File[] files = sourceFile.listFiles();
            if (files != null && files.length > 0) {
                for (File next : files) {
                    System.out.println("目标路径名称:" + nextTargetPath);
                    pause(next, new File(nextTargetPath));
                }
            }
        }
    }

    public static void moveFile(String fileName, String targetPath) {

    }

    public static void deleteFile(String fileName) {

    }

    /**
     * @param sourcePath 源文件路径
     * @param targetPath 目标路径
     * @return 避免两个路径之间有关系，例如：1.有上下级关系；2.两个路径是相同的。若两个路径没有关系，返回true
     * 例如：
     * 源文件路径 D:/ONE
     * 目标路径 D:/ONE/TWO
     * 因此two是one的子目录，即为有关系，因此不可以压缩或是复制源文件到目标路径
     */
    private static boolean avoid(String sourcePath, String targetPath) throws FileNotFoundException {
        boolean result=false;
        File one = new File(sourcePath);
        File two = new File(targetPath);
        if (!one.exists() || !two.exists()) {
            throw new FileNotFoundException("文件不存在");
        }

        String parentPath = one.getAbsolutePath();
        String childPath = two.getAbsolutePath();

        String[] parent = parentPath.split("/");
        String[] child = childPath.split("/");
        for (int i = 0; i < parent.length; i++) {
            result= parent[i].equals(child[i]);
        }

        return result;
    }
}
