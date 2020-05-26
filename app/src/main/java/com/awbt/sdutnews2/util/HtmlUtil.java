package com.awbt.sdutnews2.util;

import android.util.Log;

import com.awbt.sdutnews2.bean.NewsBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlUtil {
    private static String baseurl="https://lgwindow.sdut.edu.cn";
    private List list;
    public HtmlUtil(){
        list = new ArrayList();
        list.add(baseurl+"/1058/list");
        list.add(baseurl+"/zhxw/list");
        list.add(baseurl+"/1073/list");
        list.add(baseurl+"/jxky/list");
    }
    public List<NewsBean> getlist(int op,int page)throws IOException{
        String useurl = (String)list.get(op);
        useurl+=page;
        useurl+=".htm";
        Log.d("getlist", "getlist url:"+useurl);
        Document document = Jsoup.connect(useurl).get();

        System.out.println(document.toString());
        List<NewsBean> ret = new ArrayList<NewsBean>();
        Element postList = document.select("tbody").get(2);
        Elements postItems = postList.select("tr");
        for (Element postItem : postItems) {
            Log.d("getlist", "postitem: "+postItem.toString());
            Elements titleEle = postItem.select(".list_tit [target='_blank']");
            Elements contentEle = postItem.select(".list_content [target='_blank']");
            Elements timeEle = postItem.select(".list_time [class='lt_b']");
            NewsBean data = new NewsBean();
            data.setContent(contentEle.text());
            data.setTitle(titleEle.text());
            data.setTime(timeEle.text());
            data.setUrl(baseurl+titleEle.attr("href"));
            Log.d("getlist", "data: "+data.toString());
            ret.add(data);
        }
        return ret;
    }
}
