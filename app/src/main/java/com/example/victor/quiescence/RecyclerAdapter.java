package com.example.victor.quiescence;

/**
 * Created by victor on 2017/3/4.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class RecyclerAdapter extends RecyclerView.Adapter{

    private ArrayList<Room> rooms;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;


    //private  String[]  names;

    public RecyclerAdapter(Context context, ArrayList<Room> rooms){
        this.context=context;
        this.rooms=rooms;
        //  names=null;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        //  private    ImageView picture;
        private TextView title,text,time;
        private ImageView noiseIndex;

        public ViewHolder(View itemView) {
            super(itemView);

            //    picture = (ImageView) itemView.findViewById(R.id.picture);
            title = (TextView) itemView.findViewById(R.id.title);
            text = (TextView) itemView.findViewById(R.id.text);
            time=(TextView) itemView.findViewById(R.id.date);
            noiseIndex=(ImageView) itemView.findViewById(R.id.imageView2);
           // quietIndex=(ImageView) itemView.findViewById(R.id.imageView2);

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

        public TextView getTime(){
            return time;
        }

        public ImageView getNoiseIndex(){return noiseIndex;};
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


       String temp=null;
        final ViewHolder vh = (ViewHolder) holder;

       switch (rooms.get(position).getLevel())
       {
           case 2:
               temp = "AirPort";
               vh.getNoiseIndex().setImageResource(android.R.drawable.presence_busy);

               break;
           case 1:
               temp = "Conference";
               vh.getNoiseIndex().setImageResource(android.R.drawable.presence_away);
               break;
           default:
               temp=" Silence  ";
               // vh.getTitle().setTextColor(Color.rgb(226,201,89));
               vh.getNoiseIndex().setImageResource(android.R.drawable.presence_online);


       }
        //   vh.getPicture().setImageResource(models.get(position).getPicture());
        vh.getTitle().setText(rooms.get(position).getName());
        vh.itemView.setTag(rooms.get(position).getName());
        vh.getText().setText(temp);
        vh.getTime().setText("      Updated:"+rooms.get(position).getTime());
        vh.itemView.setClickable(true);
        //vh.itemView.setOnClickListener(mOnItemClickListener);

        //如果设置了回调，就设置点击事件
        if (mOnItemClickListener != null){
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mOnItemClickListener.onItemClick(vh.itemView,position);

                   }
            });
        }
        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = vh.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(vh.itemView,position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


    public  interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }



}
