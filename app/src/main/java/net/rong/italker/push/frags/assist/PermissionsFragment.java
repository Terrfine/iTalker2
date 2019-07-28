package net.rong.italker.push.frags.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.rong.italker.common.app.Application;
import net.rong.italker.common.app.Fragment;
import net.rong.italker.common.widget.GalleryView;
import net.rong.italker.push.App;
import net.rong.italker.push.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 权限申请弹出框
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks{
    //权限回调标识
    private static final int RC = 0x0100;

    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //先使用默认的,在8.0系统中不会有黑边
        return new BottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_permissions, container, false);

        root.findViewById(R.id.btn_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPerm();
                    }
                });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //界面显示的时进行刷新
        refreshState(getView());
    }

    /**
     * 刷新布局中的图片的状态
     * @param root
     */
    private void refreshState(View root){
        if(root == null)
            return;
        Context context = getContext();
        root.findViewById(R.id.im_state_permission_network).setVisibility(haveNetWork(context)? View.VISIBLE: View.GONE);
        root.findViewById(R.id.im_state_permission_read).setVisibility(haveReadPerm(context)? View.VISIBLE: View.GONE);
        root.findViewById(R.id.im_state_permission_write).setVisibility(haveWritePerm(context)? View.VISIBLE: View.GONE);
        root.findViewById(R.id.im_state_permission_record_audio).setVisibility(haveRecordPerm(context)? View.VISIBLE: View.GONE);
    }

    private static boolean haveNetWork(Context context) {
        //准备需要检查的网络泉下
        String[] perms = new String[]{Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveReadPerm(Context context) {
        //准备需要检查的网络泉下
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveWritePerm(Context context) {
        //准备需要检查的网络泉下
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveRecordPerm(Context context) {
        //准备需要检查的网络泉下
        String[] perms = new String[]{Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    //私有的show
    private static void show(FragmentManager manager){
        //调用已经准备好的显示方法
        new PermissionsFragment()
                .show(manager,PermissionsFragment.class.getName());
    }

    /**
     * 检查是否具有所有的权限
     * @param context
     * @param manager
     * @return 是否有权限
     */
    public static boolean haveAll(Context context, FragmentManager manager){
        boolean haveAll = haveNetWork(context)
                && haveReadPerm(context)
                && haveWritePerm(context)
                && haveRecordPerm(context);
        //如果没有则显示当前申请权限的界面
        if(!haveAll){
            show(manager);
        }
        return haveAll;
    }


    /**
     * 申请权限的方法
     */
    @AfterPermissionGranted(RC)
    private void requestPerm(){
        String[] perms = new String[]{Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        if(EasyPermissions.hasPermissions(getContext(), perms)){
            Application.showToast(R.string.label_permission_ok);
            //Fragment中调用getView得到跟布局，前提是在onCreateView方法之后
            refreshState(getView());
        }
        else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions), RC, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //如果权限存在没有申请成功的权限存在，则弹出对话框，用户点击后去到设置界面自己打开权限
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            new AppSettingsDialog
                    .Builder(this)
                    .build()
                    .show();
        }
    }

    /**
     * 权限申请的时候回调的方法，在这个方法中把对应的权限申请状态交给EasyPermissions框架
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //传递对应的 参数，并且告知接受权限的处理者是我自己
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions ,grantResults ,this);
    }
}
