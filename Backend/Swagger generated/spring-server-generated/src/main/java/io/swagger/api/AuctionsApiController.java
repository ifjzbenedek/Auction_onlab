package io.swagger.api;

import io.swagger.model.AuctionBasic;
import io.swagger.model.AuctionCard;
import io.swagger.model.Bid;
import io.swagger.model.InlineResponse2001;
import io.swagger.model.InlineResponse401;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-02-28T11:41:36.840793882Z[GMT]")
@RestController
public class AuctionsApiController implements AuctionsApi {

    private static final Logger log = LoggerFactory.getLogger(AuctionsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public AuctionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<Bid>> auctionsAuctionIdBidsGet(@Parameter(in = ParameterIn.PATH, description = "ID of the auction to retrieve bids for", required=true, schema=@Schema()) @PathVariable("auctionId") String auctionId
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Bid>>(objectMapper.readValue("[ {\n  \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"itemId\" : 6,\n  \"isWinning\" : true,\n  \"id\" : 0,\n  \"userId\" : 1,\n  \"value\" : 5.962133916683182\n}, {\n  \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"itemId\" : 6,\n  \"isWinning\" : true,\n  \"id\" : 0,\n  \"userId\" : 1,\n  \"value\" : 5.962133916683182\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Bid>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Bid>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Bid> auctionsAuctionIdBidsPost(@Parameter(in = ParameterIn.PATH, description = "ID of the auction to place the bid on", required=true, schema=@Schema()) @PathVariable("auctionId") String auctionId
,@Parameter(in = ParameterIn.DEFAULT, description = "Bid details", schema=@Schema()) @Valid @RequestBody Bid body
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Bid>(objectMapper.readValue("{\n  \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"itemId\" : 6,\n  \"isWinning\" : true,\n  \"id\" : 0,\n  \"userId\" : 1,\n  \"value\" : 5.962133916683182\n}", Bid.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Bid>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Bid>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<InlineResponse2001> auctionsAuctionIdDelete(@Parameter(in = ParameterIn.PATH, description = "ID of the auction to update", required=true, schema=@Schema()) @PathVariable("auctionId") String auctionId
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<InlineResponse2001>(objectMapper.readValue("{\n  \"message\" : \"Auction successfully deleted.\"\n}", InlineResponse2001.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<InlineResponse2001>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<InlineResponse2001>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<AuctionBasic> auctionsAuctionIdGet(@Parameter(in = ParameterIn.PATH, description = "ID of the auction to retrieve", required=true, schema=@Schema()) @PathVariable("auctionId") String auctionId
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<AuctionBasic>(objectMapper.readValue("{\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n}", AuctionBasic.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<AuctionBasic>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<AuctionBasic>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<AuctionBasic> auctionsAuctionIdPut(@Parameter(in = ParameterIn.PATH, description = "ID of the auction to update", required=true, schema=@Schema()) @PathVariable("auctionId") String auctionId
,@Parameter(in = ParameterIn.DEFAULT, description = "", required=true, schema=@Schema()) @Valid @RequestBody AuctionBasic body
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<AuctionBasic>(objectMapper.readValue("{\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n}", AuctionBasic.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<AuctionBasic>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<AuctionBasic>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<AuctionCard>> auctionsGet(@Parameter(in = ParameterIn.QUERY, description = "Filter auctions by status (e.g., ACTIVE, CLOSED)" ,schema=@Schema()) @Valid @RequestParam(value = "status", required = false) String status
,@Parameter(in = ParameterIn.QUERY, description = "Filter auctions by category (e.g., Electronics, Vehicles, Furniture). Multiple categories can be provided, separated by commas." ,schema=@Schema()) @Valid @RequestParam(value = "category", required = false) String category
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<AuctionCard>>(objectMapper.readValue("[ {\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 6.027456183070403,\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"id\" : 0,\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n}, {\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 6.027456183070403,\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"id\" : 0,\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<AuctionCard>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<AuctionCard>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<AuctionBasic>> auctionsMyBiddedAuctionsGet() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<AuctionBasic>>(objectMapper.readValue("[ {\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n}, {\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<AuctionBasic>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<AuctionBasic>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<AuctionBasic>> auctionsMyCreatedAuctionsGet() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<AuctionBasic>>(objectMapper.readValue("[ {\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n}, {\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<AuctionBasic>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<AuctionBasic>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<AuctionBasic>> auctionsMyFollowedAuctionsGet() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<AuctionBasic>>(objectMapper.readValue("[ {\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n}, {\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<AuctionBasic>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<AuctionBasic>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<AuctionBasic> createAuction(@Parameter(in = ParameterIn.DEFAULT, description = "", required=true, schema=@Schema()) @Valid @RequestBody AuctionBasic body
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<AuctionBasic>(objectMapper.readValue("{\n  \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"description\" : \"description\",\n  \"itemState\" : \"itemState\",\n  \"type\" : \"type\",\n  \"userId\" : 6,\n  \"tags\" : \"tags\",\n  \"itemName\" : \"itemName\",\n  \"lastBid\" : 5.637376656633329,\n  \"minStep\" : 2,\n  \"id\" : 0,\n  \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n  \"minimumPrice\" : 5.962133916683182,\n  \"categoryId\" : 1,\n  \"status\" : \"status\",\n  \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n}", AuctionBasic.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<AuctionBasic>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<AuctionBasic>(HttpStatus.NOT_IMPLEMENTED);
    }

}
