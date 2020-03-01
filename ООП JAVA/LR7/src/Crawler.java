import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;

public class Crawler {
    // HTML href тэг, по которому будем ловить ссылки
    static final String HREF_TAG = "<a href=\"http";

    // Список всех сайтов, которые мы успешно просмотрели
    static LinkedList<URLDepthPair> allSitesSeen = new LinkedList<URLDepthPair>();
    // Список всех сайтов, которые мы хотим просмотреть
    static LinkedList<URLDepthPair> toVisit = new LinkedList<URLDepthPair>();

    /**
     * Это основной метод класса.
     * Он обеспечивает:
     * 1) Формирования корневого элемента на основе сайта, введённого пользователем;
     * 2) Создание сокета и отправка запроса  для каждой пары значений
     * 3) Обработка полученной web-страницы.
     * 4) Вывод результирующего списка на экран.
     */
    public static void crawl(String startURL, int maxDepth)
            throws MalformedURLException {

        //Формируем корневой элемент(глубина 0) на основе введённых пользователем данных
        URL rootURL = new URL(startURL);
        URLDepthPair urlPair = new URLDepthPair(rootURL, 0);
        toVisit.add(urlPair);

        int depth;
        //Список хэшированных адресов, которые мы просмотрели.
        //это позволяет обеспечить уникальность адреса в результирующем списке.
        HashSet<URL> seenURLs = new HashSet<URL>();
        seenURLs.add(rootURL);

        //Создаём "фабрику" для для формирования SSL-сокета.
        //Использования обычного сокета выдаёт ошибку "http moved permanently 301"
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        // Будем выполнять  поиск пока есть хоть один не просмотренный сайт.
        while (!toVisit.isEmpty()) {
            // Получаем параметры сайта, с которым будем работать
            URLDepthPair currPair = toVisit.removeFirst();
            depth = currPair.getDepth();
            if (depth>maxDepth){
                continue;
            }

            // Инициализируем сокет и отравляем запрос на сайт.
            Socket socket;
            try {
                socket = socketFactory.createSocket(currPair.getHost(), 443);
                socket.setSoTimeout(5000);
                System.out.println("Connecting to " + currPair.getURL());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // Отправляем HTTP запрос
                out.println("GET " + currPair.getDocPath() + " HTTP/1.1");
                out.println("Host: " + currPair.getHost());
                out.println("Connection: close");
                out.println();
            }
            //В случае ошибки - попытаем счастья на следующей паре.
            catch (UnknownHostException e) {
                System.err.println("Host "+ currPair.getHost() + " couldn't be determined");
                continue;
            }
            catch (SocketException e) {
                System.err.println("Error with socket connection: " + currPair.getURL() +
                        " - " + e.getMessage());
                continue;
            }
            catch (IOException e) {
                System.err.println("Couldn't retrieve page at " + currPair.getURL() +
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
                while ((line = in.readLine())!=null){
                    // В первой строке содержится результат запроса к сайту
                    if(firstTry){
                        firstTry = false;
                        // если всё хорошо - сообщаем пользователю и продолжаем
                        if(line.equals("HTTP/1.1 200 OK")){
                            System.out.println("Connected successfully!");
                            continue;
                        }else{
                            // иначе грустим и переходим к следующей паре.
                            System.out.println("Server return error: " + line);
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
                        while ( c != '"' && shiftIdx < lineLength - 1) {
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
                            //Проверяем: допустима ли глубина и является ли сайт уникальным
                            if (!seenURLs.contains(currentURL)){
                                //Нам подходит!
                                URLDepthPair newPair = new URLDepthPair(currentURL, depth + 1);
                                toVisit.add(newPair);
                                seenURLs.add(currentURL);
                            }
                        }
                    }
                }

                //Закрываем за собой сокет...
                in.close();
                socket.close();
                //... и добавляем в результирующий список
                allSitesSeen.add(currPair);
            }
            catch (IOException e) {
            }
        }
        // Закончили осмотр - выводим результаты в консоль
        System.out.println( "\n" + "Result list of sites: ");

        // формат вывода: URL: https://habr.com/ru/about/, Depth: 0
        for (URLDepthPair pair : allSitesSeen) {
            System.out.println(pair.toString());
        }
    }

    /**
     *  Точка входа программу.
     *  Для корректной работы необходима передать в качестве аргументов URL сайта
     *  и максимальную глубину погружения по ссылкам.
     *  Пример: https://habr.com/ru/about/ 1
     */
    public static void main(String[] args){
        // если пользователь не ввёл 2 аргумента - ругаемся и отказываемся работать
        if (args.length != 2) {
            System.out.println("usage: java Crawler <URL> <maximum_depth>");
            System.exit(1);
        }
        try {
            // Начинаем скинировать интернеты начиная с заданного адреса.
            crawl(args[0], Integer.parseInt(args[1]));
        }
        catch (MalformedURLException e) {
            //Если пользователь ввёл кривой URL - приходим сюда, чтобы его обрадовать.
            System.err.println("Error: The URL " + args[0] + " is not valid");
            System.exit(1);
        }
        System.exit(0);
    }
}
