import java.util.LinkedList;

/**
 * A sliding-window based Rate-Limiter
 */
public class RateLimitedCalculator {
    // A list of long values where each value corresponds to the time(in milliseconds) a request/call was made to the method getSum.
    // This list serves the purpose of our sliding-window.
    // LinkedList was chosen instead of ArrayList because: Adding/Removing (Iterator remove) element in LinkedList -> O(1) whereas in ArrayList it is O(n)
    private LinkedList<Long> requestList;
    // Count of requests already in the list,
    // memory-time trade off: fetching number of items in a linkedlist is O(n) whereas keeping a variable to track this can be done in O(1)
    private int requestCount;
    // Maximum number of requests/calls that can be made in a minute (60,000 milliseconds)
    private int requestLimit;

    /**
     * Constructor to the RateLimitedCalculator class, initializes the sliding window, sets the requestCount to 0 and sets the callLimit
     * @param callLimit the maximum number of calls that can be made to the method getSum
     */
    RateLimitedCalculator(int callLimit){
        this.requestList = new LinkedList<>();
        this.requestCount = 0;
        this.requestLimit = callLimit;
    }

    /**
     * Removes all request that are 60,000 milliseconds older than the current request being made.
     * This makes our request-window a sliding window.
     * @param newRequestTime the time-unit(in milliseconds) of the current request/call to the getSum method
     */
    private void removeOldRequests(long newRequestTime) {
        while(!this.requestList.isEmpty()){
            if(this.requestList.getFirst() >= (newRequestTime-60000)) break;
            this.requestList.removeFirst();
            this.requestCount--;
        }
    }

    /**
     * Adds a new request to the requestList, before adding the request removes all request that are 60,000 milliseconds older than the current request being made.
     * This method is made synchronized to keep it thread safe.
     * @param newRequestTime the time-unit(in milliseconds) of the current request/call to the getSum method
     * @param rateLimit the maximum number of calls that can be made in 60,000 milliseconds sliding window
     * @throws Exception if the new request cannot be added to the window
     */
    private synchronized void add(long newRequestTime, int rateLimit) throws Exception{
        this.removeOldRequests(newRequestTime);
        if(this.requestCount<rateLimit){
            this.requestList.add(newRequestTime);
            this.requestCount++;
            return;
        }
        throw new Exception("Too many requests");
    }

    /**
     * Adds two integers, before adding them the time of call/request is added to the sliding-window
     * @param a
     * @param b
     * @return sum of the two number provided
     * @throws Exception
     */
    int getSum(int a, int b) throws Exception {
        this.add(System.currentTimeMillis(),this.requestLimit);
        return a + b;
    }
}
