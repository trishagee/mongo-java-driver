package org.bson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

class TestOutputStream extends OutputStream {
    private List<Integer> results = new ArrayList<Integer>();

    @Override
    public void write(final int b) throws IOException {
        results.add(b);
    }

    public void reset() {
        results = new ArrayList<Integer>();
    }

    public List<Integer> getResults() {
        return results;
    }
}
