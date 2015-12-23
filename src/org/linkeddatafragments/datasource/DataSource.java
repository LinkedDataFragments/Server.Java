package org.linkeddatafragments.datasource;

import org.linkeddatafragments.fragments.IFragmentRequestParser;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.TPFRequestParser;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;

/**
 *
 * @author mielvandersande
 * @author Bart Hanssens
 */
public abstract class DataSource implements IDataSource {
    protected String title;
    protected String description; 
    
    public DataSource(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    };

    @Override
    public String getTitle() {
        return this.title;
    };

    /**
     * This implementation assumes that requests are
     * {@link TriplePatternFragmentRequest}s.
     *
     * Data sources for other types of {@link LinkedDataFragmentRequest}s must
     * override this method accordingly.
     */
    @Override
    public IFragmentRequestParser getRequestParser()
    {
        return new TPFRequestParser();
    }

    @Override
    public void close() {}
}
