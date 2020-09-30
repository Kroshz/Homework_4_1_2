package ru.netology.lists;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_COUNT = "key_count";
    private SharedPreferences savedText;
    private static String NOTE_TEXT = "saved_text";
    // Список с мапами, изначально пустая
    private static List<Map<String, String>> simpleAdapterContent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // всегда следующие 2 строчки такие, только основной Активити меняется
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        // добавляем тулбар 2 строчки
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // находим наш основной лист
        ListView list = findViewById(R.id.list);
        final SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipe_refresh);
        final BaseAdapter listContentAdapter = createAdapter(simpleAdapterContent);

        String str = getString(R.string.large_text);

        savedText = getSharedPreferences("SavedText", MODE_PRIVATE);
        SharedPreferences.Editor editor = savedText.edit();
        savedText.edit()
                .putString(NOTE_TEXT, str)
                .apply();
        // Обращаемся к подготовке контента, но он ничего не возвращает, работает напрямую с адаптером
        prepareContent();

        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                simpleAdapterContent.remove(position);
                listContentAdapter.notifyDataSetChanged();
                Toast.makeText(ListViewActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareContent();
                listContentAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                Toast.makeText(ListViewActivity.this, "Обновлено", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        // создаем массив со значениями ключ карта. Откуда берем значения
        String[] from = {KEY_TITLE, KEY_COUNT};
        // куда эти значения будут вставляться ЧИСЛО!
        int[] to = {R.id.text_Tv, R.id.symbol_cnt_Tv};
        // Сам Адаптер
        return new SimpleAdapter(this, values, R.layout.item, from, to);
        // или Вместо фром и То вставлять новые данные сразу в симпл Адаптер)
        // return new SimpleAdapter(this, values, R.layout.item, new String[]{KEY_TITLE, KEY_COUNT}, new int[]{R.id.text_Tv, R.id.symbol_cnt_Tv});
    }

    @NonNull
    private void prepareContent() {
        String[] titles = savedText.getString(NOTE_TEXT, "").split("\n\n");
        simpleAdapterContent.clear();
        for (String title : titles) {
            Map<String, String> map = new HashMap<>();
            map.put(KEY_TITLE, title);
            map.put(KEY_COUNT, String.valueOf(title.length()));
            simpleAdapterContent.add(map);
        }
    }
}
