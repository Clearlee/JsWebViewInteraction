package com.clearlee.JsWebViewInteraction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ZerdoorDLPAPC on 2016/9/28.
 * 打开系统相册
 */
public class IntentAlbumUtils {
    static IntentAlbumUtils instence;

    public static IntentAlbumUtils getInstence() {
        if (instence == null) {
            instence = new IntentAlbumUtils();
        }
        return instence;
    }

    /**
     * 打开系统相册
     *
     * @param activity
     * @param reqCode
     */
    int currReqCode;

    public void openSystemGallery(Activity activity, int reqCode) {
//        Intent intent;
//        if (Build.VERSION.SDK_INT < 19) {
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//
//        } else {
//            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        }
        currReqCode = reqCode;
//        activity.startActivityForResult(intent, reqCode);
        tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
//        showDialog(activity, PHOTO_REQUEST_TAKEPHOTO, PHOTO_REQUEST_GALLERY, tempFile);
        showDialog(activity, reqCode, reqCode + 1111, tempFile);
    }


    public int getCode() {
        return currReqCode;
    }

    /**
     * 选择系统图片
     *
     * @param activity
     * @param PHOTO_REQUEST_TAKEPHOTO
     * @param PHOTO_REQUEST_GALLERY
     */
    public void openChoiceGallery(Activity activity, final int PHOTO_REQUEST_TAKEPHOTO, final int PHOTO_REQUEST_GALLERY) {
        tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        showDialog(activity, PHOTO_REQUEST_TAKEPHOTO, PHOTO_REQUEST_GALLERY, tempFile);


    }

    // 创建一个以当前时间为名称的文件
    public File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    // 使用系统当前日期加以调整作为照片的名称
    private static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    //提示对话框方法
    private static void showDialog(final Activity activity, final int PHOTO_REQUEST_TAKEPHOTO, final int PHOTO_REQUEST_GALLERY, final File file) {

        new AlertDialog.Builder(activity)
                .setTitle("图片选择")
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        // 调用系统的拍照功能
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        activity.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    }
                })
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
    }


    public static final int PHOTO_REQUEST_TAKEPHOTO = 1000;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2000;// 从相册中选择
    public static final int PHOTO_REQUEST_CUT = 3000;// 结果

    public void onActivityResult(int requestCode, int resultCode, Intent data, PhoteSuccess photeSuccess) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:
//                    startPhotoZoom(Uri.fromFile(tempFile), 400);
                    startScrop(Uri.fromFile(tempFile));
                    break;
                case UCrop.REQUEST_CROP:
                    if (data != null)
                        handleCropResult(data, photeSuccess);
                    break;
                case PHOTO_REQUEST_GALLERY:
                    if (data != null)
                        startScrop(data.getData());
//                        startPhotoZoom(data.getData(), 400);
                    break;

                case PHOTO_REQUEST_CUT:
                    if (data != null)
                        setPicToView(curruri, photeSuccess);
                    break;
            }

        }


    }

    private LoadingUtil loadingUtil;

    public LoadingUtil getLoadingUtil() {
        return loadingUtil;
    }

    private void handleCropResult(Intent result, final PhoteSuccess photeSuccess) {
        final Uri resultUri = UCrop.getOutput(result);
        try {
            if (resultUri == null) return;
            // 读取uri所在的图片
//            Bitmap photo = MediaStore.Images.Media.getBitmap(MyApp.getApp().activity.getContentResolver(), resultUri);
//            String path = CameraUtil.saveToString(photo);

            String cutPath = resultUri.getPath();

            loadingUtil = new LoadingUtil(BaseActivity.currAct);
            Luban.with(BaseApplication.getInstance())
                    .load(new File(cutPath))                        // 传人要压缩的图片列表
                    .ignoreBy(500)                                  // 忽略不压缩图片的大小
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                            loadingUtil.start("处理中");
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                            if (photeSuccess != null) {
                                photeSuccess.setPhote(file.getPath());
                                photeSuccess.setFile(file);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
                            loadingUtil.closeDialog();
                        }
                    }).launch();    //启动压缩

//            if (photeSuccess != null) photeSuccess.setPhote(cutPath);
//
//            if (photeSuccess != null) photeSuccess.setFile(new File(cutPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startScrop(Uri data) {
        LogUtils.v("handleCropResult startScrop");
        String newPath = ClipPathUtil.getPathForImage();
        curruri = Uri.fromFile(new File(newPath));
        if (data != null) {
            startCropActivity(data);
        }

    }

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private void startCropActivity(@NonNull Uri uri) {

        LogUtils.v("startCropActivity");

        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(BaseApplication.getInstance().getCacheDir(), System.currentTimeMillis() + destinationFileName)));

        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);

        uCrop.start(BaseActivity.currAct);
    }

    private UCrop advancedConfig(UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setStatusBarColor(BaseApplication.getInstance().getResources().getColor(android.R.color.black));
        options.setToolbarColor(BaseApplication.getInstance().getResources().getColor(android.R.color.white));
        options.setToolbarWidgetColor(BaseApplication.getInstance().getResources().getColor(android.R.color.black));
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(true);
        return uCrop.withOptions(options);
    }

    private UCrop basisConfig(UCrop uCrop) {
        //支持1:1 3:4 3:2 16:9
        uCrop = uCrop.useSourceImageAspectRatio();
        return uCrop;
    }

    Uri curruri = null;

    /**
     * 设置不返回数据
     **/
    public void startPhotoZoomNoData(Uri uri, int size) {
        int dp = size;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);//输出是X方向的比例
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", dp);//输出X方向的像素
        intent.putExtra("outputY", dp);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, curruri);
        intent.putExtra("return-data", false);//设置为不返回数据

        BaseActivity.currAct.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 设置不返回数据（获取方式）
     **/
    public void setPicToView(Uri uri, PhoteSuccess photeSuccess) {
        try {
            if (uri == null) return;
            // 读取uri所在的图片
            Bitmap photo = MediaStore.Images.Media.getBitmap(BaseApplication.getInstance().getContentResolver(), uri);
            String path = CameraUtil.saveToString(photo);
            if (photeSuccess != null) photeSuccess.setPhote(path);

            if (photeSuccess != null) photeSuccess.setFile(new File(path));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void startPhotoZoom(Uri uri, int size) {
        String newPath = ClipPathUtil.getPathForImage();
        curruri = Uri.fromFile(new File(newPath));
        startPhotoZoomNoData(uri, size);


//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        // crop为true是设置在开启的intent中设置显示的view可以剪裁
//        intent.putExtra("crop", "true");
//
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//
//        // outputX,outputY 是剪裁图片的宽高
//        intent.putExtra("outputX", size);
//        intent.putExtra("outputY", size);
//        intent.putExtra("return-data", true);
//
//        MyApp.getApp().activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata, PhoteSuccess photeSuccess) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            String path = CameraUtil.saveToString(photo);
            if (photeSuccess != null) photeSuccess.setPhote(path);

            if (photeSuccess != null) photeSuccess.setFile(new File(path));
        }
    }


    public interface PhoteSuccess {

        public void setPhote(String path);

        public void setFile(File file);
    }


}
