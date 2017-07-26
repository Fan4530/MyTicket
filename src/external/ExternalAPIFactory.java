package external;

public class ExternalAPIFactory {
  private static final String DEFAULT_PIPELINE = "ticketmaster";

  // Start different APIs based on the pipeline.
  public static ExternalAPI getExternalAPI(String pipeline) {//used for provide ExternalAPI object,  pipeline = the name of the API
    switch (pipeline) {
      case "ticketmaster":
        return new TicketMasterAPI();
   // case "yelp"
   //     return YelpAPI(); 
      default:
          throw new IllegalArgumentException("Invalid pipeline " + pipeline);
      }
    }

    public static ExternalAPI getExternalAPI() {
      return getExternalAPI(DEFAULT_PIPELINE);
    }
  }
