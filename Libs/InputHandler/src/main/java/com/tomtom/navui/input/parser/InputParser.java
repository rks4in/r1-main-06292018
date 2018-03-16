
package com.tomtom.navui.input.parser;

import com.tomtom.navui.input.parser.data.ParseResult;

public interface InputParser<T> {

    public boolean accept(T input);

    /**
     * @param input
     * @return
     * @throws ParseException When data provided by external application is not valid but we able to
     *             display this data for user for improve the data
     * @throws {@link ParseFailureException} When external application generate intent with wrong or
     *         invalid data, which we can't parse at all, or general parse error
     */
    ParseResult parse(T input) throws ParseException, ParseFailureException;

}
