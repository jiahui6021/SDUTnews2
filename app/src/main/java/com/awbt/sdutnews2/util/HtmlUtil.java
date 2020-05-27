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

        List<NewsBean> ret = new ArrayList<NewsBean>();
        Element postList = document.select("tbody").get(2);
        Elements postItems = postList.select("tr");
        for (Element postItem : postItems) {
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
    public String getnews(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Element postList1 = document.getElementsByClass("con_left clearfix").get(0);
        Log.d("getnews", "elements:"+postList1.toString());
        Element postList = document.getElementsByClass("wp_articlecontent").get(0);
        Elements imgItems = postList.select("[data-layer='photo']");
        String imgUrl;
        for (Element imgItem: imgItems) {
            imgUrl = baseurl + imgItem.attr("src");
            imgItem.attr("src", imgUrl);
            imgItem.attr("original-src", imgUrl);
            imgItem.attr("max-width", "100%")
                    .attr("height", "auto");
            imgItem.attr("style", "max-width:100%;height:auto");
        }
        return postList1.toString();
    }
}
