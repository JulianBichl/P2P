package p2pcc;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class TermFrequencyTupleWritable implements Writable {

    private String term;
    private long frequency;

    public TermFrequencyTupleWritable() {
        set(term, frequency);
    }

    public TermFrequencyTupleWritable(String term, long frequency) {
        set(term, frequency);
    }

    public void set(String word, long frequency) {
        this.term = word;
        this.frequency = frequency;
    }

    public String getTerm() {
        return term;
    }

    public long getFrequency() {
        return frequency;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(term);
        out.writeLong(frequency);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        term = in.readUTF();
        frequency = in.readLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermFrequencyTupleWritable termFrequencyTupleWritable = (TermFrequencyTupleWritable) o;

        if (frequency != termFrequencyTupleWritable.frequency) return false;
        return term.equals(termFrequencyTupleWritable.term);
    }

    @Override
    public int hashCode() {
        int result = term.hashCode();
        result = 31 * result + (int) (frequency ^ (frequency >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "(" + term + ", " + frequency + ")";
    }
}
