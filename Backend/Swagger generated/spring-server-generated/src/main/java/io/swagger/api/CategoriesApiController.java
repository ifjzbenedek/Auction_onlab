package io.swagger.api;

import io.swagger.model.Category;
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
public class CategoriesApiController implements CategoriesApi {

    private static final Logger log = LoggerFactory.getLogger(CategoriesApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public CategoriesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<Category>> categoriesGet() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Category>>(objectMapper.readValue("[ {\n  \"id\" : 0,\n  \"categoryName\" : \"categoryName\",\n  \"auctions\" : [ {\n    \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"description\" : \"description\",\n    \"itemState\" : \"itemState\",\n    \"type\" : \"type\",\n    \"userId\" : 1,\n    \"tags\" : \"tags\",\n    \"itemName\" : \"itemName\",\n    \"lastBid\" : 2.027123023002322,\n    \"minStep\" : 4,\n    \"bids\" : [ {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    }, {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    } ],\n    \"id\" : 6,\n    \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"minimumPrice\" : 3.616076749251911,\n    \"categoryId\" : 5,\n    \"watchedAuctions\" : [ {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    }, {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    } ],\n    \"status\" : \"status\",\n    \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n  }, {\n    \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"description\" : \"description\",\n    \"itemState\" : \"itemState\",\n    \"type\" : \"type\",\n    \"userId\" : 1,\n    \"tags\" : \"tags\",\n    \"itemName\" : \"itemName\",\n    \"lastBid\" : 2.027123023002322,\n    \"minStep\" : 4,\n    \"bids\" : [ {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    }, {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    } ],\n    \"id\" : 6,\n    \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"minimumPrice\" : 3.616076749251911,\n    \"categoryId\" : 5,\n    \"watchedAuctions\" : [ {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    }, {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    } ],\n    \"status\" : \"status\",\n    \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n  } ]\n}, {\n  \"id\" : 0,\n  \"categoryName\" : \"categoryName\",\n  \"auctions\" : [ {\n    \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"description\" : \"description\",\n    \"itemState\" : \"itemState\",\n    \"type\" : \"type\",\n    \"userId\" : 1,\n    \"tags\" : \"tags\",\n    \"itemName\" : \"itemName\",\n    \"lastBid\" : 2.027123023002322,\n    \"minStep\" : 4,\n    \"bids\" : [ {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    }, {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    } ],\n    \"id\" : 6,\n    \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"minimumPrice\" : 3.616076749251911,\n    \"categoryId\" : 5,\n    \"watchedAuctions\" : [ {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    }, {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    } ],\n    \"status\" : \"status\",\n    \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n  }, {\n    \"expiredDate\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"description\" : \"description\",\n    \"itemState\" : \"itemState\",\n    \"type\" : \"type\",\n    \"userId\" : 1,\n    \"tags\" : \"tags\",\n    \"itemName\" : \"itemName\",\n    \"lastBid\" : 2.027123023002322,\n    \"minStep\" : 4,\n    \"bids\" : [ {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    }, {\n      \"timeStamp\" : \"2000-01-23T04:56:07.000+00:00\",\n      \"itemId\" : 6,\n      \"isWinning\" : true,\n      \"id\" : 0,\n      \"userId\" : 1,\n      \"value\" : 5.962133916683182\n    } ],\n    \"id\" : 6,\n    \"extraTime\" : \"2000-01-23T04:56:07.000+00:00\",\n    \"minimumPrice\" : 3.616076749251911,\n    \"categoryId\" : 5,\n    \"watchedAuctions\" : [ {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    }, {\n      \"auctionId\" : 7,\n      \"id\" : {\n        \"auctionId\" : 5,\n        \"userId\" : 2\n      },\n      \"userId\" : 9\n    } ],\n    \"status\" : \"status\",\n    \"createDate\" : \"2000-01-23T04:56:07.000+00:00\"\n  } ]\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Category>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Category>>(HttpStatus.NOT_IMPLEMENTED);
    }

}
