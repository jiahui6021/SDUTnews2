package com.awbt.sdutnews2.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.awbt.sdutnews2.Adapter.NewsAdapter;
import com.awbt.sdutnews2.R;
import com.awbt.sdutnews2.bean.NewsBean;
import com.awbt.sdutnews2.util.HtmlUtil;
import com.google.android.material.navigation.NavigationView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private AppBarConfiguration mAppBarConfiguration;
    private HtmlUtil htmlUtil;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List list;
    private int nowop;
    private int page;
    private RefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nowop=0;
        page=1;
        //refresh
        refreshLayout = (RefreshLayout)findViewById(R.id.refresh);
        refreshLayout.setEnableAutoLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page=1;
                new HtmlTask().execute(nowop,page);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                new HtmlTask().execute(nowop,page);
            }
        });

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        htmlUtil = new HtmlUtil();

        //recyclerview
        recyclerView=(RecyclerView)findViewById(R.id.recy_news);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<NewsBean>();
        adapter=  new NewsAdapter(list);
        recyclerView.setAdapter(adapter);
        new HtmlTask().execute(0,page);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d("select item", "onOptionsItemSelected: "+item);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_newsa) {
            ;
        }

        return super.onOptionsItemSelected(item);
    }

    //侧滑点击后事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d("MainActivity", "onNavigationItemSelected: "+id);
        page = 1;
        switch (id){
            case R.id.nav_newsa:
                getdata(0);
                nowop=0;
                break;
            case R.id.nav_newsb:
                getdata(1);
                nowop=1;
                break;
            case R.id.nav_newsc:
                getdata(2);
                nowop=2;
                break;
            case R.id.nav_newsd:
                getdata(3);
                nowop=3;
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getdata(final int op){
        Log.d("getdata", "getdata: "+op);
        new HtmlTask().execute(op,page);
    }

    //返回关闭侧滑
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public class HtmlTask extends AsyncTask<Integer, Void, List<NewsBean>> {
        @Override
        protected void onPreExecute(){
        }

        @Override
        protected List<NewsBean> doInBackground(Integer... integers) {
            HtmlUtil htmlUtil=new HtmlUtil();
            try {
                return htmlUtil.getlist(integers[0],integers[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<NewsBean> result){
            if(result == null){
                Toast.makeText(MainActivity.this, "糟糕，加载失败了！", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(context, "加载成功"+result.size(), Toast.LENGTH_SHORT).show();
            if(page==1)
                adapter.clear();
            adapter.add(result);
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }
}
