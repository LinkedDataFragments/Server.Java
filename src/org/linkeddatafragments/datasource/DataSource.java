package org.linkeddatafragments.datasource;

import javax.servlet.http.HttpServletRequest;

import org.linkeddatafragments.config.ConfigReader;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequestImpl;

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
     * This implementation assumes that the given request is a
     * {@link TriplePatternFragmentRequest}.
     *
     * Data sources for other types of {@link LinkedDataFragmentRequest}s must
     * override this method accordingly.
     */
    @Override
    public IFragmentRequestProcessor getRequestProcessor(
            final HttpServletRequest request,
            final ConfigReader config )
    {
        final TriplePatternFragmentRequest r =
                new TriplePatternFragmentRequestImpl( request, config );

        return getRequestProcessor( r );
    }

    @Override
    public void close() {}
}
