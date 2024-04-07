package invertedIndex;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class invertedIndex {

    //  Ρυθμίζει το ελάχιστο μήκος των λέξεων στο configuration file (ορίζετε εσείς το μέγεθος)
    static int minLength;

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setInt("nGrams",minLength); // Προεπιλεγμένο μέγεθος των n-grams
        Job job = Job.getInstance(conf, "Inverted Index");
        job.setJarByClass(invertedIndex.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        minLength = Integer.parseInt(args[2]);
        conf.setInt("minLength", minLength);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class Map extends Mapper<LongWritable, Text, Text, Text> {
        private final static Text word = new Text();
        private Text fileName = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Παίρνουμε το όνομα του αρχείου
            String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
            // Μετατροπή του κειμένου σε πεζά
            String line = value.toString().toLowerCase();
            // Αφαίρεση των σημείων στίξης
            line = line.replaceAll("[^a-z A-Z\\s]", "");
            // Χωρίζει το κείμενο σε λέξεις
            StringTokenizer tokenizer = new StringTokenizer(line);

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                // Αν το token είναι μεγαλύτερο από minLength χαρακτήρες
                if (token.length() >= minLength) {
                    word.set(token);
                    // Αποθηκεύουμε το όνομα του αρχείου
                    this.fileName.set(fileName);
                    // Αποστέλλουμε το όνομα του αρχείου και το token στον reducer
                    context.write(word, this.fileName);
                }
            }
        }

    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // Δημιουργεί ένα HashSet για τις μοναδικές τιμές
            HashSet<String> uniqueValues = new HashSet<>();

            // Προσθέτει τις τιμές στο HashSet
            for (Text val : values)
                uniqueValues.add(val.toString());

            // Αποθηκεύει τις τιμές σε ένα StringBuilder
            StringBuilder result = new StringBuilder();

            // Προσθέτει τις τιμές στο StringBuilder
            for (String val : uniqueValues)
                result.append(val).append(", ");

            context.write(key, new Text(result.toString()));
        }
    }
}
