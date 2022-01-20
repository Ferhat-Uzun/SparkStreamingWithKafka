import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class StreamingConsumer {

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession sparkSession = SparkSession.builder().appName("Streaming-kafka").master("localhost").getOrCreate();

        StructType schema= new StructType()
                .add("product","string")
                .add("time", DataTypes.TimestampType);

        Dataset<Row> rawData = sparkSession.readStream().format("kafka")
                .option("kafka.bootsrap.servers", "localhost:9092")
                .option("subscribe", "search")
                .load();

        Dataset<SearchProductModel> data = rawData.selectExpr("CAST(value as STRING) message")
                .select(functions.from_json(functions.col("message"), schema))
                .as("json")
                .select("json.*")
                .as(Encoders.bean(SearchProductModel.class));

        Dataset<Row> count = data.groupBy(functions.window(data.col("time"), "1 minute"), data.col("product")).count();

        StreamingQuery start = count.writeStream().outputMode("complete").format("console").start();
        start.awaitTermination();
    }
}
