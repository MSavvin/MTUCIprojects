import java.io.*;
import java.net.*;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 *  Поток для обработки адресов
 */
public class CrawlerTask implements Runnable {
	//Ссылка на пул адресов
	UrlPool pool;
    // HTML href тэг, по которому будем ловить ссылки
	static final String HREF_TAG = "<a href=\"http";
	//id потока
    int idThread;

	public CrawlerTask(int idThread, UrlPool pool) {

		this.pool = pool;
		this.idThread = idThread;
	}

    /**
     * Это основной метод класса.
     * Он обеспечивает:
     * 1) Запрос адреса для обработки
     * 2) Создание сокета и отправка запроса  для каждой пары значений
     * 3) Обработка полученной web-страницы.
     * 4) При успехе - добавление найденных адресов в пул.
     */
	@Override
	public void run() {
        //Создаём "фабрику" для для формирования SSL-сокета.
        //Использования обычного сокета выдаёт ошибку "http moved permanently 301"
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        while (true) {
            // Получаем параметры сайта, с которым будем работать
            URLDepthPair currPair = pool.getNextPair();
			int currDepth = currPair.getDepth();

            if (currDepth>pool.getMaxDepth()){
                continue;
            }
            // Инициализируем сокет и отравляем запрос на сайт.
                Socket socket;
                try {
                    socket = socketFactory.createSocket(currPair.getHost(), 443);
                    socket.setSoTimeout(5000);
                    System.out.println("Thread:" + idThread+ " Connecting to " + currPair.getURL());
                    System.out.flush();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    // Отправляем HTTP запрос
                    out.println("GET " + currPair.getDocPath() + " HTTP/1.1");
                    out.println("Host: " + currPair.getHost());
                    out.println("Connection: close");
                    out.println();
                }
                //В случае ошибки - попытаем счастья на следующей паре.
                catch (UnknownHostException e) {
                    System.err.println("Thread:" + idThread+ " Host "+ currPair.getHost() + " couldn't be determined");
                    continue;
                }
                catch (SocketException e) {
                    System.err.println("Thread:" + idThread+ " Error with socket connection: " + currPair.getURL() +
                            " - " + e.getMessage());
                    continue;
                }
                catch (IOException e) {
                    System.err.println("Thread:" + idThread+ " Couldn't retrieve page at " + currPair.getURL() +
                            " - " + e.getMessage());
                    continue;
                }

            String line;
            int lineLength;
            int shiftIdx;
            boolean firstTry = true;
            try{
                //Будем читать буфер сокета пока не дойдём до конца
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    while ((line = in.readLine()) != null) {
                    // В первой строке содержится результат запроса к сайту
                    if(firstTry){
                        firstTry = false;
                        // если всё хорошо - сообщаем пользователю и продолжаем
                        if(line.equals("HTTP/1.1 200 OK")){
                            System.out.println("Thread:" + idThread+ " Connected successfully!");
                            System.out.flush();
                            continue;
                        }else{
                            // иначе грустим и переходим к следующей паре.
                            System.out.println("Thread:" + idThread+ " Server return error: " + line);
                            System.out.flush();
                            break;
                        }
                    }
                    // Проверим: а содержит ли строка URL?
				    boolean foundFullLink = false;
			    	int idx = line.indexOf(HREF_TAG);
			    	if (idx > 0) {
                        // Содержит!
                        // Сдвигаемся к началу найденного URL и пытаемся посимвольно считать его
			    		StringBuilder sb = new StringBuilder();
			    		shiftIdx = idx + 9;
			    		char c = line.charAt(shiftIdx);
			    		lineLength = line.length();
			    		while (c != '"' && shiftIdx < lineLength - 1) {
			    			sb.append(c);
			    			shiftIdx++;
			    			c = line.charAt(shiftIdx);
			    			if (c == '"') {
			    				foundFullLink = true;
			    			}
			    		}
                        // Пытаемся создать новую пару.
                        // Если считаем, что считали URL полностью
                        if(foundFullLink) {
                            //Делаем проверку на корректность URL
                            URL currentURL = new URL(sb.toString());
                            //Создаём пару и засовываем её в пул.
                            URLDepthPair newPair = new URLDepthPair(currentURL, currDepth + 1);
                            pool.addPair(newPair);
                        }
			    	}
			    }

                //Закрываем за собой сокет
                in.close();
                socket.close();
			}
			catch (IOException e) {
			}
		}
	}
}
