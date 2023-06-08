package prog.messenger;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kgsu.network.TCPConnection;
import com.kgsu.network.TCPConnectionListener;

import java.io.IOException;

/**
 * \brief Класс отвечающий за отрисовку чата и отправку сообщений на сервер
 * \authors LisDem
 * \date 22.05.2022
 */
public class Chat extends AppCompatActivity implements View.OnClickListener, TCPConnectionListener {

    private static final String TAG = "Logs";

    // Объявление элементов
    public static final String IP_ADDR = "192.168.0.4";
    public static final int PORT = 5999;
    private TCPConnection connection;
    EditText etIn;
    Button btnIn;
    TextView tvOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Метод вызывается при создании или перезапуске активности.
         * Отвечает за инициалиацию переменных, а так же указывает какой xml файл необходимо отобразить.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // Нахождение элементов
        etIn = (EditText) findViewById(R.id.etIn);
        btnIn = (Button) findViewById(R.id.btnIn);
        tvOut = (TextView) findViewById(R.id.tvOut);

        // Присвоение обработчика кнопке
        btnIn.setOnClickListener(this);

        // Новый поток для создания подключения
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = new TCPConnection(Chat.this, IP_ADDR, PORT);
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(Chat.this, "Невозможно подключиться к серверу", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        /**
         * Метод отвечает за обработку нажатий на кнопку отправки сообщений
         */
        String msg_0, msg_end;

        // Проверяем поля на пустоту
        if (TextUtils.isEmpty(etIn.getText().toString())) {
            Log.d(TAG, "Не введено сообщение;");
            Toast.makeText(this, "Сообщение не должно быть пустым", Toast.LENGTH_SHORT).show();
            return;
        }

        // Экспорт ника из главной активности
        Bundle arguments = getIntent().getExtras();
        String nick = arguments.getString("nick");

        // Чтение текущего сообщения
        msg_0 = etIn.getText().toString();
        Log.d(TAG, msg_0 + ";");

        // Проверка сообщения на наличие лишних пробелов
        if (msg_0.trim().length() > 0) {
            msg_end = nick + ": " + msg_0.trim();
            Log.d(TAG, msg_end + ";");

            // Отдельный поток для отправки сообщений
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connection.sendString(msg_end);
                }
            }).start();
        } else {
            Log.d(TAG, "Сообщение состоит из пробелов;");
            Toast.makeText(this, "Сообщение не должно состоять только из пробелов", Toast.LENGTH_SHORT).show();
        }

        etIn.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         *Отвечает за создание и отрисовку выпадающего меню в верхней панели.
         */
        getMenuInflater().inflate(R.menu.mes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Метод отвечает за обработку нажатий на кнопки в выпадающем меню.
         */
        // Проверка, какая именно кнопка была нажата
        switch (item.getItemId()) {
            case R.id.reset:
                // очищаем поле
                Log.d(TAG, "Нажато меню: reset;");
                tvOut.setText("");
                break;
            case R.id.quit:
                // выход из приложения
                Log.d(TAG, "Нажато меню: quit;");
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onConnection(TCPConnection tcpConnection) {
        /**
         * Пустой метод, срабатывает при подключении к серверу.
         */
    }

    @Override
    public void onStringInput(TCPConnection tcpConnection, String value) {
        /**
         * Метод срабатывает при получении строки с сервера.
         */
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        /**
        * Срабатывает при отключении от сервера.
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Chat.this, "Невозможно подключиться к серверу", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        /**
        * Срабатывает при исключении. Выводит код ошики.
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Chat.this, "Код ошибки " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    public void printMsg(String value) {
        /**
        * Метод отвечает за отображение сообщений на экране пользователя.
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!value.equals("null"))
                    tvOut.setText(tvOut.getText() + "\r\n" + value);
            }
        });
    }
}