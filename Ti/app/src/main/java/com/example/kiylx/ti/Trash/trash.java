package com.example.kiylx.ti.trash;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/29 17:44
 */
public class trash {

/* public void saveBitmap(String url) {
        InputStream is = null;
        Bitmap bm = null;
        Response response = null;

        try {
            //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
           File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();

            response = OkhttpManager.newInstance().getImgResponse(url);
            is = Objects.requireNonNull(response.body()).byteStream();

            byte[] b = new byte[1024];
            int len = 0;

            //File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(folder,  TimeProcess.getTime2() + ".jpg");
            RandomAccessFile rf = new RandomAccessFile(file, "rw");

            while ((len = is.read(b)) != -1) {
                rf.write(b, 0, len);
            }
            rf.close();
            response.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "解析失败", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }*/

/*
    public void saveBitmap(String url){
        InputStream inputStream=null;
        Bitmap bm = null;
        Response response=null;
        try {
            response = OkhttpManager.newInstance().getCall(extra).execute();
            inputStream = response.body().byteStream();
            bm = BitmapFactory.decodeStream(inputStream);
            saveImg(bm, TimeProcess.getTime2()+".jpg");//以时间命名

        }catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            response.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private void saveImg(Bitmap bm, String fileName) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        try {
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdir();

            }
            Log.d(TAG, "保存图片: " + path + fileName);
            File file = new File(folder, fileName);

            FileOutputStream op = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, op);

            op.flush();
            op.close();
            onSaveSuccess(file);
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }

    }

    private void onSaveSuccess(final File file) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    /*@SuppressLint("CheckResult")
    public void saveImage(String url) {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
                e.onNext(Glide.with(getApplicationContext())
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        //获取到下载得到的图片，进行本地保存
                        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        String fileName = System.currentTimeMillis() + ".jpg";
                        File destFile = new File(appDir, fileName);
                        //把gilde下载得到图片复制到定义好的目录中去
                        copy(file, destFile);

                        // 最后通知图库更新
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(new File(destFile.getPath()))));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                });
    }*/

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    /*public void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/


    /**
     * @param text text文本
     *             复制文本到剪切板
     */
    /*public void clipData(String text) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("网址", text);
        manager.setPrimaryClip(clip);


    }*/

}
