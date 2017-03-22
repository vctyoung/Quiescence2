package com.example.victor.quiescence;

/**
 * Created by victor on 2017/3/4.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class RecyclerAdapter extends RecyclerView.Adapter{

    private ArrayList<Room> rooms;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    //private  String[]  names;

    public RecyclerAdapter(Context context, ArrayList<Room> rooms){
        this.context=context;
        this.rooms=rooms;
        //  names=null;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        //  private ImageView picture;
        private TextView title,text;

        public ViewHolder(View itemView) {
            super(itemView);

            //    picture = (ImageView) itemView.findViewById(R.id.picture);
            title = (TextView) itemView.findViewById(R.id.title);
            text = (TextView) itemView.findViewById(R.id.text);

            //  itemView.setOnClickListener(itemView.OnClickListener);
        }

     /*   public ImageView getPicture(){
            return picture;
        }*/

        public TextView getTitle(){
            return title;
        }

        public TextView getText(){
            return text;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


       String temp=null;

        if ( rooms.get(position).getLevel()==0)
            temp=" VERY QUIET";
        else
           temp="NOISY";
        final ViewHolder vh = (ViewHolder) holder;
        //   vh.getPicture().setImageResource(models.get(position).getPicture());
        vh.getTitle().setText(rooms.get(position).getName());
        vh.itemView.setTag(rooms.get(position).getName());
        vh.getText().setText("Noise"+" "+"level:"+temp);
        vh.itemView.setClickable(true);
        //vh.itemView.setOnClickListener(mOnItemClickListener);

        //如果设置了回调，就设置点击事件
        if (mOnItemClickListener != null){
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(vh.itemView,position);

                    //    Toast.makeText(context,"fdfdf"+v.getTag(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


    public static interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


}
