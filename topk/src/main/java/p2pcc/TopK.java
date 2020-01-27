package p2pcc;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TopK extends Configured implements Tool {

    private static final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    private static final InputStream is = classloader.getResourceAsStream("english-left3words-distsim.tagger");

    private static final MaxentTagger tagger = new MaxentTagger(is);
    private static final String POS_REGEX = "NN|NNS|NNP|NNPS";

    private static final String CORPUS_PATH = "corpus" + File.separator;
    private static final String RESULT_PATH = "output" + File.separator;

    private static final int K = 10;

    public static class TermFrequencyMapper extends Mapper<Object, Text, NullWritable, TermFrequencyTupleWritable> {
        
    	@Override
        public void map(final Object key, final Text value, final Context context)
                throws IOException, InterruptedException {
    		  String line = value.toString();
    	      StringTokenizer tokenizer = new StringTokenizer(line);
    	      while (tokenizer.hasMoreTokens()) {
    	         String s = tokenizer.nextToken();
    	         context.write(null, new TermFrequencyTupleWritable(s, 1));
    		}  
        }
    }

    public static class TopKReducer extends Reducer<NullWritable,
            TermFrequencyTupleWritable,
            NullWritable,
            TermFrequencyTupleWritable> {

        @Override
        public void reduce(final NullWritable key,
                           final Iterable<TermFrequencyTupleWritable> values,
                           final Context context)
                throws IOException, InterruptedException {
        	int sum = 0;
        	for(TermFrequencyTupleWritable value: values){
        	    sum += value.getFrequency();
        	};
        	context.write(key, new TermFrequencyTupleWritable(key.toString(), sum));
        	}
    }

  public int run(final String[] args) throws Exception {

    final Configuration conf = this.getConf();
    final Job job = Job.getInstance(conf, "TopK");
    job.setJarByClass(TopK.class);

    job.setMapperClass(TermFrequencyMapper.class);
    job.setReducerClass(TopKReducer.class);

    job.setMapOutputKeyClass(NullWritable.class);
    job.setMapOutputValueClass(TermFrequencyTupleWritable.class);

    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(TermFrequencyTupleWritable.class);

    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    FileInputFormat.addInputPath(job, new Path(CORPUS_PATH));
    FileOutputFormat.setOutputPath(job, new Path(RESULT_PATH));

    return job.waitForCompletion(true) ? 0 : 1;
  }

  public static void main(final String[] args) throws Exception {
    final int returnCode = ToolRunner.run(new Configuration(), new TopK(), args);
    System.exit(returnCode);
  }
}
