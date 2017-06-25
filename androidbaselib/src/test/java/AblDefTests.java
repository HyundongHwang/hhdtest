import com.hhd2002.androidbaselib.log.AzureTableLogEntity;
import com.hhd2002.androidbaselib.log.HhdLog;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hhd on 2017-06-20.
 */

public class AblDefTests {

    public static final String AZURE_STORAGE_CONNECTION_STRING = "DefaultEndpointsProtocol=https;AccountName=hhdandroidtest;AccountKey=zJpcXJNUuvir9ucBO2WuxfDf/bFUfpnATKgNfRI28ByBJrgMzAuiQcY/2ma6udxuvCvnPFWPLf0exIK3n1XbsQ==;EndpointSuffix=core.windows.net";

    @Test
    public void def() throws Exception {

        int a = 1;
        int b = 2;
        int c = a + b;

        Assert.assertTrue(c == 3);
    }
}
