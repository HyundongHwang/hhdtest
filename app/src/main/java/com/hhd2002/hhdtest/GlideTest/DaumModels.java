package com.hhd2002.hhdtest.GlideTest;

import java.util.ArrayList;

public class DaumModels {
    public Channel channel;

    public static class Channel {
        public int result;
        public int pageCount;
        public String title;
        public int totalCount;
        public String description;
        public ArrayList<Item> item;
    }

    public static class Item {
        public String pubDate;
        public String title;
        public String thumbnail;
        public String cp;
        public int height;
        public String link;
        public int width;
        public String image;
    }
}
