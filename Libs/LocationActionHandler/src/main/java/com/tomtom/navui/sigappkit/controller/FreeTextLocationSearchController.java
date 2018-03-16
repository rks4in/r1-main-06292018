
package com.tomtom.navui.sigappkit.controller;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.input.errhandler.InputDataErrorHandler;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.data.ParsedData;
import com.tomtom.navui.input.parser.data.location.TextLocationData;
import com.tomtom.navui.taskkit.Location2;
import com.tomtom.navui.taskkit.PoiCategory.CategoryType;
import com.tomtom.navui.taskkit.location.GeoCoderTask.LocationListener;
import com.tomtom.navui.taskkit.search.AddressSearchResult;
import com.tomtom.navui.taskkit.search.CitySearchResult;
import com.tomtom.navui.taskkit.search.LocationSearchTask;
import com.tomtom.navui.taskkit.search.LocationSearchTask.SearchError;
import com.tomtom.navui.taskkit.search.LocationSearchTask.SearchInformation;
import com.tomtom.navui.taskkit.search.LocationSearchTask.SearchOptions;
import com.tomtom.navui.taskkit.search.LocationSearchTask.SearchPriority;
import com.tomtom.navui.taskkit.search.LocationSearchTask.SearchQuery;
import com.tomtom.navui.taskkit.search.LocationSearchTask.SearchResultCode;
import com.tomtom.navui.taskkit.search.LocationSearchTask.SearchStringResultListener;
import com.tomtom.navui.taskkit.search.PoiCategorySearchResult;
import com.tomtom.navui.taskkit.search.PoiSearchResult;
import com.tomtom.navui.taskkit.search.SearchResult;
import com.tomtom.navui.util.Log;
import com.tomtom.navui.util.ValueBundle;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class FreeTextLocationSearchController implements LocationSearchController, SearchStringResultListener {

    private static final String TAG = "FreeTextLocationSearchController";

    private static final int MIN_SEARCH_RESULT = 1;

    private final AppContext mAppContext;
    private final LocationListener mLocationListener;
    private InputDataErrorHandler mDataErrorHandler;

    public FreeTextLocationSearchController(final AppContext appContext, final LocationListener locationListener,
        final InputDataErrorHandler errorHandler) {
        this.mAppContext = appContext;
        this.mLocationListener = locationListener;
        this.mDataErrorHandler = errorHandler;

    }

    public AppContext getAppContext() {
        return mAppContext;
    }

    @Override
    public void search(final ParsedData data) {
        final TextLocationData textData = (TextLocationData) data;
        final String textQuery = textData.getTextLocation();
        if (Log.D) {
            Log.d(TAG, "search query" + textQuery);
        }
        final LocationSearchTask searchTask = getAppContext().getTaskKit().newTask(LocationSearchTask.class);
        final SearchQuery searchQuery = new SigNearMeSearchQuery(textQuery);
        searchTask.queryFromSearchString(searchQuery, this);
    }

    @Override
    public void onSearchAddressResult(final SearchQuery searchQuery, final List<AddressSearchResult> locations) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onSearchAddressResult::> searchQuery " + searchQuery);
        }
        if (!locations.isEmpty()) {
            mLocationListener.onLocationsRetrieved(Arrays.asList(locations.get(0).getLocation()));
        }
    }

    @Override
    public void onSearchCityResult(final SearchQuery searchQuery, final List<CitySearchResult> locations) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onSearchCityResult::> searchQuery " + searchQuery);
        }
        if (!locations.isEmpty()) {
            mLocationListener.onLocationsRetrieved(Arrays.asList(locations.get(0).getLocation()));
        }
    }

    @Override
    public void onSearchPoiResult(final SearchQuery searchQuery, final List<PoiSearchResult> locations) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onSearchPoiResult::> searchQuery " + searchQuery);
        }
        if (!locations.isEmpty()) {
            mLocationListener.onLocationsRetrieved(Arrays.asList(locations.get(0).getLocation()));
        }
    }

    @Override
    public void onSearchPoiCategoryResult(final SearchQuery searchQuery, final List<PoiCategorySearchResult> results) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onSearchPoiCategoryResult::> searchQuery " + searchQuery);
        }
        if (!results.isEmpty()) {
            mLocationListener.onLocationsRetrieved(Arrays.asList(results.get(0).getLocation()));
        }
    }

    @Override
    public void onSearchComplete(final SearchQuery searchQuery, final int totalResultsReturned, final SearchResultCode code) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onSearchComplete::> searchQuery " + searchQuery + " totalResultsReturned " + totalResultsReturned
                    + " code " + code);
        }
        final String searchString = searchQuery.getSearchString();
        final String notNullSearchQuery = searchString == null ? "" : searchString;
        if (totalResultsReturned < MIN_SEARCH_RESULT) {
            mDataErrorHandler.handle(new ParseException("Search engine found only " + totalResultsReturned + " a address for query " + searchString,
                    notNullSearchQuery));
        }

    }

    @Override
    public void onSearchInformation(final SearchQuery searchQuery, final SearchInformation informationType, final String informationString,
                                    final SearchResult informationResult) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onSearchInformation::> searchQuery " + searchQuery + " informationType " + informationType
                    + " informationString " + informationString + " informationResult " + informationResult);
        }
    }

    @Override
    public void onSearchError(final SearchQuery searchQuery, final SearchError searchError) {
        if (Log.ENTRY) {
            Log.entry(TAG, "onSearchError::> searchQuery " + searchQuery);
        }
        final String searchString = searchQuery.getSearchString();
        final String notNullSearchQuery = searchString == null ? "" : searchString;
        mDataErrorHandler.handle(new ParseException("Search engine didn't found a address for query " + searchString, notNullSearchQuery));
    }

    /**
     * Search places near me with text query.
     *
     */
    private static final class SigNearMeSearchQuery extends ValueBundle implements SearchQuery {

        private static final int MAX_RESULT_COUNT = 1;
        private static final int DEFAULT_SEARCH_RADIUS = -1;
        private final String mSearchString;
        private final EnumSet<SearchOptions> mSearchOptions;
        private final int mSearchRadius;
        private static final EnumSet<SearchOptions> NEAR_ME_SEARCH_MODE = EnumSet.of(SearchOptions.SEARCH_ADDRESSES, SearchOptions.SEARCH_CITIES, SearchOptions.SEARCH_WHOLE_MAP);

        SigNearMeSearchQuery(final String searchString) {
            mSearchString = searchString;
            final EnumSet<SearchOptions> searchOptions = EnumSet.noneOf(SearchOptions.class);
            searchOptions.addAll(NEAR_ME_SEARCH_MODE);
            mSearchOptions = searchOptions;
            mSearchRadius = DEFAULT_SEARCH_RADIUS;
        }

        @Override
        public SearchPriority getPriority() {
            return SearchPriority.MEDIUM;
        }

        @Override
        public String getSearchString() {
            return mSearchString;
        }

        @Override
        public EnumSet<SearchOptions> getOptions() {
            return mSearchOptions;
        }

        @Override
        public CategoryType getPoiCategory() {
            return null;
        }

        @Override
        public int getSearchRadius() {
            return mSearchRadius;
        }

        @Override
        public String getPoiCategoryName() {
            return null;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("SearchQuery: [SearchString: ");
            builder.append(mSearchString);
            builder.append(" Poi Category: ");
            builder.append("null");
            builder.append(" Search radius: ");
            builder.append(mSearchRadius);
            return builder.toString();
        }

        @Override
        public int getMaxResultCount() {
            return MAX_RESULT_COUNT;
        }

        @Override
        public Location2 getSearchAreaLocation() {
            return null;
        }

        @Override
        public void release() {
            //empty
        }


        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

}
