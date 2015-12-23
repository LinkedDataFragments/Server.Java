package org.linkeddatafragments.fragments.tpf;

import javax.servlet.http.HttpServletRequest;

import org.linkeddatafragments.config.ConfigReader;
import org.linkeddatafragments.fragments.FragmentRequestParserBase;
import org.linkeddatafragments.fragments.IFragmentRequestParser;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 * An {@link IFragmentRequestParser} for {@link TriplePatternFragmentRequest}s.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TPFRequestParser extends FragmentRequestParserBase
{
    @Override
    protected Worker getWorker( final HttpServletRequest httpRequest,
                                final ConfigReader config )
                                               throws IllegalArgumentException
    {
        return new Worker( httpRequest, config );
    }


    static protected class Worker extends FragmentRequestParserBase.Worker
    {   
        public Worker( final HttpServletRequest request,
                       final ConfigReader config )
        {
            super( request, config );
        }

        public LinkedDataFragmentRequest createFragmentRequest()
                                               throws IllegalArgumentException
        {
            return new TriplePatternFragmentRequestImpl( getFragmentURL(),
                                                         getDatasetURL(),
                                                         pageNumberWasRequested,
                                                         pageNumber,
                                                         getSubject(),
                                                         getPredicate(),
                                                         getObject() );
        }

        public String getSubject() {
            return request.getParameter(
                    TriplePatternFragmentRequest.PARAMETERNAME_SUBJ );
        }

        public String getPredicate() {
            return request.getParameter(
                    TriplePatternFragmentRequest.PARAMETERNAME_PRED );
        }

        public String getObject() {
            return request.getParameter(
                    TriplePatternFragmentRequest.PARAMETERNAME_OBJ );
        }

    } // end of class Worker

}
