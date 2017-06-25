/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hhd2002.hhdtest.GoogleOAuthTest;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class GoogleOAuthTestActivity extends ListActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        String[] items = new String[]{
                "Foreground",
                "Background",
                "Background with Sync",
        };

        this.setListAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Highly coupled with the order of contents in main_activity_items
        Intent intent = new Intent(this, GoogleOAuthTestHelloActivity.class);

        if (position == 0) {
            intent.putExtra(GoogleOAuthTestHelloActivity.TYPE_KEY, GoogleOAuthTestHelloActivity.Type.FOREGROUND.name());
        } else if (position == 1) {
            intent.putExtra(GoogleOAuthTestHelloActivity.TYPE_KEY, GoogleOAuthTestHelloActivity.Type.BACKGROUND.name());
        } else if (position == 2) {
            intent.putExtra(GoogleOAuthTestHelloActivity.TYPE_KEY, GoogleOAuthTestHelloActivity.Type.BACKGROUND_WITH_SYNC.name());
        }
        startActivity(intent);
    }
}
