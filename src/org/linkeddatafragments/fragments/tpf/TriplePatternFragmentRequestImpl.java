package org.linkeddatafragments.fragments.tpf;

import javax.servlet.http.HttpServletRequest;

import org.linkeddatafragments.config.ConfigReader;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequestBase;

/**
 * An implementation of {@link TriplePatternFragmentRequest} that is based on
 * an {@link HttpServletRequest}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternFragmentRequestImpl
    extends LinkedDataFragmentRequestBase
    implements TriplePatternFragmentRequest
{    
    public TriplePatternFragmentRequestImpl( final HttpServletRequest request,
                                             final ConfigReader config )
    {
        super( request, config );
    }

    @Override
    public String getSubject() {
        return request.getParameter( PARAMETERNAME_SUBJ );
    }

    @Override
    public String getPredicate() {
        return request.getParameter( PARAMETERNAME_PRED );
    }

    @Override
    public String getObject() {
        return request.getParameter( PARAMETERNAME_OBJ );
    }

}
