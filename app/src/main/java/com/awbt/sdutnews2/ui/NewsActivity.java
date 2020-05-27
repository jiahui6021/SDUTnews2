package com.awbt.sdutnews2.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.awbt.sdutnews2.R;
import com.awbt.sdutnews2.bean.NewsBean;
import com.awbt.sdutnews2.util.HtmlUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private Intent intent;
    private WebView test;
    HtmlUtil htmlUtil;
    int flag;
    Menu news_menu;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        List<NewsBean> newsBeans = LitePal.where("Url = ?",intent.getStringExtra("url")).find(NewsBean.class);
        if(newsBeans.size()==0)
            flag=0;
        else{
            flag=1;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        test = findViewById(R.id.text_news);
        htmlUtil = new HtmlUtil();
        final String[] imgItems = new String[1];

        new NewsTask().execute(intent.getStringExtra("url"));

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        toolbar.setTitle(intent.getStringExtra("title"));
        toolbar.setSubtitle("SDUT_news");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        news_menu = menu;
        getMenuInflater().inflate(R.menu.news_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.news_favorite);
        if(flag==1){
            menuItem.setIcon(R.drawable.btn_star_on);
        }
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
        if (id == R.id.news_favorite) {
            if(flag==0){
                NewsBean newsBean = new NewsBean();
                newsBean.setContent(intent.getStringExtra("content"));
                newsBean.setTime(intent.getStringExtra("time"));
                newsBean.setTitle(intent.getStringExtra("title"));
                newsBean.setUrl(intent.getStringExtra("url"));
                newsBean.save();
            }
            else{
                LitePal.deleteAll(NewsBean.class,"Url = ?",intent.getStringExtra("url"));
            }
            flag^=1;
            refreshmenu();
        }
        if(id == R.id.news_share){
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "我在山东理工大学新闻网发现了一篇新闻：《"+intent.getStringExtra("title")+"》\n\n"
                            +"请点击下方链接查看详细信息："+intent.getStringExtra("url")+" \n\n--------\n" +
                            "本消息发送自 SDUTnews APP，下载链接："+
                            "https://github.com/jiahui6021/SDUTnews2/releases");
            shareIntent = Intent.createChooser(shareIntent, "分享至：");
            startActivity(shareIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshmenu(){
        MenuItem menuItem = news_menu.findItem(R.id.news_favorite);
        if(flag==0){
            menuItem.setIcon(R.drawable.btn_star_off);
        }
        else{
            menuItem.setIcon(R.drawable.btn_star_on);
        }
    }
    public class NewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.d("newsTask", "strings[0]:"+strings[0]);
                return htmlUtil.getnews(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("newsTask", "error");
            return null;
        }

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected void onPostExecute(String result){
            Log.d("newsTask", "result:"+result);
            test.loadData(result,"text/html; charset=UTF-8;", null);
        }
    }
}
