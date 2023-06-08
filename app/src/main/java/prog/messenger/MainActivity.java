package prog.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Создание логов
    private static final String TAG = "Logs";

    // Объявление элементов
    EditText nickname;
    Button entr;

    // Создание активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name);

        // Нахождение элементов
        nickname = (EditText) findViewById(R.id.nickname);
        entr = (Button) findViewById(R.id.entr);
        entr.setOnClickListener(this);
    }

    // Обработка нажатия на кнопку входа
    @Override
    public void onClick(View view) {
        String nick;

        // Проверяем поля на пустоту
        if (TextUtils.isEmpty(nickname.getText().toString())) {
            Log.d(TAG, "Не указан никнейм;");
            Toast.makeText(this, "Заполните поле", Toast.LENGTH_SHORT).show();
            return;
        }

        // Читаем EditText
        nick = nickname.getText().toString();

        // // Проверка ника на наличие лишних пробелов
        if (nick.trim().length() > 0) {
            Log.d(TAG, "Ник пользователя: " + nick.trim() + ";");

            // Передача ника во вторую активность
            Intent intent = new Intent(this, Chat.class);
            intent.putExtra("nick", nick.trim());
            startActivity(intent);
        }
        else {
            Log.d(TAG, "Поле заполнено пробелами;");
            Toast.makeText(this, "Ник не должен состоять из пробела(ов)", Toast.LENGTH_SHORT).show();
        }
        nickname.setText("");
    }

    // Создание меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mes_menu, menu);
        return true;
    }

    // Обработка нажаний в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Проверка, какая именно кнопка была нажата
        switch (item.getItemId()) {
            case R.id.reset:
                // очищаем поле
                Log.d(TAG, "Нажато меню: reset;");
                nickname.setText("");
                break;
            case R.id.quit:
                // выход из приложения
                Log.d(TAG, "Нажато меню: quit;");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}