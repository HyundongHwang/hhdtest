package com.hhd2002.hhdtest.GlideTest.models;

/**
 * Created by hhd on 2017-06-30.
 */

public class GlideTestImage {
    
    public enum SourceTypes {
        Local,
        Web,
    };

    public int width;
    public int height;
    public String thumbnailUri;
    public String realUri;
    public SourceTypes sourceType = SourceTypes.Local;
}

