package com.awbt.sdutnews2.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.awbt.sdutnews2.R;
import com.awbt.sdutnews2.bean.NewsBean;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements View.OnClickListener{
    private List<NewsBean> msgList;
    RecyclerView recyclerView;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView textView;
        TextView textView2;
        View msgView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout=(LinearLayout)itemView.findViewById(R.id.recy_news);
            textView=(TextView)itemView.findViewById(R.id.recy_news_title);
            textView2=(TextView)itemView.findViewById(R.id.recy_news_content);
            msgView = itemView;
        }
    }

    public void setMsgList(List<NewsBean> msgList) {
        this.msgList = msgList;
    }

    public NewsAdapter(List<NewsBean> msgList){
        this.msgList=msgList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent,false);
        view.setOnClickListener(this);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        NewsBean msg=msgList.get(position);
        holder.textView.setText(msg.getTitle());
        holder.textView2.setText(msg.getContent());
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
    /*
    定义单击事件回调接口
     */
    public interface OnItemClickListener{
        //参数（父组件，当前单击的View,单击的View的位置，数据）
        void onItemClick(RecyclerView parent,View view, int position, NewsBean data);
    }
    /*
    在RecyclerView的Adapter中声明该接口，并提供setter方法
     */
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    /*
    实现View.OnClickListener接口，并重写onClick(View view)方法，然后设置给接口的事件监听
     */
    @Override
    public void onClick(View v) {
        //根据RecyclerView获得当前View的位置
        int position = recyclerView.getChildAdapterPosition(v);
        //程序执行到此，会去执行具体实现的onItemClick()方法
        if (onItemClickListener!=null){
            onItemClickListener.onItemClick(recyclerView,v,position,msgList.get(position));
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView= recyclerView;
    }

    public void add(List<NewsBean> newlist){
        int bl = this.msgList.size();
        int al = newlist.size();
        msgList.addAll(newlist);
        notifyItemInserted(bl);
        notifyItemRangeChanged(bl,msgList.size()-bl);
        Log.d("newsadapter", "add: "+msgList.size());
    }
    public void clear(){
        int len=msgList.size();
        msgList.clear();
        notifyItemRangeRemoved(0,len);
    }
}
