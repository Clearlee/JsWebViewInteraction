package com.clearlee.JsWebViewInteraction;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zerdoor_pc on 2016/5/12.
 * 打开相机进行摄像
 */
public class CameraUtil {
    /**
     * 打开系统相机(默认路径)*
     */
    public static void openCamera(int Code) {
        if (!isExitsSdcard()) {
            String st = "SD卡不存在，不能拍照";
            Toast.makeText(BaseActivity.currAct, st, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Environment.getExternalStorageDirectory(), "image_temp.jpg")));
        BaseActivity.currAct.startActivityForResult(intent, Code);
    }

    /**
     * 打开系统相机(设置路径)*
     */
    public static void openCamera(int Code, String path) {
        if (!isExitsSdcard()) {
            String st = "SD卡不存在，不能拍照";
            Toast.makeText(BaseActivity.currAct, st, Toast.LENGTH_SHORT).show();
            return;
        }
//        File cameraFile = new File(ClipPathUtil.getInstance().getImagePath(), MyApp.AppName
//                + System.currentTimeMillis() + ".jpg");
//        cameraFile.getParentFile().mkdirs();
//        MyApp.activity.startActivityForResult(
//                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
//                Code);
    }
    /**
     * 打开系统相机(设置路径)*
     */
    public static  void pickPhoto(int Code) {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            BaseActivity.currAct.startActivityForResult(intent, Code);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                BaseActivity.currAct.startActivityForResult(intent, Code);
            } catch (Exception e2) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }



    /**选择相片**/
    public static  void selectPicFromCamera(int Code,String path) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Environment.getExternalStorageDirectory(), path)));
        BaseActivity.currAct.startActivityForResult(intent, Code);
    }




    /**
     * 存储图片*
     */
    public static void saveImage(String path, String newPath, String newName) {
        try {
            File mpath = new File(newPath);
            //文件
            newPath = newPath + "/" + newName;
            File file = new File(newPath);
            if (!mpath.exists()) {
                mpath.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            saveImage(createBitmap(path), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 存储图片*
     */
    public static void saveImage(String path, String newPath) {
        try {

            //文件
            File file = new File(newPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            saveImage(createBitmap(path), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 存储图片(bitmap)*
     */
    public static void saveImage(Bitmap b, File filePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 60, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    /**
     * 存储文件(io)单个*
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();

            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }

    }

    /**
     * 创建BitMap*
     */

    public static Bitmap createBitmap(String path) {
        if (path == null) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        FileInputStream is = null;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(path);

            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }


    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示*
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值*
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 存储*
     */
    public static void saveToString(String filePath, String newPath) {
        copyFile(filePath,newPath);//复制图片到新路径
        Bitmap bm = getSmallBitmap(newPath);
        compressImage(bm);
        saveBitmapFile(bm, newPath);
    }

    public static String saveToString(String filePath) {
        String newPath=ClipPathUtil.getPathForImage();
        copyFile(filePath, newPath);//复制图片到新路径
        Bitmap bm = getSmallBitmap(filePath);
        compressImage(bm);
        saveBitmapFile(bm, newPath);
        return newPath;
    }
    public static String saveToString(Bitmap bm) {
        String newPath=ClipPathUtil.getPathForImage();
//        copyFile(filePath, newPath);//复制图片到新路径
//        Bitmap bm = getSmallBitmap(filePath);
        compressImage(bm);
        saveBitmapFile(bm, newPath);
        return newPath;
    }



    public static Bitmap compressImage(Bitmap image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
            return bitmap;
        } catch (Exception e) {
            return image;
        }

    }

    public static void saveBitmapFile(Bitmap bitmap, String newPath) {
        File file = new File(newPath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 通过uri获取文件路径
     *
     * @param mUri
     * @return
     */
    public static String getFilePath(Context context,Uri mUri) {

        String picPath;
        if (Build.VERSION.SDK_INT >= 19) {
            picPath = GetPhotoPathByUrl.getPath(context, mUri);//通过url得到路径

        } else {
            picPath = GetPhotoPathByUrl.getDataColumn(context, mUri, null, null);

        }
        return picPath;

    }
}
