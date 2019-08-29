package net.rong.italker.push.frags.panel;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import net.rong.italker.common.widget.recycler.RecyclerAdapter;
import net.rong.italker.face.Face;
import net.rong.italker.push.R;

import butterknife.BindView;

public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean> {
    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if (bean != null &&
                //drawable资源id 或者 face zip包资源路径
                ((bean.preview instanceof Integer) || (bean.preview instanceof String))) {
            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888) // 设置解码的格式8888，保证清晰度
                    .into(mFace);
        }
    }
}
