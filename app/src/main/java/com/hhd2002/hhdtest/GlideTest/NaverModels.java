package com.hhd2002.hhdtest.GlideTest;

import java.util.ArrayList;

public class NaverModels {
    public static class Rss {
        public Channel channel;
    }

    public static class Channel {
        public String title;
        public String link;
        public String description;
        public String lastBuildDate;
        public int total;
        public int start;
        public int display;
        public ArrayList<Item> items;
    }

    public static class Item {
        public String title;
        public String link;
        public String thumbnail;
        public int sizeheight;
        public int sizewidth;
    }
}
