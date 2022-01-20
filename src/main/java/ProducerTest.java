import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.SimpleTimeZone;

public class ProducerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();
        String topic = "search";

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        Producer producer = new KafkaProducer<String,String>(properties);

        while (true){
            System.out.println("Ara: ");
            String product = scanner.nextLine();
            SearchProductModel searchProductModel = new SearchProductModel();
            searchProductModel.setProduct(product);

            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            searchProductModel.setTime(time);
            String json = gson.toJson(searchProductModel);
            System.out.println(json);
            ProducerRecord<String,String> rec = new ProducerRecord<String,String>(topic,json);
            producer.send(rec);
        }


    }
}
