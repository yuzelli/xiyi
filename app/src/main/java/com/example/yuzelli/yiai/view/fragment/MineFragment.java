package com.example.yuzelli.yiai.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuzelli.yiai.R;
import com.example.yuzelli.yiai.base.BaseFragment;
import com.example.yuzelli.yiai.bean.UserInfo;
import com.example.yuzelli.yiai.constants.ConstantUtils;
import com.example.yuzelli.yiai.https.OkHttpClientManager;
import com.example.yuzelli.yiai.uitls.BaiduLoading;
import com.example.yuzelli.yiai.uitls.ImageUtils;
import com.example.yuzelli.yiai.uitls.LxQiniuUploadUtils;
import com.example.yuzelli.yiai.uitls.SharePreferencesUtil;
import com.example.yuzelli.yiai.view.activity.LoginActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;


public class MineFragment extends BaseFragment {
    private RelativeLayout rl_head;         //头像
    private RelativeLayout rl_userName;    //用户名
    private RelativeLayout rl_phoneNum;    //注册电话
    private RelativeLayout rl_address;     //用户地址
    private Button btn_finish;             //完成
    private ImageView rw_head;             //头像
    private TextView tv_userName;           //用户名
    private TextView tv_phoneNum;           //注册电话
    private TextView tv_address;           //用户地址
    private TextView tv_exit;           //用户退出


    private Context context;
    private UserInfo userInfo;
    private PersonCompileHandler handler;

    /**
     * 定义三种状态
     */
    private static final int HEAD_PORTRAIT_PIC = 1;//相册
    private static final int HEAD_PORTRAIT_CAM = 2;//相机
    private static final int HEAD_PORTRAIT_CUT = 3;//图片裁剪
    private File photoFile;
    private Bitmap photoBitmap;

