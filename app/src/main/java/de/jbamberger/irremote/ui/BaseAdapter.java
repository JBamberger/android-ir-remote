package de.jbamberger.irremote.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.jbamberger.irremote.R;
import de.jbamberger.irremote.service.IRSenderService;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ItemViewHolder> {
    private final String[] mItems = {};//FIXME: add items
    private final Context mContext;

    public BaseAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.textView.setText(mItems[position]);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IRSenderService.startActionSendIrcode(mContext, 0, "");//FIXME: holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

    }

}
