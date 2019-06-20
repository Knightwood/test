package com.example.testrecyler;

import android.view.View;

public class UniversalAdapter extends RecyclerView.Adapter<UniversalAdapter.UniversalViewHolder> {


    public UniversalViewHolder(View itemView) {
        super(itemView);
        recy_item_im=itemView.findViewById(R.id.recy_item_im);
        recy_item_tv=itemView.findViewById(R.id.recy_item_tv);
    }
}