    private PopupWindow mPopWindow;
    private String addressCur;
    private String userHeadImgUrl;
    @Override
    protected int layoutInit() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void bindEvent(View view) {
        context = getActivity();
        handler = new PersonCompileHandler();
        userInfo = (UserInfo) SharePreferencesUtil.readObject(context,ConstantUtils.USER_LOGIN_INFO);
        rl_head = (RelativeLayout) view.findViewById(R.id.rl_head);
        rl_userName = (RelativeLayout) view.findViewById(R.id.rl_userName);
        rl_address = (RelativeLayout) view.findViewById(R.id.rl_address);
        tv_exit = (TextView) view.findViewById(R.id.tv_exit);
        rw_head = (ImageView) view.findViewById(R.id.rw_head);
        tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        tv_phoneNum = (TextView) view.findViewById(R.id.tv_phoneNum);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        btn_finish = (Button) view.findViewById(R.id.btn_finish);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().loadImage(userInfo.getU_imgurl(),options,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                rw_head.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        tv_address.setText(userInfo.getU_address());
        tv_phoneNum.setText(userInfo.getU_phone());
        tv_userName.setText(userInfo.getU_name());

        rl_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        rl_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserName(tv_userName);
            }
        });
        rl_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserName(tv_address);
            }
        });
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpDataUserInfo();
            }
        });
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferencesUtil.saveObject(context, ConstantUtils.USER_LOGIN_INFO, null);
                getActivity().finish();
                LoginActivity.actionStart(getActivity());
            }
        });

    }

    /**
     * 根新用户信息：
     */
    private void doUpDataUserInfo() {
        OkHttpClientManager manager = OkHttpClientManager.getInstance();
        StringBuffer buffer = new StringBuffer(ConstantUtils.USER_ADDRESS).append(ConstantUtils.USER_REGISTER);
        Map<String, String> map = new HashMap<>();
        map.put("type", "updateUser");
        map.put("user_id",userInfo.getUser_id()+"");
        map.put("u_phone", userInfo.getU_phone());
        map.put("u_password", userInfo.getU_password());
        map.put("u_imgurl", userHeadImgUrl);
        map.put("u_address", tv_address.getText().toString().trim());
        map.put("u_name", tv_userName.getText().toString().trim());
        String url = OkHttpClientManager.attachHttpGetParams(buffer.toString(), map);
        manager.getAsync(url, new OkHttpClientManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                showToast("加载网路数据失败！");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Gson gson = new Gson();
                JSONObject object = new JSONObject(result);
                String flag = object.getString("error");
                if (flag.equals("ok")) {
                    String body = object.getString("object");
                    userInfo = gson.fromJson(body, UserInfo.class);
                    handler.sendEmptyMessage(ConstantUtils.REGISTER_GET_DATA);
                } else {
                    showToast("更新失败！");
                }
            }
        });

    }

    private void setUserName(final TextView txv) {
        final Dialog dialog = new Dialog(getActivity(),R.style.PhotoDialog);
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.personal_head_select_diallog,null);
        dialog.setContentView(view);
        TextView tv_Cancel =  (TextView)view.findViewById(R.id.tv_cancel);
        TextView tv_ok =  (TextView)view.findViewById(R.id.tv_ok);
        final EditText et_input =  (EditText) view.findViewById(R.id.et_input);
        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_input.getText().toString().trim();
                if (content!=null&&!content.equals("")){
                    txv.setText(content);
                }else {
                    showToast("请输入");
                }
                dialog.dismiss();

            }
        });

        dialog.show();

    }


    @Override
    protected void fillData() {

    }

    //显示Dialog选择拍照还是从相册选择
    private void showPhotoDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.PhotoDialogTWO);
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.diallog_personal_head_select, null);
        dialog.setContentView(view);
        TextView tv_PhotoGraph = (TextView) view.findViewById(R.id.tv_personal_photo_graph);
        TextView tv_PhotoAlbum = (TextView) view.findViewById(R.id.tv_personal_photo_album);
        TextView tv_Cancel = (TextView) view.findViewById(R.id.tv_cancel);

        tv_PhotoGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoGraph();
            }
        });

        tv_PhotoAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoAlbum();
            }
        });

        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //设置出现Dialog位置
        Window window = dialog.getWindow();
        // 可以在此设置显示动画
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        dialog.show();
    }

    //打开相册方法
    private void openPhotoAlbum() {
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, HEAD_PORTRAIT_PIC);
    }

    //打开相机方法
    private void openPhotoGraph() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
            photoFile = new File(file, System.currentTimeMillis() + "");

            Uri photoUri = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, HEAD_PORTRAIT_CAM);
        } else {

            Toast.makeText(getActivity(), "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case HEAD_PORTRAIT_CAM:
                    startPhotoZoom(Uri.fromFile(photoFile));
                    break;
                case HEAD_PORTRAIT_PIC:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());
                    break;
                case HEAD_PORTRAIT_CUT:
                    if (data != null) {
                        photoBitmap = data.getParcelableExtra("data");
                        rw_head.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        rw_head.setImageBitmap(photoBitmap);
                        BaiduLoading.onBeiginDialog(getActivity());
                        try {
                            File SDCardRoot = Environment.getExternalStorageDirectory();
                            if (ImageUtils.saveBitmap2file(photoBitmap)) {

                                String photoPath = SDCardRoot + ConstantUtils.AVATAR_FILE_PATH;
                                //doUploadPicture(photoPath);
                                final String StouserHeadImgName = userInfo.getU_phone() + "_" + System.currentTimeMillis();

                                LxQiniuUploadUtils.uploadPic("yuzelloroom", photoPath, StouserHeadImgName, new LxQiniuUploadUtils.UploadCallBack() {
                                    @Override
                                    public void sucess(String url) {
                                        Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
                                        userHeadImgUrl = ConstantUtils.QN_IMG_ADDRESS + StouserHeadImgName;
                                        BaiduLoading.onStopDialog();
                                    }

                                    @Override
                                    public void fail(String key, ResponseInfo info) {
                                        Toast.makeText(context, "shibeile", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;
            }
        }
    }

    /**
     * 打开系统图片裁剪功能
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); //黑边
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, HEAD_PORTRAIT_CUT);
    }

    class PersonCompileHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.REGISTER_GET_DATA:
                    SharePreferencesUtil.saveObject(context, ConstantUtils.USER_LOGIN_INFO, userInfo);
                    showToast("更新成功！");
                    break;

                default:
                    break;
            }
        }
    }
}
