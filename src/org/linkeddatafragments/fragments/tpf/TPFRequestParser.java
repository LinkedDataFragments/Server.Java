package org.linkeddatafragments.fragments.tpf;

import javax.servlet.http.HttpServletRequest;

import org.linkeddatafragments.config.ConfigReader;
import org.linkeddatafragments.fragments.FragmentRequestParserBase;
import org.linkeddatafragments.fragments.IFragmentRequestParser;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.util.TriplePatternElementParser;

/**
 * An {@link IFragmentRequestParser} for {@link ITriplePatternFragmentRequest}s.
 *
 * @param <TermType> type for representing RDF terms in triple patterns 
 * @param <VarType> type for representing specific variables in triple patterns
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TPFRequestParser<ConstantTermType,NamedVarType,AnonVarType>
    extends FragmentRequestParserBase
{
    public final TriplePatternElementParser<ConstantTermType,NamedVarType,AnonVarType> elmtParser;

    public TPFRequestParser(
                final TriplePatternElementParser<ConstantTermType,NamedVarType,AnonVarType> elmtParser )
    {
        this.elmtParser = elmtParser;
    }

    @Override
    protected Worker getWorker( final HttpServletRequest httpRequest,
                                final ConfigReader config )
                                               throws IllegalArgumentException
    {
        return new Worker( httpRequest, config );
    }

    protected class Worker extends FragmentRequestParserBase.Worker
    {   
        public Worker( final HttpServletRequest request,
                       final ConfigReader config )
        {
            super( request, config );
        }

        @Override
        public LinkedDataFragmentRequest createFragmentRequest()
                                               throws IllegalArgumentException
        {
            return new TriplePatternFragmentRequestImpl<ConstantTermType,NamedVarType,AnonVarType>(
                                                         getFragmentURL(),
                                                         getDatasetURL(),
                                                         pageNumberWasRequested,
                                                         pageNumber,
                                                         getSubject(),
                                                         getPredicate(),
                                                         getObject() );
        }

        public ITriplePatternElement<ConstantTermType,NamedVarType,AnonVarType> getSubject() {
            return getParameterAsTriplePatternElement(
                    ITriplePatternFragmentRequest.PARAMETERNAME_SUBJ );
        }

        public ITriplePatternElement<ConstantTermType,NamedVarType,AnonVarType> getPredicate() {
            return getParameterAsTriplePatternElement(
                    ITriplePatternFragmentRequest.PARAMETERNAME_PRED );
        }

        public ITriplePatternElement<ConstantTermType,NamedVarType,AnonVarType> getObject() {
            return getParameterAsTriplePatternElement(
                    ITriplePatternFragmentRequest.PARAMETERNAME_OBJ );
        }

        public ITriplePatternElement<ConstantTermType,NamedVarType,AnonVarType>
                   getParameterAsTriplePatternElement( final String paramName )
        {
            final String parameter = request.getParameter( paramName );
            return elmtParser.parseIntoTriplePatternElement( parameter );
        }

    } // end of class Worker

}
