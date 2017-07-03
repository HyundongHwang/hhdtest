package com.hhd2002.hhdtest.ListViewTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.androidbaselib.adapters.HhdListViewAdapter;
import com.hhd2002.androidbaselib.adapters.HhdListViewHolder;
import com.hhd2002.hhdtest.R;

import java.util.ArrayList;

public class ListViewTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    private ListView lvObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_listview_test);
        lvObj = (ListView) findViewById(R.id.lv_obj);


        ArrayList<Object> items = new ArrayList<Object>();

        {
            RedItem item = new RedItem();
            item.str = "우리는 민족 중흥의 역사적 사명을 띠고 이 땅에 태어났다.";
            items.add(item);
        }

        {
            BlueItem item = new BlueItem();
            item.str = "조상의 빛난 얼을 오늘에 되살려, 안으로 자주독립의 자세를 확립하고, 밖으로 인류 공영에 이바지할 때다.";
            items.add(item);
        }

        {
            RedItem item = new RedItem();
            item.str = "이에, 우리의 나아갈 바를 밝혀 교육의 지표로 삼는다.";
            items.add(item);
        }

        {
            GreenItem item = new GreenItem();
            item.str = "성실한 마음과 튼튼한 몸으로, 학문과 기술을 배우고 익히며, 타고난 저마다의 소질을 계발하고,";
            items.add(item);
        }

        {
            GreenItem item = new GreenItem();
            item.str = "우리의 처지를 약진의 발판으로 삼아, 창조의 힘과 개척의 정신을 기른다.";
            items.add(item);
        }

        {
            RedItem item = new RedItem();
            item.str = "공익과 질서를 앞세우며 능률과 실질을 숭상하고, 경애와 신의에 뿌리박은 상부상조의 전통을 이어받아,";
            items.add(item);
        }

        {
            BlueItem item = new BlueItem();
            item.str = "명랑하고 따뜻한 협동 정신을 북돋운다.";
            items.add(item);
        }

        {
            RedItem item = new RedItem();
            item.str = "우리의 창의와 협력을 바탕으로 나라가 발전하며, 나라의 융성이 나의 발전의 근본임을 깨달아,";
            items.add(item);
        }

        {
            GreenItem item = new GreenItem();
            item.str = "자유와 권리에 따르는 책임과 의무를 다하며, 스스로 국가 건설에 참여하고 봉사하는 국민 정신을 드높인다.";
            items.add(item);
        }

        {
            BlueItem item = new BlueItem();
            item.str = "반공 민주 정신에 투철한 애국 애족이 우리의 삶의 길이며, 자유 세계의 이상을 실현하는 기반이다.";
            items.add(item);
        }

        {
            RedItem item = new RedItem();
            item.str = "길이 후손에 물려줄 영광된 통일 조국의 앞날을 내다보며, 신념과 긍지를 지닌 근면한 국민으로서,";
            items.add(item);
        }

        {
            GreenItem item = new GreenItem();
            item.str = "민족의 슬기를 모아 줄기찬 노력으로, 새 역사를 창조하자.";
            items.add(item);
        }

        ArrayList<Class> itemTypes = new ArrayList<Class>();
        itemTypes.add(RedItem.class);
        itemTypes.add(GreenItem.class);
        itemTypes.add(BlueItem.class);

        ArrayList<Class<? extends HhdListViewHolder>> vhTypes = new ArrayList<>();
        vhTypes.add(RedVh.class);
        vhTypes.add(GreenVh.class);
        vhTypes.add(BlueVh.class);

        HhdListViewAdapter adapter = new HhdListViewAdapter(this, itemTypes, vhTypes, new AdapterListener() {
            @Override
            public void onClickItem(Object item) {
                Log.i("hhddebug", "AdapterListener.onClickItem item.getClass().getName() : " + item.getClass().getName());
            }
        });

        adapter.items.addAll(items);
        lvObj.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public interface AdapterListener {
        public void onClickItem(Object item);
    }

    @Override
    public String getSampleDesc() {
        return "HhdMultiViewTypeAdapter 샘플";
    }
}

