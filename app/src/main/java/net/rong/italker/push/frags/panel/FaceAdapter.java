package net.rong.italker.push.frags.panel;

import android.view.View;

import net.rong.italker.common.widget.recycler.RecyclerAdapter;
import net.rong.italker.face.Face;
import net.rong.italker.push.R;

import java.util.List;

public class FaceAdapter extends RecyclerAdapter<Face.Bean> {

    public FaceAdapter(List<Face.Bean> dataList, AdapterListener<Face.Bean> listener) {
        super(dataList, listener);
    }

    @Override
    protected int getItemViewType(int position, Face.Bean data) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int ViewType) {
        return new FaceHolder(root);
    }
}
